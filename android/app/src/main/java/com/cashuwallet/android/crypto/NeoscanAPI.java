package com.cashuwallet.android.crypto;

import com.cashuwallet.android.Network;
import com.raugfer.crypto.binint;
import com.raugfer.crypto.coins;
import com.raugfer.crypto.dict;
import com.raugfer.crypto.transaction;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NeoscanAPI implements Service {

    private final String baseUrl;
    private final boolean testnet;

    public NeoscanAPI(String baseUrl, boolean testnet) {
        this.baseUrl = baseUrl;
        this.testnet = testnet;
    }

    @Override
    public long getHeight() {
        try {
            String url = baseUrl + "get_height";
            JSONObject data = new JSONObject(Network.urlFetch(url));
            return data.getLong("height");
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public BigInteger getFeeEstimate() {
        return BigInteger.ZERO;
    }

    @Override
    public BigInteger getBalance(String address) {
        try {
            String url = baseUrl + "get_balance/" + address;
            JSONObject data = new JSONObject(Network.urlFetch(url));
            JSONArray balances = data.getJSONArray("balance");
            for (int i = 0; i < balances.length(); i++) {
                JSONObject item = balances.getJSONObject(i);
                String asset = item.getString("asset");
                if (asset.equals("NEO")) {
                    return BigInteger.valueOf((long) item.getDouble("amount"));
                }
            }
            return BigInteger.ZERO;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<HistoryItem> getHistory(String address, long height) {
        try {
            String neo_asset = coins.attr("asset", "neo", testnet);
            List<HistoryItem> list = new ArrayList<>();
            Map<String, HistoryItem> map = new HashMap<>();
            int page = 1;
            int pages = 1;
            while (page <= pages) {
                String url = baseUrl + "get_address_abstracts/" + address + "/" + page;
                JSONObject data = new JSONObject(Network.urlFetch(url));
                pages = data.getInt("total_pages");
                JSONArray items = data.getJSONArray("entries");
                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    String asset = item.getString("asset");
                    if (asset.equals(neo_asset)) {
                        String hash = item.getString("txid");
                        long block = item.optLong("block_height", Long.MAX_VALUE);
                        if (block == -1) block = Long.MAX_VALUE;
                        if (block < height) return list;
                        int time = item.optInt("time", 0);
                        BigInteger value = new BigInteger(item.getString("amount"));
                        String source = item.getString("address_from");
                        String target = item.getString("address_to");
                        BigInteger amount = BigInteger.ZERO;
                        if (address.equals(source)) amount = amount.subtract(value);
                        if (address.equals(target)) amount = amount.add(value);
                        BigInteger fee = BigInteger.ZERO;
                        HistoryItem o = map.get(hash);
                        if (o == null) {
                            o = new HistoryItem();
                            o.hash = hash;
                            o.time = time;
                            o.block = block;
                            o.amount = amount;
                            o.fee = fee;
                            list.add(o);
                            map.put(hash, o);
                        } else {
                            o.amount = amount.add(amount);
                        }
                    }
                }
                page++;
            }
            return list;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<UTXO> getUTXOs(String address) {
        try {
            List<UTXO> list = new ArrayList<>();
            String url = baseUrl + "get_balance/" + address;
            JSONObject data = new JSONObject(Network.urlFetch(url));
            JSONArray balances = data.getJSONArray("balance");
            for (int i = 0; i < balances.length(); i++) {
                JSONObject item = balances.getJSONObject(i);
                String asset = item.getString("asset");
                if (asset.equals("NEO")) {
                    JSONArray unspent = item.optJSONArray("unspent");
                    if (unspent != null) {
                        for (int j = 0; j < unspent.length(); j++) {
                            JSONObject output = unspent.getJSONObject(j);
                            String hash = output.getString("txid");
                            int index = output.getInt("n");
                            BigInteger amount = BigInteger.valueOf((long) output.getDouble("value"));
                            UTXO o = new UTXO();
                            o.hash = hash;
                            o.index = index;
                            o.amount = amount;
                            list.add(o);
                        }
                    }
                    break;
                }
            }
            return list;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public long getSequence(String address) {
        return 0;
    }

    @Override
    public String broadcast(String _transaction) {
        try {
            byte[] txn = binint.h2b(_transaction);
            dict fields = transaction.transaction_decode(txn, "neo", testnet);
            fields.del("scripts");
            txn = transaction.transaction_encode(fields, "neo", testnet);
            String txnid = transaction.txnid(txn, "neo", testnet);
            String url = baseUrl + "get_all_nodes";
            JSONArray nodes = new JSONArray(Network.urlFetch(url));
            long serverHeight = -1;
            String serverUrl = null;
            for (int i = 0; i < nodes.length(); i++) {
                JSONObject node = nodes.getJSONObject(i);
                long height = node.getLong("height");
                if (height > serverHeight) {
                    serverHeight = height;
                    serverUrl = node.getString("url");
                }
            }
            if (serverUrl == null) throw new IllegalArgumentException("Server unavailable");
            String content = "{\"jsonrpc\":\"2.0\",\"method\":\"sendrawtransaction\",\"params\":[\"" + _transaction + "\"],\"id\": 1}";
            JSONObject data = new JSONObject(Network.urlFetch(serverUrl, content));
            boolean result = data.getBoolean("result");
            if (!result) throw new IllegalArgumentException("Broadcast failure");
            return txnid;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Object custom(String name, Object arg) {
        return null;
    }

}
