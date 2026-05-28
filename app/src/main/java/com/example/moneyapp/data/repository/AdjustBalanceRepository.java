package com.example.moneyapp.data.repository;

import android.content.Context;
import androidx.annotation.NonNull;

import com.example.moneyapp.data.remote.request.TransactionRequest;
import com.example.moneyapp.data.remote.response.AdjustBalanceResponse;
import com.example.moneyapp.utils.DateConverter;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdjustBalanceRepository extends BaseRepository {

    public interface AdjustBalanceCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    public AdjustBalanceRepository(Context context) {
        super(context);
    }

    public void getAdjustBalances(Date startDate, Date endDate, String accountId, AdjustBalanceCallback<List<AdjustBalanceResponse>> callback) {
        String startStr = DateConverter.convertDateToString(startDate);
        String endStr = DateConverter.convertDateToString(endDate);

        apiService.getAdjustBalances(startStr, endStr, accountId).enqueue(new Callback<List<AdjustBalanceResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<AdjustBalanceResponse>> call, @NonNull Response<List<AdjustBalanceResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không tải được lịch sử điều chỉnh: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<AdjustBalanceResponse>> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void adjustBalance(String accountId, double newBalance, String note, AdjustBalanceCallback<Void> callback) {
        TransactionRequest request = new TransactionRequest();
        request.setAccountId(accountId);
        request.setAmount(newBalance);
        request.setNote(note);
        request.setDate(DateConverter.convertDateToString(new Date()));

        apiService.adjustBalance(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Điều chỉnh số dư thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
}
