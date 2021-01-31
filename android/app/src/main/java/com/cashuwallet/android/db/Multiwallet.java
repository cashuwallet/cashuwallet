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
    tableName = "multiwallet",
    indices = {
            @Index(value = {"coin", "address"}, unique = true),
            @Index(value = {"coin", "account"}, unique = true),
    }
)
public class Multiwallet {

    @PrimaryKey(autoGenerate = true)
    protected int id;

    @ColumnInfo(name = "coin") @NonNull
    public final String coin;

    @ColumnInfo(name = "address") @NonNull
    public final String address;

    @ColumnInfo(name = "account") @NonNull
    public final int account;

    @ColumnInfo(name = "balance") @NonNull
    protected BigInteger balance = BigInteger.ZERO;

    @ColumnInfo(name = "confirmed") @NonNull
    public boolean confirmed = true;

    @ColumnInfo(name = "txn_count") @NonNull
    public int txnCount = 0;

    public Multiwallet(String coin, String address, int account) {
        this.coin = coin;
        this.address = address;
        this.account = account;
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
        Multiwallet multiwallet = dao.findMultiwallet(this.id);
        balance = multiwallet.balance;
        confirmed = multiwallet.confirmed;
    }

}
