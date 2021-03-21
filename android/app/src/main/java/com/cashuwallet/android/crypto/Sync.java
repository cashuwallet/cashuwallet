package com.cashuwallet.android.crypto;

import com.raugfer.crypto.base58;
import com.raugfer.crypto.binint;
import com.raugfer.crypto.cbor;
import com.raugfer.crypto.coins;
import com.raugfer.crypto.crc32;
import com.raugfer.crypto.dict;
import com.raugfer.crypto.hashing;
import com.raugfer.crypto.hdwallet;
import com.raugfer.crypto.mnemonic;
import com.raugfer.crypto.pair;
import com.raugfer.crypto.service;
import com.raugfer.crypto.wallet;
import com.cashuwallet.android.Lambda;
import com.cashuwallet.android.db.AppDao;
import com.cashuwallet.android.db.Chain;
import com.cashuwallet.android.db.Multiwallet;
import com.cashuwallet.android.db.Transaction;
import com.cashuwallet.android.db.Unspent;
import com.cashuwallet.android.db.Wallet;
import com.cashuwallet.android.BackgroundTask;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public class Sync {

    private static int INTERVAL = 5;

    private final ExecutorService exec;
    private final AppDao dao;
    private final boolean testnet;

    public Sync(ExecutorService exec, AppDao dao, boolean testnet) {
        this.exec = exec;
        this.dao = dao;
        this.testnet = testnet;
    }

    public boolean isTestnet() {
        return testnet;
    }

    private int time() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    private boolean xlimited(String label) {
        String curve = coins.attr("ecc.curve", label, testnet);
        return curve.equals("ed25519");
    }

    /* public bootstrap routine */

    public void derive(String words, String password, List<Coin> coins, Object[] result, Runnable cont) {
        BackgroundTask.run(exec, () -> derive(words, password, coins, result), (Boolean success) -> cont.run(), false);
    }

    public void bootstrap(Object secret, Runnable cont) {
        BackgroundTask.run(exec, () -> bootstrap(secret), (Boolean success) -> cont.run(), false);
    }

    /* public synchronization routines */

    public void sync(Chain chain, Runnable cont) {
        BackgroundTask.run(exec, () -> sync(chain), (Boolean success) -> cont.run());
    }

    public void sync(Multiwallet multiwallet, Runnable cont) {
        BackgroundTask.run(exec, () -> sync(multiwallet), (Boolean success) -> cont.run());
    }

    public void sync(Wallet wallet, Runnable cont) {
        BackgroundTask.run(exec, () -> sync(wallet), (Boolean success) -> cont.run());
    }

    public void broadcastTransaction(Multiwallet multiwallet, Object[] txn, boolean[] success, Runnable cont) {
        BackgroundTask.run(exec, () -> broadcastTransaction(multiwallet, txn, success), (Boolean _success) -> cont.run());
    }

    /* public listing access */

    public void refresh(Multiwallet multiwallet) {
        multiwallet.refresh(dao);
    }

    public String getUrl(Transaction transaction) {
        Coin coin = transaction.getCoin();
        return coin.getTransactionUrl(transaction.hash, testnet);
    }

    public boolean validateAddress(Multiwallet multiwallet, String address) {
        Coin coin = multiwallet.getCoin();
        String label = coin.getLabel();
        try {
            wallet.address_decode(address, label, testnet);
        } catch (IllegalArgumentException e) {
            if (coin.getLabel().equals("cardano")) return validateCardanoAddress(address);
            return false;
        }
        return true;
    }

    private static boolean validateCardanoAddress(String address) {
        try {
            Object[] struct = (Object[]) cbor.loads(base58.decode(address));
            if (struct.length != 2) throw new IllegalArgumentException("Invalid input");
            cbor.Tag obj = (cbor.Tag) struct[0];
            BigInteger checksum = (BigInteger) struct[1];
            if (obj.tag.compareTo(BigInteger.valueOf(24)) != 0) throw new IllegalArgumentException("Unknown tag");
            BigInteger expected_checksum = binint.b2n(crc32.crc32xmodem((byte[]) obj.value));
            if (checksum.compareTo(expected_checksum) != 0) throw new IllegalArgumentException("Inconsistent checksum");
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    public Chain findChain(String label) { return dao.findChain(label); }

    public Chain findChain(int id) { return dao.findChain(id); }

    public Multiwallet findMultiwallet(int id) {
        return dao.findMultiwallet(id);
    }

    public Wallet findWallet(int id) {
        return dao.findWallet(id);
    }

    public Transaction findTransaction(int id) { return dao.findTransaction(id); }

    public List<Multiwallet> findMultiwallets(int account) {
        return dao.findMultiwallets(account);
    }

    public List<Wallet> findWallets(Multiwallet multiwallet) {
        return dao.findWallets(multiwallet.label, multiwallet.account);
    }

    public Wallet findDepositWallet(Multiwallet multiwallet, boolean change) {
        Coin coin = multiwallet.getCoin();
        int index;
        switch (coin.getMode()) {
            default:
            // get always the first address
            case ACCOUNT: change = false; index = 0; break;
            // get the first virgin address
            case UTXO: index = dao.nextAccountIndex(multiwallet.label, multiwallet.account, change); break;
        }
        return dao.findWallet(multiwallet.label, multiwallet.account, change, index);
    }

    public List<Transaction> findTransactions(Multiwallet multiwallet, int offset, int limit) {
        return dao.findTransactions(multiwallet.label, multiwallet.address, offset, limit);
    }

    public List<Transaction> findTransactions(Wallet wallet, int offset, int limit) {
        return dao.findTransactions(wallet.label, wallet.address, offset, limit);
    }

    public BigInteger estimateFee(Multiwallet multiwallet, BigInteger amount) {
        Coin coin = multiwallet.getCoin();
        String label = coin.getLabel();
        // TODO should find only addresses with UTXOs
        List<Wallet> wallets = dao.findWallets(multiwallet.label, multiwallet.account);
        String[] source_addresses = new String[wallets.size()];
        for (int i = 0; i < source_addresses.length; i++) {
            Wallet wallet = wallets.get(i);
            source_addresses[i] = wallet.address;
        }
        return service.estimate_fee(source_addresses, amount, label, testnet, cb());
    }

    public Object[] createTransaction(Multiwallet multiwallet, String address, BigInteger amount, BigInteger fee) {
        Coin coin = multiwallet.getCoin();
        String label = coin.getLabel();
        // TODO should find only addresses with UTXOs
        List<Wallet> wallets = dao.findWallets(multiwallet.label, multiwallet.account);
        String[] source_addresses = new String[wallets.size()];
        Map<String, String> paths = new HashMap<>();
        for (int i = 0; i < source_addresses.length; i++) {
            Wallet wallet = wallets.get(i);
            source_addresses[i] = wallet.address;
            String path;
            if (xlimited(label)) {
                path = hdwallet.path(wallet.account, label, testnet);
            } else {
                path = hdwallet.path(wallet.index, wallet.change, wallet.account, label, testnet);
            }
            paths.put(source_addresses[i], path);
        }
        Wallet wallet = findDepositWallet(multiwallet, true);
        String change_address = wallet == null ? null : wallet.address;
        pair<byte[][], service.context> r = service.create_rawtxn(source_addresses, address, amount, fee, change_address, label, testnet, cb());
        byte[][] txns = r.l;
        service.context context = r.r;
        Lambda<String, Object> hdcontext = xrootprivatekey -> context.call(source_address -> {
            String path = paths.get(source_address);
            String xprivatekey = hdwallet.xprivatekey_from_xprivatekey(xrootprivatekey, path, label, testnet);
            String privatekey = hdwallet.privatekey_from_xprivatekey(xprivatekey, true, label, testnet);
            return privatekey;
        });
        Object data = context.call(source_address -> source_address);
        return new Object[]{ txns, hdcontext, data };
    }

    public Object[] signTransaction(Multiwallet multiwallet, Object[] txn, Object _secrets) {
        Map<String, Object[]> secrets = (Map<String, Object[]>) _secrets;
        Coin coin = multiwallet.getCoin();
        String label = coin.getLabel();
        String curve = coins.attr("ecc.curve", label, testnet);
        byte[][] txns = (byte[][]) txn[0];
        Lambda<String, Object> hdcontext = (Lambda<String, Object>) txn[1];
        Object data = txn[2];
        Object[] t = secrets.get(curve);
        BigInteger[] k = (BigInteger[]) t[0];
        byte depth = (byte) t[1];
        int fingerprint = (int) t[2];
        int child = (int) t[3];
        String xprivatekeyroot = hdwallet.xprivatekey_encode(k, (byte)depth, fingerprint, child, label, testnet);
        Object params = hdcontext.apply(xprivatekeyroot);
        for (int i = 0; i < txns.length; i++) {
            byte[] rawtxn = txns[i];
            byte[] signed_rawtxn = service.sign_rawtxn(rawtxn, params, label, testnet);
            txns[i] = signed_rawtxn;
        }
        return new Object[]{ txns, data };
    }

    private boolean broadcastTransaction(Multiwallet multiwallet, Object[] txn, boolean[] success) {
        Coin coin = multiwallet.getCoin();
        Service service = coin.getService(testnet);
        byte[][] txns = (byte[][]) txn[0];
        Object data = txn[1];
        for (int i = 0; i < txns.length; i++) {
            if (i > 0) {
                try {
                    Thread.sleep(60*1000);
                } catch (InterruptedException e) {
                    success[0] = false;
                    break;
                }
            }
            byte[] rawtxn = txns[i];
            String txnid = service.broadcast(binint.b2h(rawtxn));
            success[0] = txnid != null;
            if (!success[0]) break;
            int time = time();
            // TODO fix the fee
            BigInteger fee = BigInteger.ZERO;
            BigInteger totalAmount = BigInteger.ZERO;
            if (data instanceof Object[]) {
                for (Object _item : (Object[]) data) {
                    Object[] item = (Object[]) _item;
                    String address = (String) item[0];
                    BigInteger amount = (BigInteger) item[1];
                    Wallet wallet = dao.findWallet(multiwallet.label, address);
                    Transaction transaction = dao.findTransaction(wallet.label, wallet.address, txnid);
                    if (transaction == null) {
                        transaction = new Transaction(wallet.label, wallet.address, txnid, BigInteger.ZERO, fee);
                        dao.createTransaction(transaction);
                        transaction = dao.findTransaction(wallet.label, wallet.address, txnid);
                    }
                    transaction = transaction.incrementAmount(amount.negate());
                    transaction.setBlock(Long.MAX_VALUE);
                    transaction.setTime(time);
                    dao.saveTransaction(transaction);
                    totalAmount = totalAmount.add(amount);
                }
            }
            Transaction transaction = new Transaction(multiwallet.label, multiwallet.address, txnid, totalAmount.negate(), fee);
            transaction.setBlock(Long.MAX_VALUE);
            transaction.setTime(time);
            dao.createTransaction(transaction);
        }
        return true;
    }

    /* crypto library callback */

    private service.callback cb() {
        return new service.callback() {
            private Coin findCoin(String label) {
                for (Iterator<Coin> i = Coins.list(); i.hasNext(); ) {
                    Coin coin = i.next();
                    if (coin.getLabel().equals(label)) return coin;
                }
                return null;
            }

            @Override
            public BigInteger get_fee(String label, boolean testnet) {
                if (testnet != Sync.this.testnet) throw new IllegalStateException();
                Chain chain = dao.findChain(label);
                return chain.getFee();
            }

            @Override
            public BigInteger get_balance(String address, String label, boolean testnet) {
                if (testnet != Sync.this.testnet) throw new IllegalStateException();
                Coin coin = findCoin(label);
                Wallet wallet = dao.findWallet(label, address);
                if (wallet != null) return wallet.getBalance();
                Service service = coin.getService(testnet);
                BigInteger balance = service.getBalance(address);
                if (balance == null) throw new IllegalArgumentException("Cannot get balance");
                return balance;
            }

            @Override
            public dict[] get_utxos(String address, String label, boolean testnet) {
                if (testnet != Sync.this.testnet) throw new IllegalStateException();
                List<Unspent> unspents = dao.findUnspents(label, address);
                dict[] utxos = new dict[unspents.size()];
                for (int i = 0; i < utxos.length; i++) {
                    Unspent unspent = unspents.get(i);
                    dict utxo = new dict();
                    utxo.put("address", address);
                    utxo.put("txnid", unspent.hash);
                    utxo.put("index", BigInteger.valueOf(unspent.index & 0xffffffffL));
                    utxo.put("amount", unspent.getAmount());
                    utxos[i] = utxo;
                }
                return utxos;
            }

            @Override
            public BigInteger get_txn_count(String address, String label, boolean testnet) {
                if (testnet != Sync.this.testnet) throw new IllegalStateException();
                Coin coin = findCoin(label);
                Wallet wallet = dao.findWallet(label, address);
                if (wallet != null) return BigInteger.valueOf(wallet.sequence);
                Service service = coin.getService(testnet);
                long sequence = service.getSequence(address);
                if (sequence == -1) throw new IllegalArgumentException("Cannot get sequence");
                return BigInteger.valueOf(sequence);
            }

            @Override
            public String broadcast_txn(byte[] txn, String label, boolean testnet) {
                Coin coin = findCoin(label);
                Service service = coin.getService(testnet);
                return service.broadcast(binint.b2h(txn));
            }

            @Override
            public Object custom_call(String name, Object arg, String label, boolean testnet) {
                Coin coin = findCoin(label);
                Service service = coin.getService(testnet);
                return service.custom(name, arg);
            }
        };
    }

    /* internal discovery routines */

    private boolean derive(String words, String password, List<Coin> _coins, Object[] result) {
        BigInteger seed = mnemonic.seed(words, password);
        Map<String, Object[]> secrets = new HashMap<>();
        Iterator<Coin> i = _coins != null ? _coins.iterator() : Coins.list();
        while (i.hasNext()) {
            Coin coin = i.next();
            String label = coin.getLabel();
            String curve = coins.attr("ecc.curve", label, testnet);
            if (!secrets.containsKey(curve)) {
                String xprivatekey = hdwallet.xprivatekey_master(seed, label, testnet);
                Object[] parts = hdwallet.xprivatekey_decode(xprivatekey, label, testnet);
                secrets.put(curve, parts);
            }
        }
        result[0] = secrets;
        result[1] = binint.b2n(hashing.blake2b(binint.n2b(seed), 16));
        return true;
    }

    private boolean bootstrap(Object secrets) {
        bootstrapChains();
        bootstrapAccount(secrets, 0);
        return true;
    }

    private void bootstrapChains() {
        for (Iterator<Coin> i = Coins.list(); i.hasNext(); ) {
            Coin coin = i.next();
            dao.createChain(new Chain(coin.getLabel()));
        }
    }

    private void bootstrapAccount(Object _secrets, int account) {
        Map<String, Object[]> secrets = (Map<String, Object[]>) _secrets;
        Map<String, String> cache = new HashMap<>();
        List<Chain> chains = dao.findChains();
        for (Chain chain : chains) {
            Multiwallet multiwallet = dao.findMultiwallet(chain.label, account);
            if (multiwallet == null) {
                Coin coin = chain.getCoin();
                String label = coin.getLabel();
                Coin feeCoin = coin.getFeeCoin();
                String feeLabel = feeCoin.getLabel();
                String curve = coins.attr("ecc.curve", label, testnet);
                String path = hdwallet.path(account, label, testnet);
                String key = (testnet ? feeLabel : "") + ":" + path;
                String xpublickey = cache.get(key);
                if (xpublickey == null) {
                    Object[] t = secrets.get(curve);
                    BigInteger[] k = (BigInteger[]) t[0];
                    byte depth = (byte) t[1];
                    int fingerprint = (int) t[2];
                    int child = (int) t[3];
                    String xprivatekeyroot = hdwallet.xprivatekey_encode(k, depth, fingerprint, child, label, testnet);
                    String xprivatekey = hdwallet.xprivatekey_from_xprivatekey(xprivatekeyroot, path, label, testnet);
                    xpublickey = hdwallet.xpublickey_from_xprivatekey(xprivatekey, label, testnet);
                    cache.put(key, xpublickey);
                }
                multiwallet = new Multiwallet(chain.label, xpublickey, account);
                dao.createMultiwallet(multiwallet);
                triggerAddresses(multiwallet, 1, 1);
            }
        }
    }

    /* internal synchronization implementation */

    private boolean sync(Multiwallet multiwallet) {
        boolean success = true;
        Chain chain = dao.findChain(multiwallet.label);
        long height = chain.getHeight();
        int txnCount = multiwallet.txnCount;
        for (;;) {
            int count = multiwallet.txnCount;
            BigInteger balance = multiwallet.getBalance();
            int total = dao.walletCount(multiwallet.label, multiwallet.account);
            success = syncBalance(multiwallet) && success;
            success = syncSequence(multiwallet) && success;
            success = syncUTXOs(multiwallet) && success;
            success = syncHistory(multiwallet) && success;
            triggerAddresses(multiwallet);
            int _count = multiwallet.txnCount;
            BigInteger _balance = multiwallet.getBalance();
            int _total = dao.walletCount(multiwallet.label, multiwallet.account);
            if (_count == count && _balance.equals(balance) && _total == total) break;
            success = true;
        };
        success = syncHeight(chain) && success;
        success = syncFee(chain) && success;
        if (txnCount != multiwallet.txnCount || height != chain.getHeight()) {
            triggerConfirmations(chain);
            multiwallet.refresh(dao);
        }
        return success;
    }

    private boolean sync(Wallet wallet) {
        boolean success = true;
        Chain chain = dao.findChain(wallet.label);
        long height = chain.getHeight();
        int txnCount = wallet.txnCount;
        success = syncBalance(wallet) && success;
        success = syncSequence(wallet) && success;
        success = syncUTXOs(wallet) && success;
        success = syncHistory(wallet) && success;
        success = syncHeight(chain) && success;
        success = syncFee(chain) && success;
        if (txnCount != wallet.txnCount || height != chain.getHeight()) {
            triggerConfirmations(chain);
            wallet.refresh(dao);
        }
        return success;
    }

    private boolean sync(Chain chain) {
        boolean success = true;
        long height = chain.getHeight();
        success = syncHeight(chain) && success;
        success = syncFee(chain) && success;
        if (height != chain.getHeight()) {
            triggerConfirmations(chain);
        }
        return success;
    }

    private boolean syncBalance(Multiwallet multiwallet) {
        // TODO figure out a better approach
        boolean success = true;
        Wallet depositWallet1 = findDepositWallet(multiwallet, false);
        Wallet depositWallet2 = findDepositWallet(multiwallet, true);
        List<Wallet> wallets = dao.findWallets(multiwallet.label, multiwallet.account);
        Collections.sort(wallets, (Wallet wallet1, Wallet wallet2) -> wallet1.balTime - wallet2.balTime);
        BigInteger balance = BigInteger.ZERO;
        for (Wallet wallet : wallets) {
            if (wallet.address.equals(depositWallet1.address) || wallet.address.equals(depositWallet2.address) || dao.pendingTransactionCount(wallet.label, wallet.address) > 0) {
                success = syncBalance(wallet) && success;
            }
            balance = balance.add(wallet.getBalance());
        }
        multiwallet.setBalance(balance);
        dao.saveMultiwallet(multiwallet);
        return success;
    }

    private boolean syncHistory(Multiwallet multiwallet) {
        // TODO figure out a better approach
        boolean success = true;
        Wallet depositWallet1 = findDepositWallet(multiwallet, false);
        Wallet depositWallet2 = findDepositWallet(multiwallet, true);
        List<Wallet> wallets = dao.findWallets(multiwallet.label, multiwallet.account);
        Collections.sort(wallets, (Wallet wallet1, Wallet wallet2) -> wallet1.txnTime - wallet2.txnTime);
        for (Wallet wallet : wallets) {
            if (wallet.address.equals(depositWallet1.address) || wallet.address.equals(depositWallet2.address) || dao.pendingTransactionCount(wallet.label, wallet.address) > 0) {
                success = syncHistory(wallet) && success;
            }
        }
        multiwallet.txnCount = dao.transactionCount(multiwallet.label, multiwallet.address);
        dao.saveMultiwallet(multiwallet);
        return success;
    }

    private boolean syncUTXOs(Multiwallet multiwallet) {
        // TODO figure out a better approach
        boolean success = true;
        Wallet depositWallet1 = findDepositWallet(multiwallet, false);
        Wallet depositWallet2 = findDepositWallet(multiwallet, true);
        List<Wallet> wallets = dao.findWallets(multiwallet.label, multiwallet.account);
        Collections.sort(wallets, (Wallet wallet1, Wallet wallet2) -> wallet1.txnTime - wallet2.txnTime);
        for (Wallet wallet : wallets) {
            if (wallet.address.equals(depositWallet1.address) || wallet.address.equals(depositWallet2.address) || dao.pendingTransactionCount(wallet.label, wallet.address) > 0) {
                success = syncUTXOs(wallet) && success;
            }
        }
        return success;
    }

    private boolean syncSequence(Multiwallet multiwallet) {
        // TODO figure out a better approach
        boolean success = true;
        Wallet depositWallet1 = findDepositWallet(multiwallet, false);
        Wallet depositWallet2 = findDepositWallet(multiwallet, true);
        List<Wallet> wallets = dao.findWallets(multiwallet.label, multiwallet.account);
        Collections.sort(wallets, (Wallet wallet1, Wallet wallet2) -> wallet1.txnTime - wallet2.txnTime);
        for (Wallet wallet : wallets) {
            if (wallet.address.equals(depositWallet1.address) || wallet.address.equals(depositWallet2.address) || dao.pendingTransactionCount(wallet.label, wallet.address) > 0) {
                success = syncSequence(wallet) && success;
            }
        }
        return success;
    }

    private boolean syncHeight(Chain chain) {
        Coin coin = chain.getCoin();
        Service service = coin.getService(testnet);
        int blockTime = coin.getBlockTime();

        int time = time();
        if (chain.heiTime + blockTime/4 > time) return true;
        if (chain.heiLastSync + INTERVAL > time) return false;
        long height = service.getHeight();
        time = time();
        boolean success = height != -1;

        if (success) {
            chain.setHeight(height);
            chain.heiTime = time;
        }
        chain.heiLastSync = time;
        dao.saveChain(chain);
        return success;
    }

    private boolean syncFee(Chain chain) {
        Coin coin = chain.getCoin();
        Service service = coin.getService(testnet);
        int blockTime = coin.getBlockTime();

        int time = time();
        if (chain.feeTime + blockTime/4 > time) return true;
        if (chain.feeLastSync + INTERVAL > time) return false;
        BigInteger fee = service.getFeeEstimate();
        time = time();
        boolean success = fee != null;

        if (success) {
            chain.setFee(fee);
            chain.feeTime = time;
        }
        chain.feeLastSync = time;
        dao.saveChain(chain);
        return success;
    }

    private boolean syncBalance(Wallet wallet) {
        Coin coin = wallet.getCoin();
        Service service = coin.getService(testnet);
        int blockTime = coin.getBlockTime();

        int time = time();
        if (wallet.balTime + blockTime/4 > time) return true;
        if (wallet.balLastSync + INTERVAL > time) return false;
        BigInteger balance = service.getBalance(wallet.address);
        time = time();
        boolean success = balance != null;

        if (success) {
            wallet.setBalance(balance);
            wallet.balTime = time;
        }
        wallet.balLastSync = time;
        dao.saveWallet(wallet);
        return success;
    }

    private boolean syncHistory(Wallet wallet) {
        Coin coin = wallet.getCoin();
        Service service = coin.getService(testnet);
        int blockTime = coin.getBlockTime();

        int time = time();
        if (wallet.txnTime + blockTime/4 > time) return true;
        if (wallet.txnLastSync + INTERVAL > time()) return false;
        long height = dao.transactionHeight(wallet.label, wallet.address);
        List<Service.HistoryItem> items = service.getHistory(wallet.address, height);
        time = time();
        boolean success = items != null;

        if (success) {
            Multiwallet multiwallet = dao.findMultiwallet(wallet.label, wallet.account);
            for (Service.HistoryItem item : items) {
                boolean newrecord = false;
                Transaction transaction = dao.findTransaction(wallet.label, wallet.address, item.hash);
                if (transaction == null) {
                    transaction = new Transaction(wallet.label, wallet.address, item.hash, item.amount, item.fee);
                    dao.createTransaction(transaction);
                    transaction = dao.findTransaction(wallet.label, wallet.address, item.hash);
                    newrecord = true;
                }
                transaction.setBlock(item.block);
                transaction.setTime(item.time);
                dao.saveTransaction(transaction);
                transaction = dao.findTransaction(multiwallet.label, multiwallet.address, item.hash);
                if (transaction == null) {
                    transaction = new Transaction(multiwallet.label, multiwallet.address, item.hash, BigInteger.ZERO, item.fee);
                    dao.createTransaction(transaction);
                    transaction = dao.findTransaction(multiwallet.label, multiwallet.address, item.hash);
                }
                if (newrecord) {
                    transaction = transaction.incrementAmount(item.amount);
                }
                transaction.setBlock(item.block);
                transaction.setTime(item.time);
                dao.saveTransaction(transaction);
            }
            wallet.txnCount = dao.transactionCount(wallet.label, wallet.address);
            wallet.txnTime = time;
        }
        wallet.txnLastSync = time;
        dao.saveWallet(wallet);
        return success;
    }

    private boolean syncUTXOs(Wallet wallet) {
        Coin coin = wallet.getCoin();
        Service service = coin.getService(testnet);
        int blockTime = coin.getBlockTime();

        int time = time();
        if (wallet.unsTime + blockTime/4 > time) return true;
        if (wallet.unsLastSync + INTERVAL > time) return false;
        List<Service.UTXO> utxos = service.getUTXOs(wallet.address);
        time = time();
        boolean success = utxos != null;

        if (success) {
            List<Unspent> unspents = new ArrayList<>();
            for (Service.UTXO utxo : utxos) {
                unspents.add(new Unspent(wallet.label, wallet.address, utxo.hash, utxo.index, utxo.amount));
            }
            dao.deleteUnspents(wallet.label, wallet.address);
            dao.insertUnspents(unspents);
            wallet.unsTime = time;
        }
        wallet.unsLastSync = time;
        dao.saveWallet(wallet);
        return success;
    }

    private boolean syncSequence(Wallet wallet) {
        Coin coin = wallet.getCoin();
        Service service = coin.getService(testnet);
        int blockTime = coin.getBlockTime();

        int time = time();
        if (wallet.seqTime + blockTime/4 > time) return true;
        if (wallet.seqLastSync + INTERVAL > time) return false;
        long sequence = service.getSequence(wallet.address);
        time = time();
        boolean success = sequence != -1;

        if (success) {
            wallet.sequence = sequence;
            wallet.seqTime = time;
        }
        wallet.seqLastSync = time;
        dao.saveWallet(wallet);
        return success;
    }

    private void triggerConfirmations(Chain chain) {
        Coin coin = chain.getCoin();
        int minConf = coin.getMinConf();
        long height = chain.getHeight();
        long limit = height - (minConf-1);
        Set<String> addresses = new HashSet<>();
        List<Transaction> transactions =  dao.findPendingTransactions(chain.label);
        for (Transaction transaction : transactions) {
            long block = transaction.getBlock();
            boolean confirmed = limit >= block;
            if (confirmed) {
                transaction.confirmed = true;
                dao.saveTransaction(transaction);
            }
            addresses.add(transaction.address);
        }
        for (String address : addresses) {
            boolean confirmed = dao.pendingTransactionCount(chain.label, address) == 0;
            Wallet wallet = dao.findWallet(chain.label, address);
            if (wallet != null) {
                if (confirmed != wallet.confirmed) {
                    wallet.confirmed = confirmed;
                    dao.saveWallet(wallet);
                }
            }
            Multiwallet multiwallet = dao.findMultiwallet(chain.label, address);
            if (multiwallet != null) {
                if (confirmed != multiwallet.confirmed) {
                    multiwallet.confirmed = confirmed;
                    dao.saveMultiwallet(multiwallet);
                }
            }
        }
    }

    private void triggerAddresses(Multiwallet multiwallet) {
        triggerAddresses(multiwallet, 20, 1);
    }

    private void triggerAddresses(Multiwallet multiwallet, int limit1, int limit2) {
        Coin coin = multiwallet.getCoin();
        String address = multiwallet.address;
        int account = multiwallet.account;
        switch (coin.getMode()) {
            default:
            case ACCOUNT:
                triggerAddress(coin, address, account, false, 0);
                break;
            case UTXO:
                if (xlimited(coin.getLabel())) {
                    triggerAddress(coin, address, account, false, 0);
                } else {
                    triggerAddresses(coin, address, account, false, limit1);
                    triggerAddresses(coin, address, account, true, limit2);
                }
                break;
        }
    }

    private Wallet triggerAddress(Coin coin, String xpublickeyroot, int account, boolean change, int index) {
        String label = coin.getLabel();
        Wallet w = dao.findWallet(label, account, change, index);
        if (w == null) {
            String xpublickey;
            if (xlimited(label)) {
                if (change || index != 0) throw new IllegalArgumentException("Invalid path");
                xpublickey = xpublickeyroot;
            } else {
                String path = "K/" + (change ? 1 : 0) + "/" + index;
                xpublickey = hdwallet.xpublickey_from_xpublickey(xpublickeyroot, path, label, testnet);
            }
            String publickey = hdwallet.publickey_from_xpublickey(xpublickey, true, label, testnet);
            String address = wallet.address_from_publickey(publickey, label, testnet);
            w = new Wallet(label, address, account, change, index);
            dao.createWallet(w);
        }
        return w;
    }

    private void triggerAddresses(Coin coin, String xpublickeyroot, int account, boolean change, int limit) {
        int baseIndex = 0;
        int gap = 0;
        while (gap < limit) {
            int index = baseIndex + gap;
            Wallet w = triggerAddress(coin, xpublickeyroot, account, change, index);
            gap++;
            if (w.txnCount > 0) {
                baseIndex += gap;
                gap = 0;
            }
        }
    }

}
