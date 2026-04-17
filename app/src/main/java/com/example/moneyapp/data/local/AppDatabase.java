package com.example.moneyapp.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.moneyapp.data.local.dao.AccountDao;
import com.example.moneyapp.data.local.dao.CategoryDao;
import com.example.moneyapp.data.local.dao.TransactionDao;
import com.example.moneyapp.data.local.dao.UserDao;
import com.example.moneyapp.data.local.entity.Account;
import com.example.moneyapp.data.local.entity.Category;
import com.example.moneyapp.data.local.entity.Transaction;
import com.example.moneyapp.data.local.entity.User;
import com.example.moneyapp.utils.DateConverter;

// Tăng version lên 2 hoặc giữ 1 nếu bạn muốn xóa sạch data cũ bằng fallbackToDestructiveMigration
@Database(entities = {
        User.class,
        Category.class,
        Account.class,
        Transaction.class
}, version = 1, exportSchema = false)
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
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
