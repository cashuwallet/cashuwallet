package com.cashuwallet.android.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface AppDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void createChain(Chain chain);

    @Update
    void saveChain(Chain chain);

    @Query("SELECT * FROM `chain` WHERE id = :id LIMIT 1")
    Chain findChain(int id);

    @Query("SELECT * FROM `chain` WHERE `coin` = :coin LIMIT 1")
    Chain findChain(String coin);

    @Query("SELECT * FROM `chain`")
    List<Chain> findChains();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void createMultiwallet(Multiwallet multiwallet);

    @Update
    void saveMultiwallet(Multiwallet wallet);

    @Query("SELECT * FROM `multiwallet` WHERE id = :id LIMIT 1")
    Multiwallet findMultiwallet(int id);

    @Query("SELECT * FROM `multiwallet` WHERE `coin` = :coin AND `address` = :address LIMIT 1")
    Multiwallet findMultiwallet(String coin, String address);

    @Query("SELECT * FROM `multiwallet` WHERE `coin` = :coin AND `account` = :account LIMIT 1")
    Multiwallet findMultiwallet(String coin, int account);

    @Query("SELECT * FROM `multiwallet` WHERE `account` = :account")
    List<Multiwallet> findMultiwallets(int account);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void createWallet(Wallet wallet);

    @Update
    void saveWallet(Wallet wallet);

    @Query("SELECT * FROM `wallet` WHERE id = :id LIMIT 1")
    Wallet findWallet(int id);

    @Query("SELECT * FROM `wallet` WHERE `coin` = :coin AND `address` = :address LIMIT 1")
    Wallet findWallet(String coin, String address);

    @Query("SELECT * FROM `wallet` WHERE `coin` = :coin AND `account` = :account AND `change` = :change AND `index` = :index LIMIT 1")
    Wallet findWallet(String coin, int account, boolean change, int index);

    @Query("SELECT * FROM `wallet` WHERE `coin` = :coin AND `account` = :account")
    List<Wallet> findWallets(String coin, int account);

    @Query("SELECT COUNT(*) FROM `wallet` WHERE `coin` = :coin AND `account` = :account")
    int walletCount(String coin, int account);

    @Query("SELECT MIN(`index`) FROM `wallet` WHERE `coin` = :coin AND `account` = :account AND `change` = :change AND `txn_count` = 0")
    int nextAccountIndex(String coin, int account, boolean change);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void createTransaction(Transaction transaction);

    @Update
    void saveTransaction(Transaction transaction);

    @Query("SELECT * FROM `transaction` WHERE id = :id LIMIT 1")
    Transaction findTransaction(int id);

    @Query("SELECT * FROM `transaction` WHERE `coin` = :coin AND `address` = :address AND `hash` = :hash LIMIT 1")
    Transaction findTransaction(String coin, String address, String hash);

    @Query("SELECT * FROM `transaction` WHERE `coin` = :coin AND `address` = :address ORDER BY `block` DESC, `amount` LIMIT :limit OFFSET :offset")
    List<Transaction> findTransactions(String coin, String address, int offset, int limit);

    @Query("SELECT * FROM `transaction` WHERE `coin` = :coin AND NOT `confirmed`")
    List<Transaction> findPendingTransactions(String coin);

    @Query("SELECT COUNT(*) FROM `transaction` WHERE `coin` = :coin AND `address` = :address AND NOT `confirmed`")
    int pendingTransactionCount(String coin, String address);

    @Query("SELECT COUNT(*) FROM `transaction` WHERE `coin` = :coin AND `address` = :address")
    int transactionCount(String coin, String address);

    @Query("SELECT MAX(`block`) FROM `transaction` WHERE `coin` = :coin AND `address` = :address AND `confirmed`")
    long transactionHeight(String coin, String address);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertUnspents(List<Unspent> unspents);

    @Query("DELETE FROM `unspent` WHERE `coin` = :coin AND `address` = :address")
    void deleteUnspents(String coin, String address);

    @Query("SELECT * FROM `unspent` WHERE  `coin` = :coin AND `address` = :address")
    List<Unspent> findUnspents(String coin, String address);

}
