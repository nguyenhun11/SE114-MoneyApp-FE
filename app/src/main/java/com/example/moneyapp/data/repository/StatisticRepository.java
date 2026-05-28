package com.example.moneyapp.data.repository;

import android.content.Context;
import androidx.annotation.NonNull;

import com.example.moneyapp.data.remote.response.CashFlowBarDto;
import com.example.moneyapp.data.remote.response.CategoryPieChartDto;
import com.example.moneyapp.data.remote.response.StackedBarChartDto;
import com.example.moneyapp.utils.DateConverter;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticRepository extends BaseRepository {

    public interface StatisticCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    public StatisticRepository(Context context) {
        super(context);
    }

    private int getTimeZoneOffset() {
        return TimeZone.getDefault().getRawOffset() / (60 * 60 * 1000);
    }

    public void getExpensePieChart(Date startDate, Date endDate, StatisticCallback<List<CategoryPieChartDto>> callback) {
        String startStr = DateConverter.convertDateToString(startDate);
        String endStr = DateConverter.convertDateToString(endDate);

        apiService.getExpensePieChart(startStr, endStr, getTimeZoneOffset()).enqueue(new Callback<List<CategoryPieChartDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<CategoryPieChartDto>> call, @NonNull Response<List<CategoryPieChartDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không tải được biểu đồ tròn chi: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CategoryPieChartDto>> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void getIncomePieChart(Date startDate, Date endDate, StatisticCallback<List<CategoryPieChartDto>> callback) {
        String startStr = DateConverter.convertDateToString(startDate);
        String endStr = DateConverter.convertDateToString(endDate);

        apiService.getIncomePieChart(startStr, endStr, getTimeZoneOffset()).enqueue(new Callback<List<CategoryPieChartDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<CategoryPieChartDto>> call, @NonNull Response<List<CategoryPieChartDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không tải được biểu đồ tròn thu: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CategoryPieChartDto>> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void getExpenseStackedBarChart(Date startDate, Date endDate, int groupBy, StatisticCallback<List<StackedBarChartDto>> callback) {
        String startStr = DateConverter.convertDateToString(startDate);
        String endStr = DateConverter.convertDateToString(endDate);

        apiService.getExpenseStackedBarChart(startStr, endStr, groupBy, getTimeZoneOffset()).enqueue(new Callback<List<StackedBarChartDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<StackedBarChartDto>> call, @NonNull Response<List<StackedBarChartDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không tải được biểu đồ cột chi: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<StackedBarChartDto>> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void getCashFlowBarChart(Date startDate, Date endDate, int groupBy, StatisticCallback<List<CashFlowBarDto>> callback) {
        String startStr = DateConverter.convertDateToString(startDate);
        String endStr = DateConverter.convertDateToString(endDate);

        apiService.getCashFlowBarChart(startStr, endStr, groupBy, getTimeZoneOffset()).enqueue(new Callback<List<CashFlowBarDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<CashFlowBarDto>> call, @NonNull Response<List<CashFlowBarDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không tải được biểu đồ dòng tiền: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CashFlowBarDto>> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
}
