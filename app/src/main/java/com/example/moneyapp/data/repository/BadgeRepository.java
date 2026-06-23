package com.example.moneyapp.data.repository;

import android.content.Context;

import com.example.moneyapp.data.remote.response.BadgeResponse;

import java.util.List;

import retrofit2.Call;

public class BadgeRepository extends BaseRepository {
    public BadgeRepository(Context context) {
        super(context);
    }

    public Call<List<BadgeResponse>> getUserBadges() {
        return apiService.getUserBadges();
    }
}
