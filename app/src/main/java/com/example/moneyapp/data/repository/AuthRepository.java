package com.example.moneyapp.data.repository;

import android.app.Application;

import com.example.moneyapp.data.local.AppDatabase;
import com.example.moneyapp.data.local.dao.UserDao;
import com.example.moneyapp.data.local.entity.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthRepository {
    private final UserDao userDao;
    private final ExecutorService executorService;

    public interface AuthCallback {
        void onSuccess(User user);

        void onError(String message);
    }

    public AuthRepository(Application application) {
        AppDatabase appDatabase = AppDatabase.getInstance(application);
        this.userDao = appDatabase.userDao();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void loginByEmail(String email, String password, AuthCallback callback) {
        executorService.execute(() -> {
            try {
                User user = userDao.getUserByEmail(email);
                if (user != null && user.getPassword().equals(password)) {
                    callback.onSuccess(user);
                } else {
                    callback.onError("Invalid email or password");
                }
            } catch (Exception e) {
                callback.onError("System error: " + e.getMessage());
            }
        });
    }

    public void loginByPhoneNumber(String phoneNumber, String password, AuthCallback callback) {
        executorService.execute(() -> {
            try {
                User user = userDao.getUserByPhoneNumber(phoneNumber);
                if (user != null && user.getPassword().equals(password)) {
                    callback.onSuccess(user);
                } else {
                    callback.onError("Invalid phone number or password");
                }
            } catch (Exception e) {
                callback.onError("System error: " + e.getMessage());
            }
        });
    }

    public void register(User user, AuthCallback callback) {
        executorService.execute(() -> {
            try {
                User existingUserByEmail = userDao.getUserByEmail(user.getEmail());
                User existingUserByPhoneNumber = userDao.getUserByPhoneNumber(user.getPhoneNumber());
                if (existingUserByEmail != null) {
                    callback.onError("Email already exists");
                }
                else if (existingUserByPhoneNumber != null) {
                    callback.onError("Phone number already exists");
                }
                else {
                    userDao.insertUser(user);
                    callback.onSuccess(user);
                }
            } catch (Exception e) {
                callback.onError("System error: " + e.getMessage());
            }
        });
    }
}
