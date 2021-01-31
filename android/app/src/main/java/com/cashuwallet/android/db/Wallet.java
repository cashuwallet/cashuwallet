package com.cashuwallet.android.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import com.cashuwallet.android.crypto.Coin;
import com.cashuwallet.android.crypto.Coins;

import java.math.BigInteger;

@Entity(
    tableName = "wallet",
    indices = {
        @Index(value = {"coin", "address"}, unique = true),
        @Index(value = {"coin", "account", "change", "index"}, unique = true),
    }
)
public class Wallet {

    @PrimaryKey(autoGenerate = true)
    protected int id;

    @ColumnInfo(name = "coin") @NonNull
    public final String coin;

    @ColumnInfo(name = "address") @NonNull
    public final String address;

    @ColumnInfo(name = "account") @NonNull
    public final int account;

    @ColumnInfo(name = "change") @NonNull
    public final boolean change;

    @ColumnInfo(name = "index") @NonNull
    public final int index;

    @ColumnInfo(name = "balance") @NonNull
    protected BigInteger balance = BigInteger.ZERO;

    @ColumnInfo(name = "confirmed") @NonNull
    public boolean confirmed = true;

    @ColumnInfo(name = "txn_count") @NonNull
    public int txnCount = 0;

    @ColumnInfo(name = "sequence") @NonNull
    public long sequence = 0;

    @ColumnInfo(name = "bal_time") @NonNull
    public int balTime = 0;

    @ColumnInfo(name = "bal_last_sync") @NonNull
    public int balLastSync = 0;

    @ColumnInfo(name = "txn_time") @NonNull
    public int txnTime = 0;

    @ColumnInfo(name = "txn_last_sync") @NonNull
    public int txnLastSync = 0;

    @ColumnInfo(name = "uns_time") @NonNull
    public int unsTime = 0;

    @ColumnInfo(name = "uns_last_sync") @NonNull
    public int unsLastSync = 0;

    @ColumnInfo(name = "seq_time") @NonNull
    public int seqTime = 0;

    @ColumnInfo(name = "seq_last_sync") @NonNull
    public int seqLastSync = 0;

    public Wallet(String coin, String address, int account, boolean change, int index) {
        this.coin = coin;
        this.address = address;
        this.account = account;
        this.change = change;
        this.index = index;
    }

    public int id() {
        return id;
    }

    public Coin getCoin() {
        return Coins.findCoin(coin);
    }

    public BigInteger getBalance() {
        return balance;
    }

    public void setBalance(BigInteger balance) {
        this.balance = balance;
    }

    public void refresh(AppDao dao) {
        Wallet wallet = dao.findWallet(this.id);
        balance = wallet.balance;
        confirmed = wallet.confirmed;
        txnCount = wallet.txnCount;
        sequence = wallet.sequence;
        balTime = wallet.balTime;
        balLastSync = wallet.balLastSync;
        txnTime = wallet.txnTime;
        txnLastSync = wallet.txnLastSync;
        unsTime = wallet.unsTime;
        unsLastSync = wallet.unsLastSync;
        seqTime = wallet.seqTime;
        seqLastSync = wallet.seqLastSync;
    }

}
