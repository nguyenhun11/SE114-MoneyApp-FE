package com.example.moneyapp.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.moneyapp.data.local.entity.Account;

import java.util.List;

@Dao
public interface AccountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAccount(Account account);

    @Query("SELECT * FROM accounts WHERE id = :id LIMIT 1")
    Account getAccountById(String id);

    @Query("SELECT * FROM accounts WHERE userId = :userId AND (isDeleted IS NULL OR isDeleted = 0)")
    List<Account> getAccountsByUserId(String userId);
    @Query("SELECT COUNT(*) FROM accounts WHERE userId = :userId")
    int countByUserId(String userId);
    @Update
    void updateAccount(Account account);

    @Delete
    void deleteAccount(Account account);
}
