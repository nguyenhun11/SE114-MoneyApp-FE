package com.example.moneyapp.data.repository;

import android.content.Context;
import androidx.annotation.NonNull;

import com.example.moneyapp.data.remote.request.CheckInRequest;
import com.example.moneyapp.data.remote.request.UserProfileRequest;
import com.example.moneyapp.data.remote.response.CheckInResponse;
import com.example.moneyapp.data.remote.response.UserProfileResponse;
import com.example.moneyapp.data.local.PreferenceManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository extends BaseRepository {
    
    public UserRepository(Context context){
        super(context);
    }
    
    public interface UserCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    public void getUserProfile(UserCallback<UserProfileResponse> callback) {
        apiService.getUserProfile(null).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserProfileResponse> call, @NonNull Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfileResponse fullUser = response.body();

                    callback.onSuccess(fullUser);
                } else {
                    callback.onError("Không tải được hồ sơ: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserProfileResponse> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void updateUserProfile(UserProfileRequest request, UserCallback<Void> callback) {
        apiService.updateUserProfile(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Không thể cập nhật thông tin: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void deleteUser(String mode, UserCallback<Void> callback) {
        apiService.deleteUser(mode).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    PreferenceManager.getInstance(context).clear();
                    callback.onSuccess(null);
                } else {
                    callback.onError("Không thể xóa người dùng: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void checkIn(CheckInRequest request, UserCallback<CheckInResponse> callback) {
        apiService.checkIn(request).enqueue(new Callback<CheckInResponse>() {
            @Override
            public void onResponse(@NonNull Call<CheckInResponse> call, @NonNull Response<CheckInResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Điểm danh thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<CheckInResponse> call, @NonNull Throwable t) {
                callback.onError("Lỗi mạng: " + t.getMessage());
            }
        });
    }

    public void restoreStreak(CheckInRequest request, UserCallback<CheckInResponse> callback) {
        apiService.restoreStreak(request).enqueue(new Callback<CheckInResponse>() {
            @Override
            public void onResponse(@NonNull Call<CheckInResponse> call, @NonNull Response<CheckInResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Khôi phục thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<CheckInResponse> call, @NonNull Throwable t) {
                callback.onError("Lỗi mạng: " + t.getMessage());
            }
        });
    }
}
