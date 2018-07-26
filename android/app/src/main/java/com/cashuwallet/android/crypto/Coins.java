package com.cashuwallet.android.crypto;

import com.raugfer.crypto.coins;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class Coins {

    private static final Coin bitcoin = new Bitcoin();
    private static final Coin bitcoincash = new BitcoinCash();
    private static final Coin bitcoingold = new BitcoinGold();
    private static final Coin dash = new Dash();
    private static final Coin dogecoin = new Dogecoin();
    private static final Coin ethereum = new Ethereum();
    private static final Coin ethereumclassic = new EthereumClassic();
    private static final Coin lisk = new Lisk();
    private static final Coin litecoin = new Litecoin();
    private static final Coin qtum = new Qtum();
    private static final Coin ripple = new Ripple();
    private static final Coin zcash = new Zcash();
    private static final Coin _0x = new _0x();
    private static final Coin augur = new Augur();
    private static final Coin basicattentiontoken = new BasicAttentionToken();
    private static final Coin eos = new EOS();
    private static final Coin golem = new Golem();
    private static final Coin omisego = new OmiseGO();
    private static final Coin status = new Status();

    private static final Map<String, Coin> registry = new HashMap<>();

    static {
        registry.put(bitcoin.getCode(), bitcoin);
        registry.put(bitcoincash.getCode(), bitcoincash);
        registry.put(bitcoingold.getCode(), bitcoingold);
        registry.put(dash.getCode(), dash);
        registry.put(dogecoin.getCode(), dogecoin);
        registry.put(ethereum.getCode(), ethereum);
        registry.put(ethereumclassic.getCode(), ethereumclassic);
        registry.put(lisk.getCode(), lisk);
        registry.put(litecoin.getCode(), litecoin);
        registry.put(qtum.getCode(), qtum);
        registry.put(ripple.getCode(), ripple);
        registry.put(zcash.getCode(), zcash);
        registry.put(_0x.getCode(), _0x);
        registry.put(augur.getCode(), augur);
        registry.put(basicattentiontoken.getCode(), basicattentiontoken);
        registry.put(eos.getCode(), eos);
        registry.put(golem.getCode(), golem);
        registry.put(omisego.getCode(), omisego);
        registry.put(status.getCode(), status);
    }

    public static Coin findCoin(String code) {
        return registry.get(code);
    }

    public static Iterator<Coin> list() {
        return registry.values().iterator();
    }

    private static abstract class AbstractCoin implements Coin {

        @Override
        public final AddressMode getMode() {
            String mode = coins.attr("address.mode", getLabel());
            switch (mode) {
                case "utxo": return AddressMode.UTXO;
                case "account": return AddressMode.ACCOUNT;
                default: throw new IllegalStateException("Unknown mode");
            }
        }

        @Override
        public final int getDecimals() {
            return coins.attr("decimals", getLabel());
        }

        @Override
        public final int getBlockTime() {
            return coins.attr("block.time", getLabel());
        }

        @Override
        public final int getMinConf() {
            return coins.attr("confirmations", getLabel());
        }

        @Override
        public Coin getFeeCoin() {
            return this;
        }
    }

    private static class Bitcoin extends AbstractCoin {
        @Override
        public String getName() {
            return "Bitcoin";
        }

        @Override
        public String getLabel() {
            return "bitcoin";
        }

        @Override
        public String getCode() {
            return "BTC";
        }

        @Override
        public String getSymbol() {
            return "฿";
        }

        @Override
        public Service getService(boolean testnet) {
            if (testnet) {
                return new Service.Multi(new Service[]{
                    new InsightAPI("https://test-insight.bitpay.com/api/", getMinConf()),
                    new InsightAPI("https://testnet.blockexplorer.com/api/", getMinConf()),
                    new InsightAPI("https://tbtc.blockdozer.com/insight-api/", getMinConf()),
                    new BlockcypherAPI("https://api.blockcypher.com/v1/btc/test3", getMinConf()),
                    new SochainAPI("https://chain.so/api/v2/*/BTCTEST"),
                });
            } else {
                return new Service.Multi(new Service[]{
                    new InsightAPI("https://insight.bitpay.com/api/", getMinConf()),
                    new InsightAPI("https://blockexplorer.com/api/", getMinConf()),
                    new InsightAPI("https://btc.blockdozer.com/insight-api/", getMinConf()),
                    new BlockcypherAPI("https://api.blockcypher.com/v1/btc/main", getMinConf()),
                    new SochainAPI("https://chain.so/api/v2/*/BTC"),
                });
            }
        }

        @Override
        public String getTransactionUrl(String hash, boolean testnet) {
            if (testnet) {
                return "https://testnet.blockchain.info/tx/" + hash;
            } else {
                return "https://blockchain.info/tx/" + hash;
            }
        }
    }

    private static class BitcoinCash extends AbstractCoin {
        @Override
        public String getName() {
            return "Bitcoin Cash";
        }

        @Override
        public String getLabel() {
            return "bitcoincash";
        }

        @Override
        public String getCode() {
            return "BCH";
        }

        @Override
        public String getSymbol() {
            return null;
        }

        @Override
        public Service getService(boolean testnet) {
            if (testnet) {
                return new Service.Multi(new Service[]{
                    new InsightAPI("https://tbch.blockdozer.com/insight-api/", getMinConf()),
                    new InsightAPI("https://test-bch-insight.bitpay.com/api/", getMinConf()),
                });
            } else {
                return new Service.Multi(new Service[]{
                    new InsightAPI("https://bch.blockdozer.com/insight-api/", getMinConf()),
                    new InsightAPI("https://bitcoincash.blockexplorer.com/api/", getMinConf()),
                    new InsightAPI("https://bch-insight.bitpay.com/api/", getMinConf()),
                });
            }
        }

        @Override
        public String getTransactionUrl(String hash, boolean testnet) {
            if (testnet) {
                return "https://tbch.blockdozer.com/tx/" + hash;
            } else {
                return "https://bch.blockdozer.com/tx/" + hash;
            }
        }
    }

    private static class BitcoinGold extends AbstractCoin {
        @Override
        public String getName() {
            return "Bitcoin Gold";
        }

        @Override
        public String getLabel() {
            return "bitcoingold";
        }

        @Override
        public String getCode() {
            return "BTG";
        }

        @Override
        public String getSymbol() {
            return null;
        }

        @Override
        public Service getService(boolean testnet) {
            if (testnet) {
                return new InsightAPI("https://test-explorer.bitcoingold.org/insight-api/", getMinConf());
            } else {
                return new InsightAPI("https://explorer.bitcoingold.org/insight-api/", getMinConf());
            }
        }

        @Override
        public String getTransactionUrl(String hash, boolean testnet) {
            if (testnet) {
                return "https://test-explorer.bitcoingold.org/insight/tx/" + hash;
            } else {
                return "https://explorer.bitcoingold.org/insight/tx/" + hash;
            }
        }
    }

    private static class Dash extends AbstractCoin {
        @Override
        public String getName() {
            return "Dash";
        }

        @Override
        public String getLabel() {
            return "dash";
        }

        @Override
        public String getCode() {
            return "DASH";
        }

        @Override
        public String getSymbol() {
            return null;
        }

        @Override
        public Service getService(boolean testnet) {
            if (testnet) {
                return new Service.Multi(new Service[]{
                    new InsightAPI("https://testnet-insight.dashevo.org/insight-api-dash/", getMinConf()),
                    new SochainAPI("https://chain.so/api/v2/*/DASHTEST"),
                });
            } else {
                return new Service.Multi(new Service[]{
                    new InsightAPI("https://insight.dash.org/insight-api-dash/", getMinConf()),
                    new BlockcypherAPI("https://api.blockcypher.com/v1/dash/main", getMinConf()),
                    new SochainAPI("https://chain.so/api/v2/*/DASH"),
                });
            }
        }

        @Override
        public String getTransactionUrl(String hash, boolean testnet) {
            if (testnet) {
                return "https://testnet-insight.dashevo.org/insight/tx/" + hash;
            } else {
                return "https://insight.dash.org/insight/tx/" + hash;
            }
        }
    }

    private static class Dogecoin extends AbstractCoin {
        @Override
        public String getName() {
            return "Dogecoin";
        }

        @Override
        public String getLabel() {
            return "dogecoin";
        }

        @Override
        public String getCode() {
            return "DOGE";
        }

        @Override
        public String getSymbol() {
            return "Ð";
        }

        @Override
        public Service getService(boolean testnet) {
            if (testnet) {
                return new SochainAPI("https://chain.so/api/v2/*/DOGETEST");
            } else {
                return new Service.Multi(new Service[]{
                        new DogechainAPI("https://dogechain.info/api/v1/"),
                        new BlockcypherAPI("https://api.blockcypher.com/v1/doge/main", getMinConf()),
                        new SochainAPI("https://chain.so/api/v2/*/DOGE"),
                });
            }
        }

        @Override
        public String getTransactionUrl(String hash, boolean testnet) {
            if (testnet) {
                return "https://chain.so/tx/DOGETEST/" + hash;
            } else {
                return "https://chain.so/tx/DOGE/" + hash;
            }
        }
    }

    private static class Ethereum extends AbstractCoin {
        @Override
        public String getName() {
            return "Ethereum";
        }

        @Override
        public String getLabel() {
            return "ethereum";
        }

        @Override
        public String getCode() {
            return "ETH";
        }

        @Override
        public String getSymbol() {
            return "Ξ";
        }

        @Override
        public Service getService(boolean testnet) {
            if (testnet) {
                return new EtherscanAPI("https://api-ropsten.etherscan.io/api");
            } else {
                return new Service.Multi(new Service[]{
                        new EtherscanAPI("https://api.etherscan.io/api"),
                        new BlockcypherAPI("https://api.blockcypher.com/v1/eth/main"),
                });
            }
        }

        @Override
        public String getTransactionUrl(String hash, boolean testnet) {
            if (testnet) {
                return "https://ropsten.etherscan.io/tx/" + hash;
            } else {
                return "https://etherscan.io/tx/" + hash;
            }
        }
    }

    private static class EthereumClassic extends AbstractCoin {
        @Override
        public String getName() {
            return "Ethereum Classic";
        }

        @Override
        public String getLabel() {
            return "ethereumclassic";
        }

        @Override
        public String getCode() {
            return "ETC";
        }

        @Override
        public String getSymbol() {
            return null;
        }

        @Override
        public Service getService(boolean testnet) {
            if (testnet) {
                return new Web3rpcAPI("https://web3.gastracker.io/morden");
            } else {
                return new Service.Multi(new Service[]{
                    new GastrackerAPI("https://api.gastracker.io/v1/"),
                    new Web3rpcAPI("https://web3.gastracker.io/"),
                });
            }
        }

        @Override
        public String getTransactionUrl(String hash, boolean testnet) {
            if (testnet) {
                return "http://mordenexplorer.ethertrack.io/" + hash;
            } else {
                return "http://etherhub.io/tx/" + hash;
            }
        }
    }

    private static class Lisk extends AbstractCoin {
        @Override
        public String getName() {
            return "Lisk";
        }

        @Override
        public String getLabel() {
            return "lisk";
        }

        @Override
        public String getCode() {
            return "LSK";
        }

        @Override
        public String getSymbol() {
            return null;
        }

        @Override
        public Service getService(boolean testnet) {
            if (testnet) {
                return new LiskioAPI("https://testnet.lisk.io/api/", "1.0", true);
            } else {
                return new Service.Multi(new Service[]{
                    new LiskioAPI("https://node08.lisk.io/api/", "0.9", false),
                    new LiskioAPI("https://node07.lisk.io/api/", "0.9", false),
                    new LiskioAPI("https://node06.lisk.io/api/", "0.9", false),
                    new LiskioAPI("https://node05.lisk.io/api/", "0.9", false),
                    new LiskioAPI("https://node04.lisk.io/api/", "0.9", false),
                    new LiskioAPI("https://node03.lisk.io/api/", "0.9", false),
                    new LiskioAPI("https://node02.lisk.io/api/", "0.9", false),
                    new LiskioAPI("https://node01.lisk.io/api/", "0.9", false),
                });
            }
        }

        @Override
        public String getTransactionUrl(String hash, boolean testnet) {
            if (testnet) {
                return "https://testnet-explorer.lisk.io/tx/" + hash;
            } else {
                return "https://explorer.lisk.io/tx/" + hash;
            }
        }
    }

    private static class Litecoin extends AbstractCoin {
        @Override
        public String getName() {
            return "Litecoin";
        }

        @Override
        public String getLabel() {
            return "litecoin";
        }

        @Override
        public String getCode() {
            return "LTC";
        }

        @Override
        public String getSymbol() {
            return "Ł";
        }

        @Override
        public Service getService(boolean testnet) {
            if (testnet) {
                return new Service.Multi(new Service[]{
                    new InsightAPI("https://testnet.litecore.io/api/", getMinConf()),
                    new SochainAPI("https://chain.so/api/v2/*/LTCTEST"),
                });
            } else {
                return new Service.Multi(new Service[]{
                    new InsightAPI("https://insight.litecore.io/api/", getMinConf()),
                    new BlockcypherAPI("https://api.blockcypher.com/v1/ltc/main", getMinConf()),
                    new SochainAPI("https://chain.so/api/v2/*/LTC"),
                });
            }
        }

        @Override
        public String getTransactionUrl(String hash, boolean testnet) {
            if (testnet) {
                return "https://testnet.litecore.io/tx/" + hash;
            } else {
                return "https://insight.litecore.io/tx/" + hash;
            }
        }
    }

    private static class Qtum extends AbstractCoin {
        @Override
        public String getName() {
            return "Qtum";
        }

        @Override
        public String getLabel() {
            return "qtum";
        }

        @Override
        public String getCode() {
            return "QTUM";
        }

        @Override
        public String getSymbol() {
            return null;
        }

        @Override
        public Service getService(boolean testnet) {
            if (testnet) {
                return new InsightAPI("https://testnet.qtum.org/insight-api/", getMinConf());
            } else {
                return new InsightAPI("https://explorer.qtum.org/insight-api/", getMinConf());
            }
        }

        @Override
        public String getTransactionUrl(String hash, boolean testnet) {
            if (testnet) {
                return "https://testnet.qtum.org/tx/" + hash;
            } else {
                return "https://explorer.qtum.org/tx/" + hash;
            }
        }
    }

    private static class Ripple extends AbstractCoin {
        @Override
        public String getName() {
            return "Ripple";
        }

        @Override
        public String getLabel() {
            return "ripple";
        }

        @Override
        public String getCode() {
            return "XRP";
        }

        @Override
        public String getSymbol() {
            return "Ʀ";
        }

        @Override
        public Service getService(boolean testnet) {
            if (testnet) {
                return new RippledAPI("https://s.altnet.rippletest.net:51234");
            } else {
                return new RippledAPI("https://s1.ripple.com:51234");
            }
        }

        @Override
        public String getTransactionUrl(String hash, boolean testnet) {
            if (testnet) {
                return null; // no known public service available
            } else {
                return "https://xrpcharts.ripple.com/#/transactions/" + hash;
            }
        }
    }

    private static class Zcash extends AbstractCoin {
        @Override
        public String getName() {
            return "Zcash";
        }

        @Override
        public String getLabel() {
            return "zcash";
        }

        @Override
        public String getCode() {
            return "ZEC";
        }

        @Override
        public String getSymbol() {
            return "ⓩ";
        }

        @Override
        public Service getService(boolean testnet) {
            if (testnet) {
                return new InsightAPI("https://explorer.testnet.z.cash/api/", getMinConf());
            } else {
                return new Service.Multi(new Service[]{
                    new InsightAPI("https://zcash.blockexplorer.com/api/", getMinConf()),
                    new InsightAPI("https://zcashnetwork.info/api/", getMinConf()),
                    new InsightAPI("https://explorer.zcashfr.io/insight-api-zcash/", getMinConf()),
                });
            }
        }

        @Override
        public String getTransactionUrl(String hash, boolean testnet) {
            if (testnet) {
                return "https://explorer.testnet.z.cash/tx/" + hash;
            } else {
                return "https://zcashnetwork.info/tx/" + hash;
            }
        }
    }

    private static abstract class ERC20Token extends Ethereum {
        @Override
        public Coin getFeeCoin() {
            return findCoin("ETH");
        }

        @Override
        public Service getService(boolean testnet) {
            String contractAddress = coins.attr("contract.address", getLabel(), testnet);
            if (testnet) {
                return new EtherscanAPI("https://api-ropsten.etherscan.io/api", contractAddress);
            } else {
                return new EtherscanAPI("https://api.etherscan.io/api", contractAddress);
            }
        }
    }

    private static class _0x extends ERC20Token {
        @Override
        public String getName() {
            return "0x";
        }

        @Override
        public String getLabel() {
            return "0x";
        }

        @Override
        public String getCode() {
            return "ZRX";
        }

        @Override
        public String getSymbol() {
            return null;
        }
    }

    private static class Augur extends ERC20Token {
        @Override
        public String getName() {
            return "Augur";
        }

        @Override
        public String getLabel() {
            return "augur";
        }

        @Override
        public String getCode() {
            return "REP";
        }

        @Override
        public String getSymbol() {
            return null;
        }
    }

    private static class BasicAttentionToken extends ERC20Token {
        @Override
        public String getName() {
            return "Basic Attention Token";
        }

        @Override
        public String getLabel() {
            return "basicattentiontoken";
        }

        @Override
        public String getCode() {
            return "BAT";
        }

        @Override
        public String getSymbol() {
            return null;
        }
    }

    private static class EOS extends ERC20Token {
        @Override
        public String getName() {
            return "EOS";
        }

        @Override
        public String getLabel() {
            return "eos";
        }

        @Override
        public String getCode() {
            return "EOS";
        }

        @Override
        public String getSymbol() {
            return null;
        }
    }

    private static class Golem extends ERC20Token {
        @Override
        public String getName() {
            return "Golem";
        }

        @Override
        public String getLabel() {
            return "golem";
        }

        @Override
        public String getCode() {
            return "GNT";
        }

        @Override
        public String getSymbol() {
            return null;
        }
    }

    private static class OmiseGO extends ERC20Token {
        @Override
        public String getName() {
            return "OmiseGO";
        }

        @Override
        public String getLabel() {
            return "omisego";
        }

        @Override
        public String getCode() {
            return "OMG";
        }

        @Override
        public String getSymbol() {
            return null;
        }
    }

    private static class Status extends ERC20Token {
        @Override
        public String getName() {
            return "Status";
        }

        @Override
        public String getLabel() {
            return "status";
        }

        @Override
        public String getCode() {
            return "SNT";
        }

        @Override
        public String getSymbol() {
            return null;
        }
    }

}
