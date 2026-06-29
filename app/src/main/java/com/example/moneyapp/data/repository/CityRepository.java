package com.example.moneyapp.data.repository;

import android.content.Context;

import com.example.moneyapp.data.remote.request.BuildRequest;
import com.example.moneyapp.data.remote.response.CityResponse;
import com.example.moneyapp.data.remote.response.RankItemDto;

import java.util.List;

import retrofit2.Call;

public class CityRepository extends BaseRepository {
    public CityRepository(Context context) {
        super(context);
    }

    public Call<CityResponse> getCity() {
        return apiService.getCity();
    }

    public Call<Void> build(BuildRequest request) {
        return apiService.build(request);
    }

    public Call<Void> upgrade(int buildingId) {
        return apiService.upgradeBuilding(buildingId);
    }

    public Call<List<RankItemDto>> getRanking(int type) {
        return apiService.getCityRanking(type);
    }
}
