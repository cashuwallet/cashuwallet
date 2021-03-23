package com.cashuwallet.android.crypto;

import com.raugfer.crypto.coins;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class Coins {

    private static final Coin binancecoin = new BinanceCoin();
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
    private static final Coin fantom = new Fantom();
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
    private static final Coin erc20_0x = new ERC20_0x();
    private static final Coin erc20_aeternity = new ERC20_Aeternity();
    private static final Coin erc20_augur = new ERC20_Augur();
    private static final Coin erc20_basicattentiontoken = new ERC20_BasicAttentionToken();
    private static final Coin erc20_binancecoin = new ERC20_BinanceCoin();
    private static final Coin erc20_chainlink = new ERC20_Chainlink();
    private static final Coin erc20_dai = new ERC20_Dai();
    private static final Coin erc20_eos = new ERC20_EOS();
    private static final Coin erc20_fantom = new ERC20_Fantom();
    private static final Coin erc20_geminidollar = new ERC20_GeminiDollar();
    private static final Coin erc20_golem = new ERC20_Golem();
    private static final Coin erc20_maker = new ERC20_Maker();
    private static final Coin erc20_omisego = new ERC20_OmiseGO();
    private static final Coin erc20_sai = new ERC20_Sai();
    private static final Coin erc20_status = new ERC20_Status();
    private static final Coin erc20_tether = new ERC20_Tether();
    private static final Coin erc20_usdcoin = new ERC20_USDCoin();
    private static final Coin erc20_wrappedbitcoin = new ERC20_WrappedBitcoin();
    private static final Coin erc20_zilliqa = new ERC20_Zilliqa();
    private static final Coin bep20_basicattentiontoken = new BEP20_BasicAttentionToken();
    private static final Coin bep20_bitcoin = new BEP20_Bitcoin();
    private static final Coin bep20_bitcoincash = new BEP20_BitcoinCash();
    private static final Coin bep20_cardano = new BEP20_Cardano();
    private static final Coin bep20_chainlink = new BEP20_Chainlink();
    private static final Coin bep20_dai = new BEP20_Dai();
    private static final Coin bep20_dogecoin = new BEP20_Dogecoin();
    private static final Coin bep20_eos = new BEP20_EOS();
    private static final Coin bep20_ethereum = new BEP20_Ethereum();
    private static final Coin bep20_ethereumclassic = new BEP20_EthereumClassic();
    private static final Coin bep20_litecoin = new BEP20_Litecoin();
    private static final Coin bep20_maker = new BEP20_Maker();
    private static final Coin bep20_ripple = new BEP20_Ripple();
    private static final Coin bep20_tether = new BEP20_Tether();
    private static final Coin bep20_usdcoin = new BEP20_USDCoin();
    private static final Coin bep20_zcash = new BEP20_Zcash();
    private static final Coin bep20_zilliqa = new BEP20_Zilliqa();

    private static final Map<String, Coin> registry = new HashMap<>();

    static {
        registry.put(binancecoin.getLabel(), binancecoin);
        registry.put(bitcoin.getLabel(), bitcoin);
        registry.put(bitcoincash.getLabel(), bitcoincash);
        registry.put(bitcoingold.getLabel(), bitcoingold);
        registry.put(bitcoinsv.getLabel(), bitcoinsv);
        registry.put(cardano.getLabel(), cardano);
        registry.put(dash.getLabel(), dash);
        registry.put(decred.getLabel(), decred);
        registry.put(digibyte.getLabel(), digibyte);
        registry.put(dogecoin.getLabel(), dogecoin);
        registry.put(ethereum.getLabel(), ethereum);
        registry.put(ethereumclassic.getLabel(), ethereumclassic);
        registry.put(fantom.getLabel(), fantom);
        registry.put(lisk.getLabel(), lisk);
        registry.put(litecoin.getLabel(), litecoin);
        registry.put(nano.getLabel(), nano);
        registry.put(neo.getLabel(), neo);
        registry.put(neogas.getLabel(), neogas);
        registry.put(qtum.getLabel(), qtum);
        registry.put(ripple.getLabel(), ripple);
        registry.put(stellar.getLabel(), stellar);
        registry.put(tron.getLabel(), tron);
        registry.put(waves.getLabel(), waves);
        registry.put(zcash.getLabel(), zcash);
        registry.put(erc20_0x.getLabel(), erc20_0x);
        registry.put(erc20_aeternity.getLabel(), erc20_aeternity);
        registry.put(erc20_augur.getLabel(), erc20_augur);
        registry.put(erc20_basicattentiontoken.getLabel(), erc20_basicattentiontoken);
        registry.put(erc20_binancecoin.getLabel(), erc20_binancecoin);
        registry.put(erc20_chainlink.getLabel(), erc20_chainlink);
        registry.put(erc20_dai.getLabel(), erc20_dai);
        registry.put(erc20_eos.getLabel(), erc20_eos);
        registry.put(erc20_fantom.getLabel(), erc20_fantom);
        registry.put(erc20_geminidollar.getLabel(), erc20_geminidollar);
        registry.put(erc20_golem.getLabel(), erc20_golem);
        registry.put(erc20_maker.getLabel(), erc20_maker);
        registry.put(erc20_omisego.getLabel(), erc20_omisego);
        registry.put(erc20_sai.getLabel(), erc20_sai);
        registry.put(erc20_status.getLabel(), erc20_status);
        registry.put(erc20_tether.getLabel(), erc20_tether);
        registry.put(erc20_usdcoin.getLabel(), erc20_usdcoin);
        registry.put(erc20_wrappedbitcoin.getLabel(), erc20_wrappedbitcoin);
        registry.put(erc20_zilliqa.getLabel(), erc20_zilliqa);
        registry.put(bep20_basicattentiontoken.getLabel(), bep20_basicattentiontoken);
        registry.put(bep20_bitcoin.getLabel(), bep20_bitcoin);
        registry.put(bep20_bitcoincash.getLabel(), bep20_bitcoincash);
        registry.put(bep20_cardano.getLabel(), bep20_cardano);
        registry.put(bep20_chainlink.getLabel(), bep20_chainlink);
        registry.put(bep20_dai.getLabel(), bep20_dai);
        registry.put(bep20_dogecoin.getLabel(), bep20_dogecoin);
        registry.put(bep20_eos.getLabel(), bep20_eos);
        registry.put(bep20_ethereum.getLabel(), bep20_ethereum);
        registry.put(bep20_ethereumclassic.getLabel(), bep20_ethereumclassic);
        registry.put(bep20_litecoin.getLabel(), bep20_litecoin);
        registry.put(bep20_maker.getLabel(), bep20_maker);
        registry.put(bep20_ripple.getLabel(), bep20_ripple);
        registry.put(bep20_tether.getLabel(), bep20_tether);
        registry.put(bep20_usdcoin.getLabel(), bep20_usdcoin);
        registry.put(bep20_zcash.getLabel(), bep20_zcash);
        registry.put(bep20_zilliqa.getLabel(), bep20_zilliqa);
    }

    public static Coin findCoin(String label) {
        return registry.get(label);
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

    private static class BinanceCoin extends AbstractCoin {
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

        @Override
        public Service getService(boolean testnet) {
            if (testnet) {
                return new Service.Multi(new Service[]{
                    new EtherscanAPI("https://api-testnet.bscscan.com/api"),
                    new Web3rpcAPI("https://data-seed-prebsc-1-s1.binance.org:8545/"),
                    new Web3rpcAPI("https://data-seed-prebsc-2-s1.binance.org:8545/"),
                    new Web3rpcAPI("https://data-seed-prebsc-1-s2.binance.org:8545/"),
                    new Web3rpcAPI("https://data-seed-prebsc-2-s2.binance.org:8545/"),
                    new Web3rpcAPI("https://data-seed-prebsc-1-s3.binance.org:8545/"),
                    new Web3rpcAPI("https://data-seed-prebsc-2-s3.binance.org:8545/"),
                });
            } else {
                return new Service.Multi(new Service[]{
                    new EtherscanAPI("https://api.bscscan.com/api"),
                    new Web3rpcAPI("https://bsc-dataseed.binance.org/"),
                    new Web3rpcAPI("https://bsc-dataseed1.defibit.io/"),
                    new Web3rpcAPI("https://bsc-dataseed1.ninicoin.io/"),
                    new Web3rpcAPI("https://bsc-dataseed2.defibit.io/"),
                    new Web3rpcAPI("https://bsc-dataseed3.defibit.io/"),
                    new Web3rpcAPI("https://bsc-dataseed4.defibit.io/"),
                    new Web3rpcAPI("https://bsc-dataseed2.ninicoin.io/"),
                    new Web3rpcAPI("https://bsc-dataseed3.ninicoin.io/"),
                    new Web3rpcAPI("https://bsc-dataseed4.ninicoin.io/"),
                    new Web3rpcAPI("https://bsc-dataseed1.binance.org/"),
                    new Web3rpcAPI("https://bsc-dataseed2.binance.org/"),
                    new Web3rpcAPI("https://bsc-dataseed3.binance.org/"),
                    new Web3rpcAPI("https://bsc-dataseed4.binance.org/"),
                });
            }
        }

        @Override
        public String getTransactionUrl(String hash, boolean testnet) {
            if (testnet) {
                return "https://testnet.bscscan.com/tx/" + hash;
            } else {
                return "https://bscscan.com/tx/" + hash;
            }
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
                    new InsightAPI("https://tbtc.blockdozer.com/insight-api/", getMinConf(), "bitcoin", true),
                    new BlockcypherAPI("https://api.blockcypher.com/v1/btc/test3", getMinConf()),
                    new SochainAPI("https://chain.so/api/v2/*/BTCTEST"),
                    new BlockBookAPI("https://btcbook-testnet.guarda.co/api/", getMinConf(), "bitcoin", true),
                    new BlockBookAPI("https://tbtc2.trezor.io/api/", getMinConf(), "bitcoin", true),
                    new BlockBookAPI("https://tbtc1.trezor.io/api/", getMinConf(), "bitcoin", true),
                    // Obsolete
                    // new InsightAPI("https://testnet.blockexplorer.com/api/", getMinConf(), "bitcoin", true),
                    // new InsightAPI("https://test-insight.bitpay.com/api/", getMinConf(), "bitcoin", true),
                });
            } else {
                return new Service.Multi(new Service[]{
                    new InsightAPI("https://btc.blockdozer.com/insight-api/", getMinConf(), "bitcoin", false),
                    new BlockcypherAPI("https://api.blockcypher.com/v1/btc/main", getMinConf()),
                    new SochainAPI("https://chain.so/api/v2/*/BTC"),
                    new BlockBookAPI("https://btc.nownodes.io/api/", getMinConf(), "bitcoin", false),
                    new BlockBookAPI("https://btcbook.guarda.co/api/", getMinConf(), "bitcoin", false),
                    new BlockBookAPI("https://btc5.trezor.io/api/", getMinConf(), "bitcoin", false),
                    new BlockBookAPI("https://btc4.trezor.io/api/", getMinConf(), "bitcoin", false),
                    new BlockBookAPI("https://btc3.trezor.io/api/", getMinConf(), "bitcoin", false),
                    new BlockBookAPI("https://btc2.trezor.io/api/", getMinConf(), "bitcoin", false),
                    new BlockBookAPI("https://btc1.trezor.io/api/", getMinConf(), "bitcoin", false),
                    // Obsolete
                    // new InsightAPI("https://insight.bitpay.com/api/", getMinConf(), "bitcoin", false),
                    // new InsightAPI("https://blockexplorer.com/api/", getMinConf(), "bitcoin", false),
                });
            }
        }

        @Override
        public String getTransactionUrl(String hash, boolean testnet) {
            if (testnet) {
                return "https://www.blockchain.com/btc-testnet/tx/" + hash;
            } else {
                return "https://www.blockchain.com/btc/tx/" + hash;
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
                    new BlockBookAPI("https://bchbook.guarda.co/api/", getMinConf(), "bitcoincash", false),
                    new BlockBookAPI("https://bch5.trezor.io/api/", getMinConf(), "bitcoincash", false),
                    new BlockBookAPI("https://bch4.trezor.io/api/", getMinConf(), "bitcoincash", false),
                    new BlockBookAPI("https://bch3.trezor.io/api/", getMinConf(), "bitcoincash", false),
                    new BlockBookAPI("https://bch2.trezor.io/api/", getMinConf(), "bitcoincash", false),
                    new BlockBookAPI("https://bch1.trezor.io/api/", getMinConf(), "bitcoincash", false),
                    // Obsolete
                    // new InsightAPI("https://bitcoincash.blockexplorer.com/api/", getMinConf(), "bitcoincash", false),
                    // new InsightAPI("https://bch-insight.bitpay.com/api/", getMinConf(), "bitcoincash", false),
                });
            }
        }

        @Override
        public String getTransactionUrl(String hash, boolean testnet) {
            if (testnet) {
                return "https://www.blockchain.com/bch-testnet/tx/" + hash;
            } else {
                return "https://www.blockchain.com/bch/tx/" + hash;
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
                return new Service.Multi(new Service[]{
                    new InsightAPI("https://explorer.bitcoingold.org/insight-api/", getMinConf(), "bitcoingold", false),
                    new BlockBookAPI("https://btgexplorer.com/api/", getMinConf(), "bitcoingold", false),
                    new BlockBookAPI("https://btg.nownodes.io/api/", getMinConf(), "bitcoingold", false),
                    new BlockBookAPI("https://btgbook.guarda.co/api/", getMinConf(), "bitcoingold", false),
                    new BlockBookAPI("https://btg5.trezor.io/api/", getMinConf(), "bitcoingold", false),
                    new BlockBookAPI("https://btg4.trezor.io/api/", getMinConf(), "bitcoingold", false),
                    new BlockBookAPI("https://btg3.trezor.io/api/", getMinConf(), "bitcoingold", false),
                    new BlockBookAPI("https://btg2.trezor.io/api/", getMinConf(), "bitcoingold", false),
                    new BlockBookAPI("https://btg1.trezor.io/api/", getMinConf(), "bitcoingold", false),
                });
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
                    new BlockBookAPI("https://bsvtestnetbook.guarda.co/api/", getMinConf(), "bitcoinsv", true),
                });
            } else {
                return new Service.Multi(new Service[]{
                    new InsightAPI("https://bchsvexplorer.com/api/", getMinConf(), "bitcoinsv", false),
                    new BlockBookAPI("https://bsv.nownodes.io/api/", getMinConf(), "bitcoinsv", false),
                    new BlockBookAPI("https://bsvbook.guarda.co/api/", getMinConf(), "bitcoinsv", false),
                });
            }
        }

        @Override
        public String getTransactionUrl(String hash, boolean testnet) {
            if (testnet) {
                return "https://test.whatsonchain.com/tx/" + hash;
            } else {
                return "https://main.whatsonchain.com/tx/" + hash;
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
                    new BlockBookAPI("https://dash.nownodes.io/api/", getMinConf(), "dash", false),
                    new BlockBookAPI("https://dashbook.guarda.co/api/", getMinConf(), "dash", false),
                    new BlockBookAPI("https://dash5.trezor.io/api/", getMinConf(), "dash", false),
                    new BlockBookAPI("https://dash4.trezor.io/api/", getMinConf(), "dash", false),
                    new BlockBookAPI("https://dash3.trezor.io/api/", getMinConf(), "dash", false),
                    new BlockBookAPI("https://dash2.trezor.io/api/", getMinConf(), "dash", false),
                    new BlockBookAPI("https://dash1.trezor.io/api/", getMinConf(), "dash", false),
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
                return new Service.Multi(new Service[]{
                    new InsightAPI("https://testnet.decred.org/insight/api/", getMinConf(), "decred", true),
                    new InsightAPI("https://testnet.dcrdata.org/insight/api/", getMinConf(), "decred", true),
                });
            } else {
                return new Service.Multi(new Service[]{
                    new InsightAPI("https://mainnet.decred.org/insight/api/", getMinConf(), "decred", false),
                    new InsightAPI("https://mainnet.dcrdata.org/insight/api/", getMinConf(), "decred", false),
                    new BlockBookAPI("https://dcrblockexplorer.com/api/", getMinConf(), "decred", false),
                    new BlockBookAPI("https://dcr.nownodes.io/api/", getMinConf(), "decred", false),
                    new BlockBookAPI("https://dcrbook.guarda.co/api/", getMinConf(), "decred", false),
                });
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
                return new InsightAPI("https://testnet.digiexplorer.info/api/", 0/*getMinConf()*/, "digibyte", true);
            } else {
                return new Service.Multi(new Service[]{
                    new InsightAPI("https://digiexplorer.info/api/", 0/*getMinConf()*/, "digibyte", false),
                    new BlockBookAPI("https://digibyteblockexplorer.com/api/", getMinConf(), "digibyte", false),
                    new BlockBookAPI("https://dgbbook.guarda.co/api/", getMinConf(), "digibyte", false),
                    new BlockBookAPI("https://dgb2.trezor.io/api/", getMinConf(), "digibyte", false),
                    new BlockBookAPI("https://dgb1.trezor.io/api/", getMinConf(), "digibyte", false),
                });
            }
        }

        @Override
        public String getTransactionUrl(String hash, boolean testnet) {
            if (testnet) {
                return "https://testnet.digiexplorer.info/tx/" + hash;
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
                    new InsightAPI("https://dogeblocks.com/api/", 0/*getMinConf()*/, "dogecoin", false),
                    new DogechainAPI("https://dogechain.info/api/v1/"),
                    new BlockcypherAPI("https://api.blockcypher.com/v1/doge/main", getMinConf()),
                    new SochainAPI("https://chain.so/api/v2/*/DOGE"),
                    new BlockBookAPI("https://doge.nownodes.io/api/", getMinConf(), "dogecoin", false),
                    new BlockBookAPI("https://dogebook.guarda.co/api/", getMinConf(), "dogecoin", false),
                    new BlockBookAPI("https://doge5.trezor.io/api/", getMinConf(), "dogecoin", false),
                    new BlockBookAPI("https://doge4.trezor.io/api/", getMinConf(), "dogecoin", false),
                    new BlockBookAPI("https://doge3.trezor.io/api/", getMinConf(), "dogecoin", false),
                    new BlockBookAPI("https://doge2.trezor.io/api/", getMinConf(), "dogecoin", false),
                    new BlockBookAPI("https://doge1.trezor.io/api/", getMinConf(), "dogecoin", false),
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
                return new Service.Multi(new Service[]{
                    new EtherscanAPI("https://api-ropsten.etherscan.io/api"),
                    // Obsolete
                    // new EtherscanAPI("https://blockscout.com/eth/ropsten/api", true),
                });
            } else {
                return new Service.Multi(new Service[]{
                    new EtherscanAPI("https://api.etherscan.io/api"),
                    new BlockcypherAPI("https://api.blockcypher.com/v1/eth/main"),
                    new Web3rpcAPI("https://eth.nownodes.io/"),
                    new BlockBookAPI("https://ethbook.guarda.co/api/"),
                    new BlockBookAPI("https://eth2.trezor.io/api/"),
                    new BlockBookAPI("https://eth1.trezor.io/api/"),
                    // Obsolete
                    // new Web3rpcAPI("https://blockscout.com/eth/mainnet/api/eth_rpc"),
                    // new EtherscanAPI("https://blockscout.com/eth/mainnet/api", true),
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
                return new Service.Multi(new Service[]{
                    new Web3rpcAPI("https://blockscout.com/etc/kotti/api/eth_rpc"),
                    new EtherscanAPI("https://blockscout.com/etc/kotti/api", true),
                    new Web3rpcAPI("https://www.ethercluster.com/kotti"),
                    // Obsolete
                    // new EtherscanAPI("https://kottiexplorer.ethernode.io/api", true),
                    // new Web3rpcAPI("https://web3.gastracker.io/morden"),
                });
            } else {
                return new Service.Multi(new Service[]{
                    new Web3rpcAPI("https://blockscout.com/etc/mainnet/api/eth_rpc"),
                    new EtherscanAPI("https://blockscout.com/etc/mainnet/api", true),
                    new Web3rpcAPI("https://www.ethercluster.com/etc"),
                    new BlockBookAPI("https://etcblockexplorer.com/api/"),
                    new BlockBookAPI("https://etcbook.guarda.co/api/"),
                    new BlockBookAPI("https://etc2.trezor.io/api/"),
                    new BlockBookAPI("https://etc1.trezor.io/api/"),
                    // Obsolete
                    // new Web3rpcAPI("https://etc-geth.0xinfra.com/"),
                    // new Web3rpcAPI("https://etc-parity.0xinfra.com/"),
                    // new GastrackerAPI("https://api.gastracker.io/v1/"),
                    // new Web3rpcAPI("https://web3.gastracker.io/"),
                });
            }
        }

        @Override
        public String getTransactionUrl(String hash, boolean testnet) {
            if (testnet) {
                return "https://blockscout.com/etc/kotti/tx/" + hash;
                // Obsolete
                // return "https://kottiexplorer.ethernode.io/tx/" + hash;
                // return "https://mordenexplorer.ethertrack.io/addr/" + hash;
            } else {
                return "https://blockscout.com/etc/mainnet/tx/" + hash;
            }
        }
    }

    private static class Fantom extends AbstractCoin {
        @Override
        public String getName() {
            return "Fantom";
        }

        @Override
        public String getLabel() {
            return "fantom";
        }

        @Override
        public String getCode() {
            return "FTM";
        }

        @Override
        public String getSymbol() {
            return null;
        }

        @Override
        public Service getService(boolean testnet) {
            if (testnet) {
                return new Service.Multi(new Service[]{
                    new Web3rpcAPI("https://rpc.testnet.fantom.network/"),
                });
            } else {
                return new Service.Multi(new Service[]{
                    new EtherscanAPI("https://api.ftmscan.com/api"),
                    new Web3rpcAPI("https://rpcapi.fantom.network/"),
                });
            }
        }

        @Override
        public String getTransactionUrl(String hash, boolean testnet) {
            if (testnet) {
                return "https://explorer.testnet.fantom.network/transactions/" + hash;
            } else {
                return "https://ftmscan.com/tx/" + hash;
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
                    new InsightAPI("https://litecoinblockexplorer.net/api/", getMinConf(), "litecoin", false),
                    new BlockcypherAPI("https://api.blockcypher.com/v1/ltc/main", getMinConf()),
                    new SochainAPI("https://chain.so/api/v2/*/LTC"),
                    new BlockBookAPI("https://ltc.nownodes.io/api/", getMinConf(), "litecoin", false),
                    new BlockBookAPI("https://ltcbook.guarda.co/api/", getMinConf(), "litecoin", false),
                    new BlockBookAPI("https://ltc5.trezor.io/api/", getMinConf(), "litecoin", false),
                    new BlockBookAPI("https://ltc4.trezor.io/api/", getMinConf(), "litecoin", false),
                    new BlockBookAPI("https://ltc3.trezor.io/api/", getMinConf(), "litecoin", false),
                    new BlockBookAPI("https://ltc2.trezor.io/api/", getMinConf(), "litecoin", false),
                    new BlockBookAPI("https://ltc1.trezor.io/api/", getMinConf(), "litecoin", false),
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
            return findCoin("neogas");
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
                return new Service.Multi(new Service[]{
                    new QtuminfoAPI("https://testnet.qtum.info/api/"),
                    new InsightAPI("https://testnet.qtum.org/insight-api/", getMinConf(), "qtum", true),
                });
            } else {
                return new Service.Multi(new Service[]{
                    new QtuminfoAPI("https://qtum.info/api/"),
                    new InsightAPI("https://explorer.qtum.org/insight-api/", getMinConf(), "qtum", false),
                    new InsightAPI("https://qtumblockexplorer.com/api/", getMinConf(), "qtum", false),
                    new BlockBookAPI("https://qtumbook.guarda.co/api/", getMinConf(), "qtum", false),
                });
            }
        }

        @Override
        public String getTransactionUrl(String hash, boolean testnet) {
            if (testnet) {
                return "https://testnet.qtum.info/tx/" + hash;
            } else {
                return "https://qtum.info/tx/" + hash;
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
                return "https://test.bithomp.com/explorer/" + hash;
            } else {
                return "https://bithomp.com/explorer/" + hash;
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
                return new TronscanAPI("https://api.shasta.tronscan.org/api/", true);
            } else {
                return new TronscanAPI("https://api.tronscan.org/api/", false);
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
                return new WavesnodesAPI("https://nodes-testnet.wavesnodes.com/", true);
            } else {
                return new WavesnodesAPI("https://nodes.wavesnodes.com/", false);
            }
        }

        @Override
        public String getTransactionUrl(String hash, boolean testnet) {
            if (testnet) {
                return "https://wavesexplorer.com/testnet/tx/" + hash;
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
                return new Service.Multi(new Service[]{
                    new InsightAPI("https://explorer.testnet.z.cash/api/", getMinConf(), "zcash", true),
                    new BlockBookAPI("https://zecbook-testnet.guarda.co/api/", getMinConf(), "zcash", true),
                });
            } else {
                return new Service.Multi(new Service[]{
                    new InsightAPI("https://explorer.z.cash/api/", getMinConf(), "zcash", false),
                    new InsightAPI("https://explorer.zcashfr.io/insight-api-zcash/", getMinConf(), "zcash", false),
                    new BlockBookAPI("https://zec.nownodes.io/api/", getMinConf(), "zcash", false),
                    new BlockBookAPI("https://zecbook.guarda.co/api/", getMinConf(), "zcash", false),
                    new BlockBookAPI("https://zec5.trezor.io/api/", getMinConf(), "zcash", false),
                    new BlockBookAPI("https://zec4.trezor.io/api/", getMinConf(), "zcash", false),
                    new BlockBookAPI("https://zec3.trezor.io/api/", getMinConf(), "zcash", false),
                    new BlockBookAPI("https://zec2.trezor.io/api/", getMinConf(), "zcash", false),
                    new BlockBookAPI("https://zec1.trezor.io/api/", getMinConf(), "zcash", false),
                    // Obsolete
                    // new InsightAPI("https://zcash.blockexplorer.com/api/", getMinConf(), "zcash", false),
                    // new InsightAPI("https://zcashnetwork.info/api/", getMinConf(), "zcash", false),
                });
            }
        }

        @Override
        public String getTransactionUrl(String hash, boolean testnet) {
            if (testnet) {
                return "https://explorer.testnet.z.cash/tx/" + hash;
            } else {
                return "https://explorer.z.cash/tx/" + hash;
            }
        }
    }

    public static abstract class ERC20Token extends Ethereum {
        @Override
        public Coin getFeeCoin() {
            return findCoin("ethereum");
        }

        @Override
        public Service getService(boolean testnet) {
            String contractAddress = coins.attr("contract.address", getLabel(), testnet);
            if (testnet) {
                return new Service.Multi(new Service[]{
                    new EtherscanAPI("https://api-ropsten.etherscan.io/api", contractAddress),
                    // Obsolete
                    // new EtherscanAPI("https://blockscout.com/eth/ropsten/api", contractAddress, true),
                });
            } else {
                return new Service.Multi(new Service[]{
                    new EtherscanAPI("https://api.etherscan.io/api", contractAddress),
                    new EtherscanAPI("https://blockscout.com/eth/mainnet/api", contractAddress, true),
                    new BlockBookAPI("https://ethbook.guarda.co/api/", contractAddress),
                    new BlockBookAPI("https://eth2.trezor.io/api/", contractAddress),
                    new BlockBookAPI("https://eth1.trezor.io/api/", contractAddress),
                });
            }
        }
    }

    private static class ERC20_0x extends ERC20Token {
        @Override
        public String getName() {
            return "0x";
        }

        @Override
        public String getLabel() {
            return "erc20-0x";
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

    private static class ERC20_Aeternity extends ERC20Token {
        @Override
        public String getName() {
            return "Aeternity";
        }

        @Override
        public String getLabel() {
            return "erc20-aeternity";
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

    private static class ERC20_Augur extends ERC20Token {
        @Override
        public String getName() {
            return "Augur";
        }

        @Override
        public String getLabel() {
            return "erc20-augur";
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

    private static class ERC20_BasicAttentionToken extends ERC20Token {
        @Override
        public String getName() {
            return "Basic Attention Token";
        }

        @Override
        public String getLabel() {
            return "erc20-basicattentiontoken";
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

    private static class ERC20_BinanceCoin extends ERC20Token {
        @Override
        public String getName() {
            return "Binance Coin";
        }

        @Override
        public String getLabel() {
            return "erc20-binancecoin";
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

    private static class ERC20_Chainlink extends ERC20Token {
        @Override
        public String getName() {
            return "Chainlink";
        }

        @Override
        public String getLabel() {
            return "erc20-chainlink";
        }

        @Override
        public String getCode() {
            return "LINK";
        }

        @Override
        public String getSymbol() {
            return null;
        }
    }

    private static class ERC20_Dai extends ERC20Token {
        @Override
        public String getName() {
            return "Dai";
        }

        @Override
        public String getLabel() {
            return "erc20-dai";
        }

        @Override
        public String getCode() {
            return "DAI";
        }

        @Override
        public String getSymbol() {
            return null;
        }
    }

    private static class ERC20_EOS extends ERC20Token {
        @Override
        public String getName() {
            return "EOS";
        }

        @Override
        public String getLabel() {
            return "erc20-eos";
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

    private static class ERC20_Fantom extends ERC20Token {
        @Override
        public String getName() {
            return "Fantom";
        }

        @Override
        public String getLabel() {
            return "erc20-fantom";
        }

        @Override
        public String getCode() {
            return "FTM";
        }

        @Override
        public String getSymbol() {
            return null;
        }
    }

    private static class ERC20_GeminiDollar extends ERC20Token {
        @Override
        public String getName() {
            return "Gemini Dollar";
        }

        @Override
        public String getLabel() {
            return "erc20-geminidollar";
        }

        @Override
        public String getCode() {
            return "GUSD";
        }

        @Override
        public String getSymbol() {
            return null;
        }
    }

    private static class ERC20_Golem extends ERC20Token {
        @Override
        public String getName() {
            return "Golem";
        }

        @Override
        public String getLabel() {
            return "erc20-golem";
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

    private static class ERC20_Maker extends ERC20Token {
        @Override
        public String getName() {
            return "Maker";
        }

        @Override
        public String getLabel() {
            return "erc20-maker";
        }

        @Override
        public String getCode() {
            return "MKR";
        }

        @Override
        public String getSymbol() {
            return null;
        }
    }

    private static class ERC20_OmiseGO extends ERC20Token {
        @Override
        public String getName() {
            return "OmiseGO";
        }

        @Override
        public String getLabel() {
            return "erc20-omisego";
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

    private static class ERC20_Sai extends ERC20Token {
        @Override
        public String getName() {
            return "Sai";
        }

        @Override
        public String getLabel() {
            return "erc20-sai";
        }

        @Override
        public String getCode() {
            return "SAI";
        }

        @Override
        public String getSymbol() {
            return null;
        }
    }

    private static class ERC20_Status extends ERC20Token {
        @Override
        public String getName() {
            return "Status";
        }

        @Override
        public String getLabel() {
            return "erc20-status";
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

    private static class ERC20_Tether extends ERC20Token {
        @Override
        public String getName() {
            return "Tether";
        }

        @Override
        public String getLabel() {
            return "erc20-tether";
        }

        @Override
        public String getCode() {
            return "USDT";
        }

        @Override
        public String getSymbol() {
            return null;
        }
    }

    private static class ERC20_USDCoin extends ERC20Token {
        @Override
        public String getName() {
            return "USD Coin";
        }

        @Override
        public String getLabel() {
            return "erc20-usdcoin";
        }

        @Override
        public String getCode() {
            return "USDC";
        }

        @Override
        public String getSymbol() {
            return null;
        }
    }

    private static class ERC20_WrappedBitcoin extends ERC20Token {
        @Override
        public String getName() {
            return "Wrapped Bitcoin";
        }

        @Override
        public String getLabel() {
            return "erc20-wrappedbitcoin";
        }

        @Override
        public String getCode() {
            return "WBTC";
        }

        @Override
        public String getSymbol() {
            return null;
        }
    }

    private static class ERC20_Zilliqa extends ERC20Token {
        @Override
        public String getName() {
            return "Zilliqa";
        }

        @Override
        public String getLabel() {
            return "erc20-zilliqa";
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

    public static abstract class BEP20Token extends BinanceCoin {
        @Override
        public Coin getFeeCoin() {
            return findCoin("binancecoin");
        }

        @Override
        public Service getService(boolean testnet) {
            String contractAddress = coins.attr("contract.address", getLabel(), testnet);
            if (testnet) {
                return new Service.Multi(new Service[]{
                    new EtherscanAPI("https://api-testnet.bscscan.com/api", contractAddress),
                });
            } else {
                return new Service.Multi(new Service[]{
                    new EtherscanAPI("https://api.bscscan.com/api", contractAddress),
                });
            }
        }
    }

    private static class BEP20_BasicAttentionToken extends BEP20Token {
        @Override
        public String getName() {
            return "Basic Attention Token";
        }

        @Override
        public String getLabel() {
            return "bep20-basicattentiontoken";
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

    private static class BEP20_Bitcoin extends BEP20Token {
        @Override
        public String getName() {
            return "Bitcoin";
        }

        @Override
        public String getLabel() {
            return "bep20-bitcoin";
        }

        @Override
        public String getCode() {
            return "BTC";
        }

        @Override
        public String getSymbol() {
            return "฿";
        }
    }

    private static class BEP20_BitcoinCash extends BEP20Token {
        @Override
        public String getName() {
            return "Bitcoin Cash";
        }

        @Override
        public String getLabel() {
            return "bep20-bitcoincash";
        }

        @Override
        public String getCode() {
            return "BCH";
        }

        @Override
        public String getSymbol() {
            return null;
        }
    }

    private static class BEP20_Cardano extends BEP20Token {
        @Override
        public String getName() {
            return "Cardano";
        }

        @Override
        public String getLabel() {
            return "bep20-cardano";
        }

        @Override
        public String getCode() {
            return "ADA";
        }

        @Override
        public String getSymbol() {
            return null;
        }
    }

    private static class BEP20_Chainlink extends BEP20Token {
        @Override
        public String getName() {
            return "Chainlink";
        }

        @Override
        public String getLabel() {
            return "bep20-chainlink";
        }

        @Override
        public String getCode() {
            return "LINK";
        }

        @Override
        public String getSymbol() {
            return null;
        }
    }

    private static class BEP20_Dai extends BEP20Token {
        @Override
        public String getName() {
            return "Dai";
        }

        @Override
        public String getLabel() {
            return "bep20-dai";
        }

        @Override
        public String getCode() {
            return "DAI";
        }

        @Override
        public String getSymbol() {
            return null;
        }
    }

    private static class BEP20_Dogecoin extends BEP20Token {
        @Override
        public String getName() {
            return "Dogecoin";
        }

        @Override
        public String getLabel() {
            return "bep20-dogecoin";
        }

        @Override
        public String getCode() {
            return "DOGE";
        }

        @Override
        public String getSymbol() {
            return "Ð";
        }
    }

    private static class BEP20_EOS extends BEP20Token {
        @Override
        public String getName() {
            return "EOS";
        }

        @Override
        public String getLabel() {
            return "bep20-eos";
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

    private static class BEP20_Ethereum extends BEP20Token {
        @Override
        public String getName() {
            return "Ethereum";
        }

        @Override
        public String getLabel() {
            return "bep20-ethereum";
        }

        @Override
        public String getCode() {
            return "ETH";
        }

        @Override
        public String getSymbol() {
            return "Ξ";
        }
    }

    private static class BEP20_EthereumClassic extends BEP20Token {
        @Override
        public String getName() {
            return "Ethereum Classic";
        }

        @Override
        public String getLabel() {
            return "bep20-ethereumclassic";
        }

        @Override
        public String getCode() {
            return "ETC";
        }

        @Override
        public String getSymbol() {
            return null;
        }
    }

    private static class BEP20_Litecoin extends BEP20Token {
        @Override
        public String getName() {
            return "Litecoin";
        }

        @Override
        public String getLabel() {
            return "bep20-litecoin";
        }

        @Override
        public String getCode() {
            return "LTC";
        }

        @Override
        public String getSymbol() {
            return "Ł";
        }
    }

    private static class BEP20_Maker extends BEP20Token {
        @Override
        public String getName() {
            return "Maker";
        }

        @Override
        public String getLabel() {
            return "bep20-maker";
        }

        @Override
        public String getCode() {
            return "MKR";
        }

        @Override
        public String getSymbol() {
            return null;
        }
    }

    private static class BEP20_Ripple extends BEP20Token {
        @Override
        public String getName() {
            return "Ripple";
        }

        @Override
        public String getLabel() {
            return "bep20-ripple";
        }

        @Override
        public String getCode() {
            return "XRP";
        }

        @Override
        public String getSymbol() {
            return null;
        }
    }

    private static class BEP20_Tether extends BEP20Token {
        @Override
        public String getName() {
            return "Tether";
        }

        @Override
        public String getLabel() {
            return "bep20-tether";
        }

        @Override
        public String getCode() {
            return "USDT";
        }

        @Override
        public String getSymbol() {
            return null;
        }
    }

    private static class BEP20_USDCoin extends BEP20Token {
        @Override
        public String getName() {
            return "USD Coin";
        }

        @Override
        public String getLabel() {
            return "bep20-usdcoin";
        }

        @Override
        public String getCode() {
            return "USDC";
        }

        @Override
        public String getSymbol() {
            return null;
        }
    }

    private static class BEP20_Zcash extends BEP20Token {
        @Override
        public String getName() {
            return "Zcash";
        }

        @Override
        public String getLabel() {
            return "bep20-zcash";
        }

        @Override
        public String getCode() {
            return "ZEC";
        }

        @Override
        public String getSymbol() {
            return null;
        }
    }

    private static class BEP20_Zilliqa extends BEP20Token {
        @Override
        public String getName() {
            return "Zilliqa";
        }

        @Override
        public String getLabel() {
            return "bep20-zilliqa";
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
            return findCoin("waves");
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
