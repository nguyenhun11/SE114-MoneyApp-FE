package com.example.moneyapp.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.moneyapp.data.local.entity.Transaction;

import java.util.Date;
import java.util.List;

@Dao
public interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTransaction(Transaction transaction);

    @Query("select * from transactions where id = :id limit 1")
    Transaction getTransactionById(String id);

    @Query("SELECT transactions.* FROM transactions " +
            // 1. Dùng cầu nối: Móc bảng giao dịch vào bảng tài khoản
            "INNER JOIN accounts ON transactions.sourceAccountId = accounts.id " +
            // 2. Lọc theo UserID (Nằm ở bảng accounts)
            "WHERE accounts.userId = :userID AND " +
            // 3. Các bộ lọc bắt buộc (Nằm ở bảng transactions)
            "transactions.transactionType = :type AND " +
            "transactions.date BETWEEN :startDate AND :endDate AND " +
            // 4. Các bộ lọc tùy chọn (Cho phép truyền Null)
            "(:accountID IS NULL OR transactions.sourceAccountId = :accountID) AND " +
            "(:categoryID IS NULL OR transactions.categoryId = :categoryID)")
    List<Transaction> getAllTransactions(
            int type,
            Date startDate,
            Date endDate,
            String accountID,
            String categoryID,
            String userID
    );


    @Update
    void updateTransaction(Transaction transaction);

    @Delete
    void deleteTransaction(Transaction transaction);

}
