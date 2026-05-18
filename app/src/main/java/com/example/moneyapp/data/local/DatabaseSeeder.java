package com.example.moneyapp.data.local;

import android.app.Application;
import com.example.moneyapp.data.local.dao.AccountDao;
import com.example.moneyapp.data.local.dao.CategoryDao;
import com.example.moneyapp.data.local.entity.Account;
import com.example.moneyapp.data.local.entity.Category;
import com.example.moneyapp.utils.PreferenceManager;
import java.util.concurrent.Executors;

public class DatabaseSeeder {

    public static void seedIfEmpty(Application application) {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(application);
            String userId = PreferenceManager.getInstance(application).getUserID();

            if (userId == null || userId.isEmpty()) return;

            seedAccounts(db.accountDao(), userId);
            seedCategories(db.categoryDao(), userId);
        });
    }

    private static void seedAccounts(AccountDao dao, String userId) {
        if (dao.countByUserId(userId) > 0) return; // đã có rồi, bỏ qua

        dao.insertAccount(new Account(userId, "Tiền mặt", 0.0, "ic_cash",    "#4CAF50"));
        dao.insertAccount(new Account(userId, "Momo",     0.0, "ic_momo",    "#E91E8C"));
        dao.insertAccount(new Account(userId, "Ngân hàng",0.0, "ic_bank",    "#2196F3"));
    }

    private static void seedCategories(CategoryDao dao, String userId) {
        if (dao.countByUserId(userId) > 0) return; // đã có rồi, bỏ qua

        // type: 2 = expense (chi)
        dao.insertCategory(new Category(userId, "Ăn uống",   null, "ic_food",     "#FF9800", 2, false));
        dao.insertCategory(new Category(userId, "Sinh hoạt", null, "ic_home",     "#9C27B0", 2, false));
        dao.insertCategory(new Category(userId, "Di chuyển", null, "ic_transport","#2196F3", 2, false));
        dao.insertCategory(new Category(userId, "Mua sắm",   null, "ic_shopping", "#E91E63", 2, false));
        dao.insertCategory(new Category(userId, "Giải trí",  null, "ic_entertain","#607D8B", 2, false));

        // type: 1 = income (thu)
        dao.insertCategory(new Category(userId, "Lương",     null, "ic_salary",   "#4CAF50", 1, false));
        dao.insertCategory(new Category(userId, "Thưởng",    null, "ic_bonus",    "#FF5722", 1, false));
        dao.insertCategory(new Category(userId, "Khác",      null, "ic_other",    "#9E9E9E", 1, false));
    }
}