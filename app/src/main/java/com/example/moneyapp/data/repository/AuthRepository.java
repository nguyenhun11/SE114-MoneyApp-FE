package com.example.moneyapp.data.repository;

import android.app.Application;
import android.content.Context;

import com.example.moneyapp.data.local.AppDatabase;
import com.example.moneyapp.data.local.dao.UserDao;
import com.example.moneyapp.data.local.entity.User;
import com.example.moneyapp.utils.PreferenceManager;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
        // 1. Đăng nhập qua Firebase trước
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // 2. Nếu Firebase thành công, lấy thông tin User từ Room và đồng bộ mật khẩu
                        executorService.execute(() -> {
                            try {
                                User user = userDao.getUserByEmail(email);
                                if (user != null) {
                                    // Đồng bộ mật khẩu mới từ Firebase vào Room (nếu người dùng vừa đổi mật khẩu)
                                    user.setPassword(password);
                                    userDao.insertUser(user);

                                    PreferenceManager.getInstance(context).setLoggedIn(true);
                                    PreferenceManager.getInstance(context).setUserID(user.getId());
                                    callback.onSuccess(user);
                                } else {
                                    // Trường hợp user có trên Firebase nhưng local bị xóa hoặc mới đổi máy
                                    callback.onError("Tài khoản hợp lệ nhưng không tìm thấy dữ liệu cục bộ.");
                                }
                            } catch (Exception e) {
                                callback.onError("Lỗi hệ thống: " + e.getMessage());
                            }
                        });
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Sai email hoặc mật khẩu";
                        callback.onError(error);
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
        // 1. Đăng ký tài khoản trên Firebase trước
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                        // Lấy UID từ Firebase để đồng bộ
                        String firebaseUid = mAuth.getCurrentUser().getUid();
                        
                        // Cập nhật ID của user bằng firebaseUid từ Firebase
                        user.setId(firebaseUid);
                        
                        // 2. Nếu Firebase thành công, lưu vào Room Local
                        executorService.execute(() -> {
                            try {
                                // Bạn có thể cập nhật ID của user bằng firebaseUid nếu muốn
                                userDao.insertUser(user);
                                callback.onSuccess(user);
                            } catch (Exception e) {
                                callback.onError("Lỗi lưu local: " + e.getMessage());
                            }
                        });
                    } else {
                        // Nếu Firebase báo lỗi (ví dụ: email đã tồn tại, mật khẩu yếu...)
                        String error = task.getException() != null ? task.getException().getMessage() : "Đăng ký thất bại";
                        callback.onError(error);
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
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        
        if (firebaseUser == null || firebaseUser.getEmail() == null) {
            callback.onError("Người dùng chưa đăng nhập hoặc phiên làm việc hết hạn.");
            return;
        }

        // 1. Xác thực lại (Re-authenticate) để đảm bảo an toàn
        AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), oldPassword);

        firebaseUser.reauthenticate(credential).addOnCompleteListener(reAuthTask -> {
            if (reAuthTask.isSuccessful()) {
                // 2. Nếu xác thực mật khẩu cũ đúng, tiến hành cập nhật mật khẩu mới trên Firebase
                firebaseUser.updatePassword(newPassword).addOnCompleteListener(updateTask -> {
                    if (updateTask.isSuccessful()) {
                        // 3. Cập nhật mật khẩu mới vào Room Database Local
                        executorService.execute(() -> {
                            try {
                                User user = userDao.getUserById(userID);
                                if (user != null) {
                                    user.setPassword(newPassword);
                                    userDao.insertUser(user);
                                    callback.onSuccess(user);
                                } else {
                                    callback.onError("Cập nhật Cloud thành công nhưng không tìm thấy dữ liệu Local.");
                                }
                            } catch (Exception e) {
                                callback.onError("Lỗi cập nhật local: " + e.getMessage());
                            }
                        });
                    } else {
                        String error = updateTask.getException() != null ? updateTask.getException().getMessage() : "Lỗi cập nhật mật khẩu trên Firebase";
                        callback.onError(error);
                    }
                });
            } else {
                callback.onError("Mật khẩu hiện tại không chính xác.");
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
