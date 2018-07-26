package com.cashuwallet.android.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

@Database(entities = {Chain.class, Multiwallet.class, Wallet.class, Transaction.class, Unspent.class}, version = 1, exportSchema = false)
@TypeConverters({AppTypeConverters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract AppDao appDao();

}
