package com.example.moneyapp.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.moneyapp.data.local.entity.Category;

@Dao
public interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCategory(Category category);

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    Category getCategoryById(int categoryId);

    @Update
    void updateCategory(Category category);

    @Delete
    void deleteCategory(Category category);
}
