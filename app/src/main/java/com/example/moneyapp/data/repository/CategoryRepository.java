package com.example.moneyapp.data.repository;

import android.app.Application;
import android.content.Context;
import com.example.moneyapp.data.local.AppDatabase;
import com.example.moneyapp.data.local.dao.CategoryDao;
import com.example.moneyapp.data.local.entity.Category;
import com.example.moneyapp.utils.PreferenceManager;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CategoryRepository {
    private final CategoryDao categoryDao;
    private final ExecutorService executor;
    private final String currentUserId;

    public interface CategoryCallback {
        void onSuccess(List<Category> categories);
        void onError(String message);
    }

    public interface Callback<T> {
        void onResult(T result);
    }

    public CategoryRepository(Context context) {
        categoryDao = AppDatabase.getInstance(context).categoryDao();
        executor = Executors.newSingleThreadExecutor();
        currentUserId = PreferenceManager.getInstance(context).getUserID();
    }

    // type: 1=income(thu), 2=expense(chi) — theo Category entity
    public void getAllCategoriesByType(int type, Callback<List<Category>> callback) {
        executor.execute(() -> {
            List<Category> categories = categoryDao.getCategoriesByTypeAndUserId(currentUserId, type);
            callback.onResult(categories);
        });
    }

    public void getCategoriesByType(int type, CategoryCallback callback) {
        executor.execute(() -> {
            try {
                List<Category> list = categoryDao.getCategoriesByTypeAndUserId(currentUserId, type);
                callback.onSuccess(list);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public void insertCategory(Category category, Runnable onComplete) {
        executor.execute(() -> {
            categoryDao.insertCategory(category);
            if (onComplete != null) onComplete.run();
        });
    }

    public void updateCategory(Category category, Runnable onComplete) {
        executor.execute(() -> {
            categoryDao.updateCategory(category);
            if (onComplete != null) onComplete.run();
        });
    }
    
    public void deleteCategory(Category category, Runnable onComplete) {
        executor.execute(() -> {
            categoryDao.deleteCategory(category);
            if (onComplete != null) onComplete.run();
        });
    }
}
