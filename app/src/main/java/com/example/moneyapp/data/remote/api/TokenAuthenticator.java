package com.example.moneyapp.data.remote.api;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.moneyapp.data.local.PreferenceManager;
import com.example.moneyapp.data.remote.request.RefreshTokenRequest;
import com.example.moneyapp.data.remote.response.AuthResponse;

import java.io.IOException;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TokenAuthenticator implements Authenticator {

    private final Context context;

    public TokenAuthenticator(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public Request authenticate(@Nullable Route route, @NonNull Response response) throws IOException {
        PreferenceManager prefs = PreferenceManager.getInstance(context);

        // 1. Tránh lặp vô hạn: Nếu chính API refresh-token bị lỗi 401, tức là hết cứu -> Đăng xuất
        if (response.request().url().encodedPath().contains("refresh-token")) {
            prefs.clear();
            return null;
        }

        // 2. Tạo một Retrofit "Trắng" (Không dính Interceptor) để gọi API làm mới Token
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService authApi = retrofit.create(ApiService.class);

        // 3. Gọi API xin Token mới (Gọi đồng bộ bằng .execute() thay vì .enqueue())
        var refreshToken = prefs.getRefreshToken();
        if (refreshToken == null || refreshToken.isEmpty()) {
            prefs.clear();
            return null;
        }
        Call<AuthResponse> call = authApi.refreshToken(new RefreshTokenRequest(refreshToken));
        retrofit2.Response<AuthResponse> refreshResponse = call.execute();

        if (refreshResponse.isSuccessful() && refreshResponse.body() != null) {
            // 4. Lấy được Token mới -> Cất vào két sắt
            AuthResponse newTokens = refreshResponse.body();
            prefs.setToken(newTokens.getToken());
            prefs.setRefreshToken(newTokens.getRefreshToken());

            // 5. Giải cứu API bị lỗi ban đầu bằng cách đính Token mới vào và cho chạy tiếp
            return response.request().newBuilder()
                    .header("Authorization", "Bearer " + newTokens.getToken())
                    .build();
        } else {
            // 6. Refresh Token cũng đã hết hạn hoặc server từ chối -> Đăng xuất
            prefs.clear();
            return null;
        }
    }
}