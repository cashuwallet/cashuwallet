package com.cashuwallet.android.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.cashuwallet.android.crypto.Coin;
import com.cashuwallet.android.crypto.Coins;

import java.math.BigInteger;

@Entity(
        tableName = "unspent",
        indices = {
            @Index(value = {"coin", "address", "hash", "index"}, unique = true),
        }
)
public class Unspent {

    @PrimaryKey(autoGenerate = true)
    protected int id;

    @ColumnInfo(name = "coin") @NonNull
    public final String coin;

    @ColumnInfo(name = "address") @NonNull
    public final String address;

    @ColumnInfo(name = "hash") @NonNull
    public final String hash;

    @ColumnInfo(name = "index") @NonNull
    public final int index;

    @ColumnInfo(name = "amount") @NonNull
    protected final BigInteger amount;

    public Unspent(String coin, String address, String hash, int index, BigInteger amount) {
        this.coin = coin;
        this.address = address;
        this.hash = hash;
        this.index = index;
        this.amount = amount;
    }

    public int id() {
        return id;
    }

    public Coin getCoin() {
        return Coins.findCoin(coin);
    }

    public BigInteger getAmount() {
        return amount;
    }

    public void refresh(AppDao dao) {
        Transaction transaction = dao.findTransaction(this.id);
    }

}
