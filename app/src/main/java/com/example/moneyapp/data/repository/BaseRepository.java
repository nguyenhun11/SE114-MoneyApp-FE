package com.example.moneyapp.data.repository;

import android.app.Application;
import android.content.Context;

import com.example.moneyapp.data.local.PreferenceManager;
import com.example.moneyapp.data.remote.api.ApiService;
import com.example.moneyapp.data.remote.api.RetrofitClient;

public class BaseRepository {
    protected final ApiService apiService;
    protected final Context context;
    protected BaseRepository(Context context){
        apiService = RetrofitClient.getInstance(context).create(ApiService.class);
        this.context = context.getApplicationContext();
    }
}
