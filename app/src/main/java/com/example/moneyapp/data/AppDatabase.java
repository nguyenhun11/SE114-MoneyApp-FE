package com.example.moneyapp.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.moneyapp.data.local.dao.UserDao;
import com.example.moneyapp.data.local.entity.User;
import com.example.moneyapp.utils.DateConverter;

// Tăng version lên 2 hoặc giữ 1 nếu bạn muốn xóa sạch data cũ bằng fallbackToDestructiveMigration
@Database(entities = {User.class}, version = 1, exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    
    public abstract UserDao userDao();
    // Thêm các DAO khác ở đây khi bạn tạo mới, ví dụ:
    // public abstract TransactionDao transactionDao();

    private static volatile AppDatabase INSTANCE;
    private static final String DATABASE_NAME = "moneyapp_db";

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, DATABASE_NAME)
                            .fallbackToDestructiveMigration() // Tự động xóa db cũ nếu lệch version (phù hợp khi đang dev)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
