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
    public synchronized Request authenticate(@Nullable Route route, @NonNull Response response) throws IOException {
        PreferenceManager prefs = PreferenceManager.getInstance(context);
        if (response.request().url().encodedPath().contains("refresh-token")) {
            prefs.clear();
            return null;
        }

        String currentTokenInPrefs = prefs.getToken();
        String failedToken = response.request().header("Authorization");

        if (failedToken != null && currentTokenInPrefs != null && !failedToken.contains(currentTokenInPrefs)) {
            return response.request().newBuilder()
                    .header("Authorization", "Bearer " + currentTokenInPrefs)
                    .build();
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService authApi = retrofit.create(ApiService.class);

        var refreshToken = prefs.getRefreshToken();
        if (refreshToken == null || refreshToken.isEmpty()) {
            prefs.clear();
            return null;
        }

        Call<AuthResponse> call = authApi.refreshToken(new RefreshTokenRequest(refreshToken));
        retrofit2.Response<AuthResponse> refreshResponse = call.execute();

        if (refreshResponse.isSuccessful() && refreshResponse.body() != null) {
            AuthResponse newTokens = refreshResponse.body();
            prefs.setToken(newTokens.getToken());
            prefs.setRefreshToken(newTokens.getRefreshToken());

            return response.request().newBuilder()
                    .header("Authorization", "Bearer " + newTokens.getToken())
                    .build();
        } else {
            prefs.clear();
            return null;
        }
    }
}