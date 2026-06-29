package com.example.moneyapp.data.repository;

import android.content.Context;
import com.example.moneyapp.data.remote.response.DashboardOverviewResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardRepository extends BaseRepository {
    public DashboardRepository(Context context) {
        super(context);
    }

    public interface DashboardCallback {
        void onSuccess(DashboardOverviewResponse result);
        void onError(String message);
    }

    public void getDashboardOverview(DashboardCallback callback) {
        apiService.getDashboardOverview().enqueue(new Callback<DashboardOverviewResponse>() {
            @Override
            public void onResponse(Call<DashboardOverviewResponse> call, Response<DashboardOverviewResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi tải dashboard: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<DashboardOverviewResponse> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
}
