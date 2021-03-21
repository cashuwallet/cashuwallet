package com.cashuwallet.android.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AppDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void createChain(Chain chain);

    @Update
    void saveChain(Chain chain);

    @Query("SELECT * FROM `chain` WHERE id = :id LIMIT 1")
    Chain findChain(int id);

    @Query("SELECT * FROM `chain` WHERE `label` = :label LIMIT 1")
    Chain findChain(String label);

    @Query("SELECT * FROM `chain`")
    List<Chain> findChains();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void createMultiwallet(Multiwallet multiwallet);

    @Update
    void saveMultiwallet(Multiwallet wallet);

    @Query("SELECT * FROM `multiwallet` WHERE id = :id LIMIT 1")
    Multiwallet findMultiwallet(int id);

    @Query("SELECT * FROM `multiwallet` WHERE `label` = :label AND `address` = :address LIMIT 1")
    Multiwallet findMultiwallet(String label, String address);

    @Query("SELECT * FROM `multiwallet` WHERE `label` = :label AND `account` = :account LIMIT 1")
    Multiwallet findMultiwallet(String label, int account);

    @Query("SELECT * FROM `multiwallet` WHERE `account` = :account")
    List<Multiwallet> findMultiwallets(int account);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void createWallet(Wallet wallet);

    @Update
    void saveWallet(Wallet wallet);

    @Query("SELECT * FROM `wallet` WHERE id = :id LIMIT 1")
    Wallet findWallet(int id);

    @Query("SELECT * FROM `wallet` WHERE `label` = :label AND `address` = :address LIMIT 1")
    Wallet findWallet(String label, String address);

    @Query("SELECT * FROM `wallet` WHERE `label` = :label AND `account` = :account AND `change` = :change AND `index` = :index LIMIT 1")
    Wallet findWallet(String label, int account, boolean change, int index);

    @Query("SELECT * FROM `wallet` WHERE `label` = :label AND `account` = :account")
    List<Wallet> findWallets(String label, int account);

    @Query("SELECT COUNT(*) FROM `wallet` WHERE `label` = :label AND `account` = :account")
    int walletCount(String label, int account);

    @Query("SELECT MIN(`index`) FROM `wallet` WHERE `label` = :label AND `account` = :account AND `change` = :change AND `txn_count` = 0")
    int nextAccountIndex(String label, int account, boolean change);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void createTransaction(Transaction transaction);

    @Update
    void saveTransaction(Transaction transaction);

    @Query("SELECT * FROM `transaction` WHERE id = :id LIMIT 1")
    Transaction findTransaction(int id);

    @Query("SELECT * FROM `transaction` WHERE `label` = :label AND `address` = :address AND `hash` = :hash LIMIT 1")
    Transaction findTransaction(String label, String address, String hash);

    @Query("SELECT * FROM `transaction` WHERE `label` = :label AND `address` = :address ORDER BY `block` DESC, `amount` LIMIT :limit OFFSET :offset")
    List<Transaction> findTransactions(String label, String address, int offset, int limit);

    @Query("SELECT * FROM `transaction` WHERE `label` = :label AND NOT `confirmed`")
    List<Transaction> findPendingTransactions(String label);

    @Query("SELECT COUNT(*) FROM `transaction` WHERE `label` = :label AND `address` = :address AND NOT `confirmed`")
    int pendingTransactionCount(String label, String address);

    @Query("SELECT COUNT(*) FROM `transaction` WHERE `label` = :label AND `address` = :address")
    int transactionCount(String label, String address);

    @Query("SELECT MAX(`block`) FROM `transaction` WHERE `label` = :label AND `address` = :address AND `confirmed`")
    long transactionHeight(String label, String address);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertUnspents(List<Unspent> unspents);

    @Query("DELETE FROM `unspent` WHERE `label` = :label AND `address` = :address")
    void deleteUnspents(String label, String address);

    @Query("SELECT * FROM `unspent` WHERE  `label` = :label AND `address` = :address")
    List<Unspent> findUnspents(String label, String address);

}
