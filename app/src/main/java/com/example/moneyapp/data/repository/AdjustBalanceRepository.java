package com.example.moneyapp.data.repository;

import android.content.Context;
import androidx.annotation.NonNull;

import com.example.moneyapp.data.remote.request.AdjustBalanceRequest;
import com.example.moneyapp.data.remote.response.AdjustBalanceResponse;
import com.example.moneyapp.model.AdjustBalance;
import com.example.moneyapp.utils.DateConverter;

import java.util.ArrayList;
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

    // Hàm chuyển đổi dữ liệu từ API (Response) sang Model nội bộ của App
    private AdjustBalance mapToAdjustBalance(AdjustBalanceResponse response) {
        return new AdjustBalance(
                response.getId(),
                response.getAccountId(),
                response.getAccountName(),
                response.getAmount(),
                DateConverter.convertStringToDate(response.getCreatedAt())
        );
    }

    // ĐÃ SỬA: Callback trả về List<AdjustBalance> thay vì List<AdjustBalanceResponse>
    public void getAdjustBalances(Date startDate, Date endDate, String accountId, AdjustBalanceCallback<List<AdjustBalance>> callback) {
        String startStr = DateConverter.convertDateToString(startDate);
        String endStr = DateConverter.convertDateToString(endDate);

        apiService.getAdjustBalances(startStr, endStr, accountId).enqueue(new Callback<List<AdjustBalanceResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<AdjustBalanceResponse>> call, @NonNull Response<List<AdjustBalanceResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    // Xử lý Map dữ liệu trước khi trả về
                    List<AdjustBalance> adjustBalances = new ArrayList<>();
                    for (AdjustBalanceResponse res : response.body()) {
                        adjustBalances.add(mapToAdjustBalance(res));
                    }
                    callback.onSuccess(adjustBalances);

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

    public void adjustBalance(String accountId, double newBalance, AdjustBalanceCallback<Void> callback) {
        AdjustBalanceRequest request = new AdjustBalanceRequest(accountId, newBalance);

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