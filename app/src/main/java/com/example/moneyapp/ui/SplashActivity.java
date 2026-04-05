package com.example.moneyapp.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.moneyapp.R;
import com.example.moneyapp.ui.auth.AuthActivity;
import com.example.moneyapp.utils.PreferenceManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Force light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Đọc trạng thái đăng nhập từ SharedPreferences
        boolean isLoggedIn = PreferenceManager.getInstance(this).isLoggedIn();

        // Click nút "Tiếp tục"
        findViewById(R.id.btn_continue).setOnClickListener(v -> {
            Intent intent;
            if (isLoggedIn) {
                // Nếu đã đăng nhập, chuyển vào MainActivity
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                // Nếu chưa đăng nhập, chuyển vào AuthActivity (Login/Register)
                intent = new Intent(SplashActivity.this, AuthActivity.class);
            }
            startActivity(intent);
            finish();
        });
    }
}
