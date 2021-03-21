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
    tableName = "chain",
    indices = {
        @Index(value = {"label"}, unique = true),
    }
)
public class Chain {

    @PrimaryKey(autoGenerate = true)
    protected int id;

    @ColumnInfo(name = "label") @NonNull
    public final String label;

    @ColumnInfo(name = "height") @NonNull
    protected long height = 0;

    @ColumnInfo(name = "fee") @NonNull
    protected BigInteger fee = BigInteger.ZERO;

    @ColumnInfo(name = "hei_time") @NonNull
    public int heiTime = 0;

    @ColumnInfo(name = "hei_last_sync") @NonNull
    public int heiLastSync = 0;

    @ColumnInfo(name = "fee_time") @NonNull
    public int feeTime = 0;

    @ColumnInfo(name = "fee_last_sync") @NonNull
    public int feeLastSync = 0;

    public Chain(String label) {
        this.label = label;
    }

    public int id() {
        return id;
    }

    public Coin getCoin() {
        return Coins.findCoin(label);
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public BigInteger getFee() {
        return fee;
    }

    public void setFee(BigInteger fee) {
        this.fee = fee;
    }

    public void refresh(AppDao dao) {
        Chain chain = dao.findChain(this.id);
        height = chain.height;
        heiTime = chain.heiTime;
        heiLastSync = chain.heiLastSync;
        fee = chain.fee;
        feeTime = chain.feeTime;
        feeLastSync = chain.feeLastSync;
    }

}
