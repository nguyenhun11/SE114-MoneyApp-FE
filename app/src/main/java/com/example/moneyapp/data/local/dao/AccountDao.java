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

    @Query("SELECT * FROM accounts WHERE isDeleted = 0")
    List<Account> getAllAccounts();

    @Query("SELECT * FROM accounts WHERE id = :id LIMIT 1")
    Account getAccountById(String id);

    @Update
    void updateAccount(Account account);

    @Delete
    void deleteAccount(Account account);
}
