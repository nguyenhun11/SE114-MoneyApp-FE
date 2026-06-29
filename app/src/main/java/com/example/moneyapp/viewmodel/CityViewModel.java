package com.example.moneyapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moneyapp.data.remote.request.BuildRequest;
import com.example.moneyapp.data.remote.response.CityResponse;
import com.example.moneyapp.data.remote.response.RankItemDto;
import com.example.moneyapp.data.repository.CityRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CityViewModel extends AndroidViewModel {
    private final CityRepository repository;
    private final MutableLiveData<CityResponse> cityData = new MutableLiveData<>();
    private final MutableLiveData<List<RankItemDto>> rankingData = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public CityViewModel(@NonNull Application application) {
        super(application);
        this.repository = new CityRepository(application);
    }

    public LiveData<CityResponse> getCityData() { return cityData; }
    public LiveData<List<RankItemDto>> getRankingData() { return rankingData; }
    public LiveData<String> getError() { return error; }

    public void fetchCityData() {
        repository.getCity().enqueue(new Callback<CityResponse>() {
            @Override
            public void onResponse(Call<CityResponse> call, Response<CityResponse> response) {
                if (response.isSuccessful()) {
                    cityData.setValue(response.body());
                } else {
                    error.setValue("Failed to fetch city data");
                }
            }

            @Override
            public void onFailure(Call<CityResponse> call, Throwable t) {
                error.setValue(t.getMessage());
            }
        });
    }

    public void fetchRankingData(int type) {
        repository.getRanking(type).enqueue(new Callback<List<RankItemDto>>() {
            @Override
            public void onResponse(Call<List<RankItemDto>> call, Response<List<RankItemDto>> response) {
                if (response.isSuccessful()) {
                    rankingData.setValue(response.body());
                } else {
                    error.setValue("Không thể tải bảng xếp hạng");
                }
            }

            @Override
            public void onFailure(Call<List<RankItemDto>> call, Throwable t) {
                error.setValue(t.getMessage());
            }
        });
    }

    public void buildStructure(BuildRequest request) {
        repository.build(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    fetchCityData();
                } else {
                    try {
                        String errorMsg = "Lỗi xây dựng: " + response.code();
                        if (response.errorBody() != null) {
                            errorMsg += " - " + response.errorBody().string();
                        }
                        error.setValue(errorMsg);
                    } catch (Exception e) {
                        error.setValue("Lỗi hệ thống khi xây dựng");
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                error.setValue(t.getMessage());
            }
        });
    }

    public void upgradeStructure(int buildingId) {
        repository.upgrade(buildingId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    fetchCityData();
                } else {
                    error.setValue("Không đủ điểm Prosperity hoặc đã đạt cấp tối đa");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                error.setValue(t.getMessage());
            }
        });
    }
}
