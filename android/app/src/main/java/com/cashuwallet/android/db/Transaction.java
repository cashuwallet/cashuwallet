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
    tableName = "transaction",
    indices = {
        @Index(value = {"label", "address", "hash"}, unique = true),
    }
)
public class Transaction {

    public enum Type { NONE, INCOMING, OUTGOING }

    @PrimaryKey(autoGenerate = true)
    protected int id;

    @ColumnInfo(name = "label") @NonNull
    public final String label;

    @ColumnInfo(name = "address") @NonNull
    public final String address;

    @ColumnInfo(name = "hash") @NonNull
    public final String hash;

    @ColumnInfo(name = "amount") @NonNull
    protected final BigInteger amount;

    @ColumnInfo(name = "fee") @NonNull
    protected final BigInteger fee;

    @ColumnInfo(name = "block") @NonNull
    protected long block = 0;

    @ColumnInfo(name = "time") @NonNull
    protected int time = 0;

    @ColumnInfo(name = "confirmed") @NonNull
    public boolean confirmed = false;

    public Transaction(String label, String address, String hash, BigInteger amount, BigInteger fee) {
        this.label = label;
        this.address = address;
        this.hash = hash;
        this.amount = amount;
        this.fee = fee;
    }

    public int id() {
        return id;
    }

    public Coin getCoin() {
        return Coins.findCoin(label);
    }

    public BigInteger getAmount() {
        return amount;
    }

    public BigInteger getFee() {
        return fee;
    }

    public BigInteger getAbsAmount() {
        return amount.abs();
    }

    public Type getType() {
        switch (amount.compareTo(BigInteger.ZERO)) {
            case 0: return Type.NONE;
            case 1: return Type.INCOMING;
            case -1: return Type.OUTGOING;
        }
        return null;
    }

    public long getBlock() {
        return block;
    }

    public void setBlock(long block) {
        this.block = block;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void refresh(AppDao dao) {
        Transaction transaction = dao.findTransaction(this.id);
        block = transaction.block;
        time = transaction.time;
        confirmed = transaction.confirmed;
    }

    public Transaction incrementAmount(BigInteger amount) {
        Transaction transaction = new Transaction(label, address, hash, this.amount.add(amount), fee);
        transaction.id = id;
        transaction.block = block;
        transaction.time = time;
        transaction.confirmed = confirmed;
        return transaction;
    }

}
