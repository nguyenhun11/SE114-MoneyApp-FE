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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCategories(List<Category> categories);

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    Category getCategoryById(String categoryId);

    @Query("SELECT * FROM categories WHERE type = :type AND isDeleted = 0")
    List<Category> getCategoriesByType(int type);

    @Query("SELECT * FROM categories WHERE isFrequent = 1 AND isDeleted = 0")
    List<Category> getFrequentCategories();

    @Query("SELECT DISTINCT groupName FROM categories WHERE type = :type AND isDeleted = 0")
    List<String> getGroupNamesByType(int type);

    @Query("SELECT * FROM categories WHERE type = :type AND groupName = :groupName AND isDeleted = 0")
    List<Category> getCategoriesByGroup(int type, String groupName);

    @Update
    void updateCategory(Category category);

    @Delete
    void deleteCategory(Category category);
}
