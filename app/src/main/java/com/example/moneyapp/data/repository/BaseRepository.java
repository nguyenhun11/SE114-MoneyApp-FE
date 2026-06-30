package com.example.moneyapp.data.repository;

import android.app.Application;
import android.content.Context;

import com.example.moneyapp.data.local.PreferenceManager;
import com.example.moneyapp.data.remote.api.ApiService;
import com.example.moneyapp.data.remote.api.RetrofitClient;

import org.json.JSONObject;

import retrofit2.Response;

public class BaseRepository {
    protected final ApiService apiService;
    protected final Context context;
    protected BaseRepository(Context context){
        apiService = RetrofitClient.getInstance(context).create(ApiService.class);
        this.context = context.getApplicationContext();
    }

    protected String parseError(Response<?> response, String defaultMessage) {
        try {
            if (response.errorBody() != null) {
                JSONObject jObjError = new JSONObject(response.errorBody().string());
                return jObjError.optString("message", defaultMessage);
            }
        } catch (Exception e) {
            return defaultMessage + ": " + response.code();
        }
        return defaultMessage;
    }
}
