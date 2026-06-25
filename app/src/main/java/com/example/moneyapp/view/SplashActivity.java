package com.example.moneyapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.moneyapp.R;
import com.example.moneyapp.data.remote.api.ApiService;
import com.example.moneyapp.data.remote.api.RetrofitClient;
import com.example.moneyapp.data.remote.response.ExchangeRateResponse;
import com.example.moneyapp.utils.CurrencyFormatter;
import com.example.moneyapp.view.auth.AuthActivity;
import com.example.moneyapp.data.local.PreferenceManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        syncExchangeRates();

        boolean isLoggedIn = PreferenceManager.getInstance(this).isLoggedIn();

        findViewById(R.id.btn_continue).setOnClickListener(v -> {
            Intent intent;
            if (isLoggedIn) {
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, AuthActivity.class);
            }
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        });
    }

    private void syncExchangeRates() {
        ApiService apiService = RetrofitClient.getInstance(this).create(ApiService.class);
        apiService.getLatestExchangeRates().enqueue(new Callback<ExchangeRateResponse>() {
            @Override
            public void onResponse(Call<ExchangeRateResponse> call, Response<ExchangeRateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ExchangeRateResponse data = response.body();

                    CurrencyFormatter.setData(data.getCurrencies(), data.getRates());

                    Log.d("CurrencySync", "Đồng bộ tỷ giá thành công! Đã nạp "
                            + data.getCurrencies().size() + " đồng tiền.");
                } else {
                    Log.e("CurrencySync", "Backend trả về lỗi hoặc thiếu dữ liệu.");
                    loadLocalFallbackRates();
                }
            }

            @Override
            public void onFailure(Call<ExchangeRateResponse> call, Throwable t) {
                Log.e("CurrencySync", "Lỗi kết nối mạng khi tải tỷ giá: " + t.getMessage());
                loadLocalFallbackRates();
            }
        });
    }

    private void loadLocalFallbackRates() {
        List<String> defaultCurrencies = new java.util.ArrayList<>();
        defaultCurrencies.add("VND");
        defaultCurrencies.add("USD");
        defaultCurrencies.add("EUR");
        defaultCurrencies.add("JPY");

        java.util.Map<String, Double> defaultRates = new java.util.HashMap<>();
        defaultRates.put("VND", 1.0);
        defaultRates.put("USD", 0.000038);
        defaultRates.put("EUR", 0.000035);
        defaultRates.put("JPY", 0.0061);

        CurrencyFormatter.setData(defaultCurrencies, defaultRates);
    }
}
