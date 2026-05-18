package com.example.moneyapp.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.moneyapp.data.local.entity.Category;
import java.util.List;

@Dao
public interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCategory(Category category);

    // FIX: int → String
    @Query("SELECT * FROM categories WHERE id = :categoryId")
    Category getCategoryById(String categoryId);

    // THÊM: lấy categories theo userId + type
    @Query("SELECT * FROM categories WHERE userId = :userId AND type = :type AND (isDeleted IS NULL OR isDeleted = 0)")
    List<Category> getCategoriesByTypeAndUserId(String userId, int type);
    @Query("SELECT COUNT(*) FROM categories WHERE userId = :userId")
    int countByUserId(String userId);
    @Update
    void updateCategory(Category category);

    @Delete
    void deleteCategory(Category category);
}