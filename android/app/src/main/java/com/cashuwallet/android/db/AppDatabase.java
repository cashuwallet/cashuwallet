package com.cashuwallet.android.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Chain.class, Multiwallet.class, Wallet.class, Transaction.class, Unspent.class}, version = 1, exportSchema = false)
@TypeConverters({AppTypeConverters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract AppDao appDao();

}
