package com.example.moneyapp.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.moneyapp.data.local.dao.PendingTransactionDao;
import com.example.moneyapp.data.local.entity.PendingTransaction;
import com.example.moneyapp.utils.DateConverter;

@Database(entities = {
        PendingTransaction.class
}, version = 2, exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    
    public abstract PendingTransactionDao pendingTransactionDao();

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

