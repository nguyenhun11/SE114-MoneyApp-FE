package com.example.moneyapp.data.repository;

import android.app.Application;
import android.content.Context;
import androidx.annotation.NonNull;
import com.example.moneyapp.data.local.PreferenceManager;
import com.example.moneyapp.data.remote.api.ApiService;
import com.example.moneyapp.data.remote.api.RetrofitClient;
import com.example.moneyapp.data.remote.request.ChangePasswordRequest;
import com.example.moneyapp.data.remote.request.LoginRequest;
import com.example.moneyapp.data.remote.request.LogoutRequest;
import com.example.moneyapp.data.remote.request.RegisterRequest;
import com.example.moneyapp.data.remote.response.AuthResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private final ApiService apiService;
    private final Context context;

    public interface AuthCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    public AuthRepository(Application application) {
        this.context = application.getApplicationContext();
        this.apiService = RetrofitClient.getInstance(context).create(ApiService.class);
    }

    public void loginByEmail(String email, String password, AuthCallback<Integer> callback) {
        LoginRequest request = new LoginRequest(email, password);
        apiService.login(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse auth = response.body();

                    // Lưu thông tin đăng nhập vào SharedPreferences
                    PreferenceManager prefs = PreferenceManager.getInstance(context);
                    prefs.setLoggedIn(true);
                    prefs.setUserID(String.valueOf(auth.getId()));
                    prefs.setUserName(auth.getName());
                    prefs.setUserEmail(auth.getEmail());
                    prefs.setToken(auth.getToken());
                    prefs.setRefreshToken(auth.getRefreshToken());

                    callback.onSuccess(auth.getId());
                } else {
                    callback.onError("Đăng nhập thất bại: Sai email hoặc mật khẩu");
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void register(String name, String email, String password, AuthCallback<Integer> callback) {
        RegisterRequest request = new RegisterRequest(name, email, password);
        apiService.register(request).enqueue(new Callback<>() {

            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse auth = response.body();

                    PreferenceManager prefs = PreferenceManager.getInstance(context);
                    prefs.setLoggedIn(true);
                    prefs.setUserID(String.valueOf(auth.getId()));
                    prefs.setUserName(auth.getName());
                    prefs.setUserEmail(auth.getEmail());
                    prefs.setToken(auth.getToken());
                    prefs.setRefreshToken(auth.getRefreshToken());

                    callback.onSuccess(auth.getId());
                } else {
                    callback.onError("Đăng ký thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void changePassword(String oldPassword, String newPassword, AuthCallback<Void> callback) {
        ChangePasswordRequest request = new ChangePasswordRequest(oldPassword, newPassword);
        apiService.changePassword(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Đổi mật khẩu thất bại: Kiểm tra lại mật khẩu cũ.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void sendPasswordResetEmail(String email, AuthCallback<String> callback) {
        // Vì đã gỡ Firebase, cần viết thêm 1 API bên .NET để xử lý gửi OTP/Link qua email.
        // Tạm thời báo lỗi để người dùng biết.
        callback.onError("Chức năng lấy lại mật khẩu đang được cập nhật!");
    }

    public void logout(AuthCallback<String> callback) {
        PreferenceManager prefs = PreferenceManager.getInstance(context);

        String refreshToken = prefs.getRefreshToken();
        if (refreshToken == null || refreshToken.isEmpty()) {
            prefs.clear();
            callback.onSuccess(null);
            return;
        }

        LogoutRequest request = new LogoutRequest(refreshToken);
        apiService.logout(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                prefs.clear();
                callback.onSuccess(null);
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                prefs.clear(); // Vẫn xóa local nếu rớt mạng
                callback.onSuccess(null);
            }
        });
    }
}