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
            @Index(value = {"label", "address"}, unique = true),
            @Index(value = {"label", "account"}, unique = true),
    }
)
public class Multiwallet {

    @PrimaryKey(autoGenerate = true)
    protected int id;

    @ColumnInfo(name = "label") @NonNull
    public final String label;

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

    public Multiwallet(String label, String address, int account) {
        this.label = label;
        this.address = address;
        this.account = account;
    }

    public int id() {
        return id;
    }

    public Coin getCoin() {
        return Coins.findCoin(label);
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
