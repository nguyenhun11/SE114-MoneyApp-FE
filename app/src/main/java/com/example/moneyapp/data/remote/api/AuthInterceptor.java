package com.example.moneyapp.data.remote.api;

import android.content.Context;
import androidx.annotation.NonNull;
import com.example.moneyapp.data.local.PreferenceManager;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String token = PreferenceManager.getInstance(context).getToken();

        // Bỏ qua nếu chưa có Token (Lúc mới cài app, Đăng nhập, Đăng ký)
        if (token == null || token.isEmpty()) {
            return chain.proceed(originalRequest);
        }

        // Đính Token vào Header
        Request newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();

        return chain.proceed(newRequest);
    }
}