package com.example.moneyapp.data.local;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.moneyapp.data.local.dao.AccountDao;
import com.example.moneyapp.data.local.dao.CategoryDao;
import com.example.moneyapp.data.local.dao.TransactionDao;
import com.example.moneyapp.data.local.dao.UserDao;
import com.example.moneyapp.data.local.entity.Account;
import com.example.moneyapp.data.local.entity.Category;
import com.example.moneyapp.data.local.entity.Transaction;
import com.example.moneyapp.data.local.entity.User;
import com.example.moneyapp.utils.DateConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

// Tăng version lên 2 hoặc giữ 1 nếu bạn muốn xóa sạch data cũ bằng fallbackToDestructiveMigration
@Database(entities = {
        User.class,
        Category.class,
        Account.class,
        Transaction.class
}, version = 2, exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    
    public abstract UserDao userDao();
    public abstract CategoryDao categoryDao();
    public abstract AccountDao accountDao();
    public abstract TransactionDao transactionDao();

    private static volatile AppDatabase INSTANCE;
    private static final String DATABASE_NAME = "moneyapp_db";

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .addCallback(roomCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            Executors.newSingleThreadExecutor().execute(() -> {
                CategoryDao categoryDao = INSTANCE.categoryDao();
                // Chỉ chèn nếu chưa có hạng mục nào
                if (categoryDao.getCategoriesByType(1).isEmpty() && categoryDao.getCategoriesByType(2).isEmpty()) {
                    List<Category> defaultCategories = new ArrayList<>();
                    // Nhóm: Sinh hoạt
                    defaultCategories.add(new Category(null, "Ăn uống", 0.0, "ic_transaction", "#FF9800", "Sinh hoạt", 2, true, false));
                    defaultCategories.add(new Category(null, "Mua sắm", 0.0, "ic_transaction", "#E91E63", "Sinh hoạt", 2, true, false));
                    defaultCategories.add(new Category(null, "Tiền điện", 0.0, "ic_transaction", "#F44336", "Sinh hoạt", 2, false, false));
                    defaultCategories.add(new Category(null, "Tiền nước", 0.0, "ic_transaction", "#2196F3", "Sinh hoạt", 2, false, false));
                    defaultCategories.add(new Category(null, "Internet", 0.0, "ic_transaction", "#3F51B5", "Sinh hoạt", 2, false, false));
                    
                    // Nhóm: Di chuyển
                    defaultCategories.add(new Category(null, "Xăng xe", 0.0, "ic_transaction", "#2196F3", "Di chuyển", 2, true, false));
                    defaultCategories.add(new Category(null, "Bus/Grab", 0.0, "ic_transaction", "#4CAF50", "Di chuyển", 2, true, false));
                    defaultCategories.add(new Category(null, "Sửa xe", 0.0, "ic_transaction", "#9C27B0", "Di chuyển", 2, false, false));
                    defaultCategories.add(new Category(null, "Bảo hiểm xe", 0.0, "ic_transaction", "#607D8B", "Di chuyển", 2, false, false));
                    defaultCategories.add(new Category(null, "Gửi xe", 0.0, "ic_transaction", "#795548", "Di chuyển", 2, false, false));

                    // Hạng mục THU NHẬP (Type = 1)
                    defaultCategories.add(new Category(null, "Tiền lương", 0.0, "ic_transaction", "#4CAF50", "Thu nhập", 1, true, false));
                    defaultCategories.add(new Category(null, "Tiền thưởng", 0.0, "ic_transaction", "#FFEB3B", "Thu nhập", 1, true, false));
                    defaultCategories.add(new Category(null, "Quà tặng", 0.0, "ic_transaction", "#FF4081", "Thu nhập", 1, false, false));
                    defaultCategories.add(new Category(null, "Lãi tiết kiệm", 0.0, "ic_transaction", "#00BCD4", "Thu nhập", 1, false, false));

                    categoryDao.insertCategories(defaultCategories);
                }
            });
        }
    };
}
