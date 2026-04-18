package com.example.moneyapp.data.repository;

import android.app.Application;
import android.content.Context;

import com.example.moneyapp.data.local.AppDatabase;
import com.example.moneyapp.data.local.dao.UserDao;
import com.example.moneyapp.data.local.entity.User;
import com.example.moneyapp.utils.PreferenceManager;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthRepository {
    private final UserDao userDao;
    private final ExecutorService executorService;
    private final Context context;
    private final FirebaseAuth mAuth;

    public interface AuthCallback {
        void onSuccess(User user);

        void onError(String message);
    }

    public AuthRepository(Application application) {
        AppDatabase appDatabase = AppDatabase.getInstance(application);
        this.userDao = appDatabase.userDao();
        this.executorService = Executors.newSingleThreadExecutor();
        this.context = application.getApplicationContext();
        this.mAuth = FirebaseAuth.getInstance();
    }

    public void loginByEmail(String email, String password, AuthCallback callback) {
        executorService.execute(() -> {
            try {
                User user = userDao.getUserByEmail(email);
                if (user != null && user.getPassword().equals(password)) {
                    PreferenceManager.getInstance(context).setLoggedIn(true);
                    PreferenceManager.getInstance(context).setUserID(user.getId());
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
                    PreferenceManager.getInstance(context).setLoggedIn(true);
                    PreferenceManager.getInstance(context).setUserID(user.getId());
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
//                else if (existingUserByPhoneNumber != null) {
//                    callback.onError("Phone number already exists");
//                }
                else {
                    userDao.insertUser(user);
                    callback.onSuccess(user);
                }
            } catch (Exception e) {
                callback.onError("System error: " + e.getMessage());
            }
        });
    }
    public void getUserByID(String userID, AuthCallback callback){
        executorService.execute(()->{
            try {
                User user = userDao.getUserById(userID);
                callback.onSuccess(user);
            }
            catch (Exception e){
                callback.onError("System error: " + e.getMessage());
            }
        });
    }

    public void updatePassword(String userID, String oldPassword, String newPassword, AuthCallback callback) {
        executorService.execute(() -> {
            try {
                User user = userDao.getUserById(userID);
                if (user != null && user.getPassword().equals(oldPassword)) {
                    user.setPassword(newPassword);
                    userDao.insertUser(user); // Room dùng @Insert(onConflict = OnConflictStrategy.REPLACE)
                    callback.onSuccess(user);
                } else {
                    callback.onError("Mật khẩu hiện tại không chính xác");
                }
            } catch (Exception e) {
                callback.onError("Lỗi hệ thống: " + e.getMessage());
            }
        });
    }

    public void updateUser(User user, AuthCallback callback) {
        executorService.execute(() -> {
            try {
                userDao.updateUser(user);
                callback.onSuccess(user);
            } catch (Exception e) {
                callback.onError("Lỗi cập nhật: " + e.getMessage());
            }
        });
    }

    public void deleteUser(String userID, AuthCallback callback) {
        executorService.execute(() -> {
            try {
                User user = userDao.getUserById(userID);
                if (user != null) {
                    userDao.deleteUser(user);
                    // Xóa thông tin đăng nhập trong Preference
                    PreferenceManager.getInstance(context).clear();
                    callback.onSuccess(null);
                } else {
                    callback.onError("Không tìm thấy người dùng");
                }
            } catch (Exception e) {
                callback.onError("Lỗi hệ thống: " + e.getMessage());
            }
        });
    }

    public void sendPasswordResetEmail(String email, AuthCallback callback) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(null);
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Gửi email thất bại";
                        callback.onError(error);
                    }
                });
    }
}
