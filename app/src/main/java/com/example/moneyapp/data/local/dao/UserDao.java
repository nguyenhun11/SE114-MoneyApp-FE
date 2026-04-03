package com.example.moneyapp.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.moneyapp.data.local.entity.User;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    User getUserById(String userId);

    @Update
    void updateUser(User user);

    @Query("DELETE FROM users")
    void clearUser();
}
