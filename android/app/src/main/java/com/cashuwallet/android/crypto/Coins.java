package com.cashuwallet.android.crypto;

import com.raugfer.crypto.coins;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class Coins {

    private static final Coin bitcoin = new Bitcoin();
    private static final Coin bitcoincash = new BitcoinCash();
    private static final Coin bitcoingold = new BitcoinGold();
    private static final Coin bitcoinsv = new BitcoinSV();
    private static final Coin cardano = new Cardano();
    private static final Coin dash = new Dash();
    private static final Coin decred = new Decred();
    private static final Coin digibyte = new Digibyte();
    private static final Coin dogecoin = new Dogecoin();
    private static final Coin ethereum = new Ethereum();
    private static final Coin ethereumclassic = new EthereumClassic();
    private static final Coin lisk = new Lisk();
    private static final Coin litecoin = new Litecoin();
    private static final Coin nano = new Nano();
    private static final Coin neo = new Neo();
    private static final Coin neogas = new NeoGas();
    private static final Coin qtum = new Qtum();
    private static final Coin ripple = new Ripple();
    private static final Coin stellar = new Stellar();
    private static final Coin tron = new Tron();
    private static final Coin waves = new Waves();
    private static final Coin zcash = new Zcash();
    private static final Coin _0x = new _0x();
    private static final Coin aeternity = new Aeternity();
    private static final Coin augur = new Augur();
    private static final Coin basicattentiontoken = new BasicAttentionToken();
    private static final Coin binancecoin = new BinanceCoin();
    private static final Coin eos = new EOS();
    private static final Coin golem = new Golem();
    private static final Coin omisego = new OmiseGO();
    private static final Coin status = new Status();
    private static final Coin zilliqa = new Zilliqa();

    private static final Map<String, Coin> registry = new HashMap<>();

    static {
        registry.put(bitcoin.getCode(), bitcoin);
        registry.put(bitcoincash.getCode(), bitcoincash);
        registry.put(bitcoingold.getCode(), bitcoingold);
        registry.put(bitcoinsv.getCode(), bitcoinsv);
        registry.put(cardano.getCode(), cardano);
        registry.put(dash.getCode(), dash);
        registry.put(decred.getCode(), decred);
        registry.put(digibyte.getCode(), digibyte);
        registry.put(dogecoin.getCode(), dogecoin);
        registry.put(ethereum.getCode(), ethereum);
        registry.put(ethereumclassic.getCode(), ethereumclassic);
        registry.put(lisk.getCode(), lisk);
        registry.put(litecoin.getCode(), litecoin);
        registry.put(nano.getCode(), nano);
        registry.put(neo.getCode(), neo);
        registry.put(neogas.getCode(), neogas);
        registry.put(qtum.getCode(), qtum);
        registry.put(ripple.getCode(), ripple);
        registry.put(stellar.getCode(), stellar);
        registry.put(tron.getCode(), tron);
        registry.put(waves.getCode(), waves);
        registry.put(zcash.getCode(), zcash);
        registry.put(_0x.getCode(), _0x);
        registry.put(aeternity.getCode(), aeternity);
        registry.put(augur.getCode(), augur);
        registry.put(basicattentiontoken.getCode(), basicattentiontoken);
        registry.put(binancecoin.getCode(), binancecoin);
        registry.put(eos.getCode(), eos);
        registry.put(golem.getCode(), golem);
        registry.put(omisego.getCode(), omisego);
        registry.put(status.getCode(), status);
        registry.put(zilliqa.getCode(), zilliqa);
    }

    public static Coin findCoin(String code) {
        return registry.get(code);
    }

    public static Iterator<Coin> list() {
        return registry.values().iterator();
    }

    public static int count() {
        return registry.size();
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
                    new InsightAPI("https://test-insight.bitpay.com/api/", getMinConf(), "bitcoin", true),
                    new InsightAPI("https://testnet.blockexplorer.com/api/", getMinConf(), "bitcoin", true),
                    new InsightAPI("https://tbtc.blockdozer.com/insight-api/", getMinConf(), "bitcoin", true),
                    new BlockcypherAPI("https://api.blockcypher.com/v1/btc/test3", getMinConf()),
                    new SochainAPI("https://chain.so/api/v2/*/BTCTEST"),
                });
            } else {
                return new Service.Multi(new Service[]{
                    new InsightAPI("https://insight.bitpay.com/api/", getMinConf(), "bitcoin", false),
                    new InsightAPI("https://blockexplorer.com/api/", getMinConf(), "bitcoin", false),
                    new InsightAPI("https://btc.blockdozer.com/insight-api/", getMinConf(), "bitcoin", false),
                    new BlockcypherAPI("https://api.blockcypher.com/v1/btc/main", getMinConf()),
                    new SochainAPI("https://chain.so/api/v2/*/BTC"),
                });
            }
        }

        @Override
        public String getTransactionUrl(String hash, boolean testnet) {
            if (testnet) {
                return "https://test-insight.bitpay.com/tx/" + hash;
            } else {
                return "https://insight.bitpay.com/tx/" + hash;
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
                    new InsightAPI("https://tbch.blockdozer.com/insight-api/", getMinConf(), "bitcoincash", true),
                    new InsightAPI("https://test-bch-insight.bitpay.com/api/", getMinConf(), "bitcoincash", true),
                });
            } else {
                return new Service.Multi(new Service[]{
                    new InsightAPI("https://bch.blockdozer.com/insight-api/", getMinConf(), "bitcoincash", false),
                    new InsightAPI("https://bitcoincash.blockexplorer.com/api/", getMinConf(), "bitcoincash", false),
                    new InsightAPI("https://bch-insight.bitpay.com/api/", getMinConf(), "bitcoincash", false),
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
                return new InsightAPI("https://test-explorer.bitcoingold.org/insight-api/", getMinConf(), "bitcoingold", true);
            } else {
                return new InsightAPI("https://explorer.bitcoingold.org/insight-api/", getMinConf(), "bitcoingold", false);
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

    private static class BitcoinSV extends BitcoinCash {
        @Override
        public String getName() {
            return "Bitcoin SV";
        }

        @Override
        public String getLabel() {
            return "bitcoinsv";
        }

        @Override
        public String getCode() {
            return "BSV";
        }

        @Override
        public String getSymbol() {
            return null;
        }

        @Override
        public Service getService(boolean testnet) {
            if (testnet) {
                return new Service.Multi(new Service[]{
                });
            } else {
                return new Service.Multi(new Service[]{
                    new InsightAPI("https://bchsvexplorer.com/api/", getMinConf(), "bitcoinsv", false),
                });
            }
        }

        @Override
        public String getTransactionUrl(String hash, boolean testnet) {
            if (testnet) {
                return null;
            } else {
                return "https://bchsvexplorer.com/tx/" + hash;
            }
        }
    }

    private static class Cardano extends AbstractCoin {
        @Override
        public String getName() {
            return "Cardano";
        }

        @Override
        public String getLabel() {
            return "cardano";
        }

        @Override
        public String getCode() {
            return "ADA";
        }

        @Override
        public String getSymbol() {
            return "₳";
        }

        @Override
        public Service getService(boolean testnet) {
            if (testnet) {
                return new Service.Multi(new Service[]{
                    new CardanoslAPI("https://cardano-explorer.cardano-testnet.iohkdev.io/api/", "cardano", true),
                });
            } else {
                return new Service.Multi(new Service[]{
                    new CardanoslAPI("https://explorer.adalite.io/api/", "cardano", false),
                    new CardanoslAPI("https://explorer2.adalite.io/api/", "cardano", false),
                    new CardanoslAPI("https://iohk-mainnet.yoroiwallet.com/api/", "cardano", false),
                    new CardanoslAPI("https://cardanoexplorer.com/api/", "cardano", false),
                });
            }
        }

        @Override
        public String getTransactionUrl(String hash, boolean testnet) {
            if (testnet) {
                return "https://cardano-explorer.cardano-testnet.iohkdev.io/tx/" + hash;
            } else {
                return "https://cardanoexplorer.com/tx/" + hash;
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
                    new InsightAPI("https://testnet-insight.dashevo.org/insight-api-dash/", getMinConf(), "dash", true),
                    new SochainAPI("https://chain.so/api/v2/*/DASHTEST"),
                });
            } else {
                return new Service.Multi(new Service[]{
                    new InsightAPI("https://insight.dash.org/insight-api-dash/", getMinConf(), "dash", false),
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

    private static class Decred extends AbstractCoin {
        @Override
        public String getName() {
            return "Decred";
        }

        @Override
        public String getLabel() {
            return "decred";
        }

        @Override
        public String getCode() {
            return "DCR";
        }

        @Override
        public String getSymbol() {
            return null;
        }

        @Override
        public Service getService(boolean testnet) {
            if (testnet) {
                return new InsightAPI("https://testnet.decred.org/api/", getMinConf(), "decred", true);
            } else {
                return new InsightAPI("https://mainnet.decred.org/api/", getMinConf(), "decred", false);
            }
        }

        @Override
        public String getTransactionUrl(String hash, boolean testnet) {
            if (testnet) {
                return "https://testnet.decred.org/tx/" + hash;
            } else {
                return "https://mainnet.decred.org/tx/" + hash;
            }
        }
    }

    private static class Digibyte extends AbstractCoin {
        @Override
        public String getName() {
            return "Digibyte";
        }

        @Override
        public String getLabel() {
            return "digibyte";
        }

        @Override
        public String getCode() {
            return "DGB";
        }

        @Override
        public String getSymbol() {
            return null;
        }

        @Override
        public Service getService(boolean testnet) {
            if (testnet) {
                return new Service.Multi(new Service[]{
                });
            } else {
                return new Service.Multi(new Service[]{
                    new InsightAPI("https://digiexplorer.info/api/", 0/*getMinConf()*/, "digibyte", false),
                });
            }
        }

        @Override
        public String getTransactionUrl(String hash, boolean testnet) {
            if (testnet) {
                return null;
            } else {
                return "https://digiexplorer.info/tx/" + hash;
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
                    new Web3rpcAPI("https://etc-geth.0xinfra.com/"),
                    new Web3rpcAPI("https://web3.gastracker.io/"),
                });
            }
        }

        @Override
        public String getTransactionUrl(String hash, boolean testnet) {
            if (testnet) {
                return "https://mordenexplorer.ethertrack.io/addr/" + hash;
            } else {
                return "https://etherhub.io/tx/" + hash;
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
                    new LiskioAPI("https://node08.lisk.io/api/", "1.0", false),
                    new LiskioAPI("https://node07.lisk.io/api/", "1.0", false),
                    new LiskioAPI("https://node06.lisk.io/api/", "1.0", false),
                    new LiskioAPI("https://node05.lisk.io/api/", "1.0", false),
                    new LiskioAPI("https://node04.lisk.io/api/", "1.0", false),
                    new LiskioAPI("https://node03.lisk.io/api/", "1.0", false),
                    new LiskioAPI("https://node02.lisk.io/api/", "1.0", false),
                    new LiskioAPI("https://node01.lisk.io/api/", "1.0", false),
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
                    new InsightAPI("https://testnet.litecore.io/api/", getMinConf(), "litecoin", true),
                    new SochainAPI("https://chain.so/api/v2/*/LTCTEST"),
                });
            } else {
                return new Service.Multi(new Service[]{
                    new InsightAPI("https://insight.litecore.io/api/", getMinConf(), "litecoin", false),
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

    private static class Nano extends AbstractCoin {
        @Override
        public String getName() {
            return "Nano";
        }

        @Override
        public String getLabel() {
            return "nano";
        }

        @Override
        public String getCode() {
            return "NANO";
        }

        @Override
        public String getSymbol() {
            return null;
        }

        @Override
        public Service getService(boolean testnet) {
            if (testnet) {
                return new Service.Multi(new Service[]{
                    // TODO implement an API for the beta testnet
                });
            } else {
                return new Service.Multi(new Service[]{
                    new NanodeAPI("https://www.nanode.co/api/"),
                    new NanorpcAPI("https://nano.cashu.cc/rpc", false),
                });
            }
        }

        @Override
        public String getTransactionUrl(String hash, boolean testnet) {
            if (testnet) {
                return "https://beta.nano.org/block/index.php?h=" + hash;
            } else {
                return "https://raiblocks.net/block/index.php?h=" + hash;
            }
        }
    }

    private static class Neo extends AbstractCoin {
        @Override
        public String getName() {
            return "Neo";
        }

        @Override
        public String getLabel() {
            return "neo";
        }

        @Override
        public String getCode() {
            return "NEO";
        }

        @Override
        public String getSymbol() {
            return null;
        }

        @Override
        public Coin getFeeCoin() {
            return findCoin("GAS");
        }

        @Override
        public Service getService(boolean testnet) {
            if (testnet) {
                //return new NeoscanAPI("https://neoscan-testnet.io/api/test_net/v1/", true);
                return new NeoscanAPI("https://coz.neoscan-testnet.io/api/test_net/v1/", "NEO", "neo", true);
            } else {
                return new NeoscanAPI("https://api.neoscan.io/api/main_net/v1/", "NEO", "neo", false);
            }
        }

        @Override
        public String getTransactionUrl(String hash, boolean testnet) {
            if (testnet) {
                //return "https://neoscan-testnet.io/transaction/" + hash;
                return "https://coz.neoscan-testnet.io/transaction/" + hash;
            } else {
                return "https://neoscan.io/transaction/" + hash;
            }
        }
    }

    private static class NeoGas extends AbstractCoin {
        @Override
        public String getName() {
            return "Neo Gas";
        }

        @Override
        public String getLabel() {
            return "neogas";
        }

        @Override
        public String getCode() {
            return "GAS";
        }

        @Override
        public String getSymbol() {
            return null;
        }

        @Override
        public Service getService(boolean testnet) {
            if (testnet) {
                //return new NeoscanAPI("https://neoscan-testnet.io/api/test_net/v1/", true);
                return new NeoscanAPI("https://coz.neoscan-testnet.io/api/test_net/v1/", "GAS", "neogas", true);
            } else {
                return new NeoscanAPI("https://api.neoscan.io/api/main_net/v1/", "GAS", "neogas", false);
            }
        }

        @Override
        public String getTransactionUrl(String hash, boolean testnet) {
            if (testnet) {
                //return "https://neoscan-testnet.io/transaction/" + hash;
                return "https://coz.neoscan-testnet.io/transaction/" + hash;
            } else {
                return "https://neoscan.io/transaction/" + hash;
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
                return new InsightAPI("https://testnet.qtum.org/insight-api/", getMinConf(), "qtum", true);
            } else {
                return new InsightAPI("https://explorer.qtum.org/insight-api/", getMinConf(), "qtum", false);
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

    private static class Stellar extends AbstractCoin {
        @Override
        public String getName() {
            return "Stellar";
        }

        @Override
        public String getLabel() {
            return "stellar";
        }

        @Override
        public String getCode() {
            return "XLM";
        }

        @Override
        public String getSymbol() {
            return null;
        }

        @Override
        public Service getService(boolean testnet) {
            if (testnet) {
                return new HorizonAPI("https://horizon-testnet.stellar.org/");
            } else {
                return new HorizonAPI("https://horizon.stellar.org/");
            }
        }

        @Override
        public String getTransactionUrl(String hash, boolean testnet) {
            if (testnet) {
                return "http://testnet.stellarchain.io/tx/" + hash;
            } else {
                return "https://stellarchain.io/tx/" + hash;
            }
        }
    }

    private static class Tron extends AbstractCoin {
        @Override
        public String getName() {
            return "Tron";
        }

        @Override
        public String getLabel() {
            return "tron";
        }

        @Override
        public String getCode() {
            return "TRX";
        }

        @Override
        public String getSymbol() {
            return null;
        }

        @Override
        public Service getService(boolean testnet) {
            if (testnet) {
                return new TronscanAPI("https://api.shasta.tronscan.org/api/");
            } else {
                return new TronscanAPI("https://api.tronscan.org/api/");
            }
        }

        @Override
        public String getTransactionUrl(String hash, boolean testnet) {
            if (testnet) {
                return "https://shasta.tronscan.org/#/transaction/" + hash;
            } else {
                return "https://tronscan.org/#/transaction/" + hash;
            }
        }
    }

    private static class Waves extends AbstractCoin {
        @Override
        public String getName() {
            return "Waves";
        }

        @Override
        public String getLabel() {
            return "waves";
        }

        @Override
        public String getCode() {
            return "WAVES";
        }

        @Override
        public String getSymbol() {
            return null;
        }

        @Override
        public Service getService(boolean testnet) {
            if (testnet) {
                return new WavesnodesAPI("https://pool.testnet.wavesnodes.com/", true);
            } else {
                return new WavesnodesAPI("https://nodes.wavesnodes.com/", false);
            }
        }

        @Override
        public String getTransactionUrl(String hash, boolean testnet) {
            if (testnet) {
                return "https://testnet.wavesexplorer.com/tx/" + hash;
            } else {
                return "https://wavesexplorer.com/tx/" + hash;
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
                return new InsightAPI("https://explorer.testnet.z.cash/api/", getMinConf(), "zcash", true);
            } else {
                return new Service.Multi(new Service[]{
                    new InsightAPI("https://zcash.blockexplorer.com/api/", getMinConf(), "zcash", false),
                    new InsightAPI("https://zcashnetwork.info/api/", getMinConf(), "zcash", false),
                    new InsightAPI("https://explorer.zcashfr.io/insight-api-zcash/", getMinConf(), "zcash", false),
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

    public static abstract class ERC20Token extends Ethereum {
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

    private static class Aeternity extends ERC20Token {
        @Override
        public String getName() {
            return "Aeternity";
        }

        @Override
        public String getLabel() {
            return "aeternity";
        }

        @Override
        public String getCode() {
            return "AE";
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

    private static class BinanceCoin extends ERC20Token {
        @Override
        public String getName() {
            return "Binance Coin";
        }

        @Override
        public String getLabel() {
            return "binancecoin";
        }

        @Override
        public String getCode() {
            return "BNB";
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

    private static class Zilliqa extends ERC20Token {
        @Override
        public String getName() {
            return "Zilliqa";
        }

        @Override
        public String getLabel() {
            return "zilliqa";
        }

        @Override
        public String getCode() {
            return "ZIL";
        }

        @Override
        public String getSymbol() {
            return null;
        }
    }

    public static abstract class WavesToken extends Waves {
        @Override
        public Coin getFeeCoin() {
            return findCoin("WAVES");
        }

        @Override
        public Service getService(boolean testnet) {
            String assetId = coins.attr("asset.id", getLabel(), testnet);
            if (testnet) {
                return new WavesnodesAPI("https://pool.testnet.wavesnodes.com/", assetId, true);
            } else {
                return new WavesnodesAPI("https://nodes.wavesnodes.com/", assetId, false);
            }
        }
    }

}
