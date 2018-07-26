package com.cashuwallet.android.crypto;

public interface Coin {
    enum AddressMode { ACCOUNT, UTXO };
    String getName();
    String getLabel();
    String getCode();
    String getSymbol();
    int getDecimals();
    int getBlockTime();
    int getMinConf();
    AddressMode getMode();
    Coin getFeeCoin();
    Service getService(boolean testnet);
    String getTransactionUrl(String hash, boolean testnet);
}
