package com.example.moneyapp.data.repository;

import android.app.Application;
import android.content.Context;
import androidx.annotation.NonNull;
import com.example.moneyapp.data.local.PreferenceManager;
import com.example.moneyapp.data.remote.api.ApiService;
import com.example.moneyapp.data.remote.api.RetrofitClient;
import com.example.moneyapp.data.remote.request.ChangePasswordRequest;
import com.example.moneyapp.data.remote.request.ForgotPasswordRequest;
import com.example.moneyapp.data.remote.request.GoogleLoginRequest;
import com.example.moneyapp.data.remote.request.LoginRequest;
import com.example.moneyapp.data.remote.request.LogoutRequest;
import com.example.moneyapp.data.remote.request.RegisterRequest;
import com.example.moneyapp.data.remote.request.ResetPasswordRequest;
import com.example.moneyapp.data.remote.response.AuthResponse;

import org.json.JSONObject;

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
                    prefs.setUserEmail(auth.getEmail());
                    prefs.setToken(auth.getToken());
                    prefs.setRefreshToken(auth.getRefreshToken());

                    callback.onSuccess(auth.getId());
                } else {
                    String errorMessage = "Đăng nhập thất bại";
                    try {
                        if (response.errorBody() != null) {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            errorMessage = jObjError.getString("message");
                        }
                    } catch (Exception e) {
                        errorMessage += ": " + response.code();
                    }
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void loginByGoogle(String idToken, AuthCallback<Integer> callback) {
        GoogleLoginRequest request = new GoogleLoginRequest(idToken);
        apiService.googleLogin(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse auth = response.body();

                    // Lưu thông tin đăng nhập vào SharedPreferences
                    PreferenceManager prefs = PreferenceManager.getInstance(context);
                    prefs.setLoggedIn(true);
                    prefs.setUserID(String.valueOf(auth.getId()));
                    prefs.setUserEmail(auth.getEmail());
                    prefs.setToken(auth.getToken());
                    prefs.setRefreshToken(auth.getRefreshToken());

                    callback.onSuccess(auth.getId());
                } else {
                    callback.onError("Đăng nhập Google thất bại: " + response.code());
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

                    // ⚠️ CHÚ Ý: Không lưu trạng thái đăng nhập ở đây vì API Register hiện tại
                    // không trả về Token. Người dùng cần quay lại Login để đăng nhập chính thức.

                    callback.onSuccess(auth.getId());
                } else {
                    String errorMessage = "Đăng ký thất bại";
                    try {
                        if (response.errorBody() != null) {
                            String errorStr = response.errorBody().string();
                            JSONObject jObjError = new JSONObject(errorStr);
                            if (jObjError.has("message")) {
                                errorMessage = jObjError.getString("message");
                            } else if (jObjError.has("Message")) {
                                errorMessage = jObjError.getString("Message");
                            }
                        }
                    } catch (Exception e) {
                        errorMessage += ": " + response.code();
                    }
                    callback.onError(errorMessage);
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
                    String errorMessage = "Đổi mật khẩu thất bại";
                    try {
                        if (response.errorBody() != null) {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            errorMessage = jObjError.optString("message", errorMessage);
                        }
                    } catch (Exception e) {
                        errorMessage += ": " + response.code();
                    }
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void sendPasswordResetEmail(String email, AuthCallback<Void> callback) {
        ForgotPasswordRequest request = new ForgotPasswordRequest(email);
        apiService.forgotPassword(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    String errorMessage = "Yêu cầu thất bại";
                    try {
                        if (response.errorBody() != null) {
                            String errorStr = response.errorBody().string();
                            JSONObject jObjError = new JSONObject(errorStr);
                            
                            // Nếu Backend trả về một flag hoặc message đặc thù cho tài khoản Google
                            if (errorStr.contains("Google") || errorStr.contains("external")) {
                                errorMessage = "Tài khoản này được quản lý bởi Google. Vui lòng sử dụng Đăng nhập bằng Google.";
                            } else {
                                errorMessage = jObjError.optString("message", "Yêu cầu thất bại");
                            }
                        }
                    } catch (Exception e) {
                        errorMessage += ": " + response.code();
                    }
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void resetPassword(String email, String token, String newPassword, AuthCallback<Void> callback) {
        ResetPasswordRequest request = new ResetPasswordRequest(email, token, newPassword);
        apiService.resetPassword(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    String errorMessage = "Đặt lại mật khẩu thất bại";
                    try {
                        if (response.errorBody() != null) {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            errorMessage = jObjError.getString("message");
                        }
                    } catch (Exception e) {
                        errorMessage += ": " + response.code();
                    }
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
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