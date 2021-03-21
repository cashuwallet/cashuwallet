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
        tableName = "unspent",
        indices = {
            @Index(value = {"label", "address", "hash", "index"}, unique = true),
        }
)
public class Unspent {

    @PrimaryKey(autoGenerate = true)
    protected int id;

    @ColumnInfo(name = "label") @NonNull
    public final String label;

    @ColumnInfo(name = "address") @NonNull
    public final String address;

    @ColumnInfo(name = "hash") @NonNull
    public final String hash;

    @ColumnInfo(name = "index") @NonNull
    public final int index;

    @ColumnInfo(name = "amount") @NonNull
    protected final BigInteger amount;

    public Unspent(String label, String address, String hash, int index, BigInteger amount) {
        this.label = label;
        this.address = address;
        this.hash = hash;
        this.index = index;
        this.amount = amount;
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

    public void refresh(AppDao dao) {
        Transaction transaction = dao.findTransaction(this.id);
    }

}
