package com.cashuwallet.android.db;

import androidx.room.TypeConverter;

import java.math.BigInteger;

public class AppTypeConverters {

    @TypeConverter
    public static String fromBigInteger(BigInteger amount) {
        if (amount == null) return null;
        return amount.toString(Character.MAX_RADIX);
    }

    @TypeConverter
    public static BigInteger toBigInteger(String amount) {
        if (amount == null) return null;
        return new BigInteger(amount, Character.MAX_RADIX);
    }

}
