package com.example.moneyapp.data.repository;

import android.content.Context;
import com.example.moneyapp.data.local.AppDatabase;
import com.example.moneyapp.data.local.dao.CategoryDao;
import com.example.moneyapp.data.local.entity.Category;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CategoryRepository {
    private final CategoryDao categoryDao;
    private final ExecutorService executor;

    public CategoryRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        categoryDao = db.categoryDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public void getAllCategoriesByType(int type, Callback<List<Category>> callback) {
        executor.execute(() -> {
            List<Category> categories = categoryDao.getCategoriesByType(type);
            callback.onResult(categories);
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

    public interface Callback<T> {
        void onResult(T result);
    }
}
