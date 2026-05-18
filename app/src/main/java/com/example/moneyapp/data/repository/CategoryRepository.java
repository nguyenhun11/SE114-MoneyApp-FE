package com.example.moneyapp.data.repository;

import android.app.Application;
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

    public CategoryRepository(Application application) {
        categoryDao = AppDatabase.getInstance(application).categoryDao();
        executor = Executors.newSingleThreadExecutor();
        currentUserId = PreferenceManager.getInstance(application).getUserID();
    }

    // type: 1=income(thu), 2=expense(chi) — theo Category entity
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
}