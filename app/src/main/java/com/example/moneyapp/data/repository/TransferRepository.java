package com.example.moneyapp.data.repository;

import android.content.Context;
import androidx.annotation.NonNull;

import com.example.moneyapp.data.remote.request.TransferRequest;
import com.example.moneyapp.data.remote.response.TransferResponse;
import com.example.moneyapp.model.Transfer;
import com.example.moneyapp.utils.DateConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransferRepository extends BaseRepository {
    public interface TransferCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    public TransferRepository(Context context) {
        super(context);
    }

    private Transfer mapToTransfer(TransferResponse response) {
        return new Transfer(
                response.getId(),
                response.getSourceAccountId(),
                response.getSourceAccountName(),
                response.getSourceAccountIconId(),
                response.getSourceAccountColorId(),
                response.getDestinationAccountId(),
                response.getDestinationAccountName(),
                response.getDestinationAccountIconId(),
                response.getDestinationAccountColorId(),
                response.getSourceAmount(),
                response.getDestinationAmount(),
                response.getBaseAmount(),
                response.getSourceExchangeRate(),
                response.getDestinationExchangeRate(),
                DateConverter.convertStringToDate(response.getTransferDate()),
                response.getDescription(),
                DateConverter.convertStringToDate(response.getCreatedAt()),
                DateConverter.convertStringToDate(response.getLastUpdatedAt())
        );
    }

    public void getTransfers(Date startDate, Date endDate, String source, String destination, TransferCallback<List<Transfer>> callback) {
        String startStr = DateConverter.convertDateToString(startDate);
        String endStr = DateConverter.convertDateToString(endDate);

        apiService.getTransfers(startStr, endStr, source, destination).enqueue(new Callback<List<TransferResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<TransferResponse>> call, @NonNull Response<List<TransferResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Transfer> transfers = new ArrayList<>();
                    for (TransferResponse res : response.body()) {
                        transfers.add(mapToTransfer(res));
                    }
                    callback.onSuccess(transfers);
                } else {
                    callback.onError("Không tải được dữ liệu chuyển khoản: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<TransferResponse>> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void getTransferById(String id, TransferCallback<Transfer> callback) {
        apiService.getTransferById(id).enqueue(new Callback<TransferResponse>() {
            @Override
            public void onResponse(@NonNull Call<TransferResponse> call, @NonNull Response<TransferResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(mapToTransfer(response.body()));
                } else {
                    callback.onError("Không tìm thấy thông tin chuyển khoản: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<TransferResponse> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void createTransfer(TransferRequest request, TransferCallback<Transfer> callback) {
        apiService.createTransfer(request).enqueue(new Callback<TransferResponse>() {
            @Override
            public void onResponse(@NonNull Call<TransferResponse> call, @NonNull Response<TransferResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(mapToTransfer(response.body()));
                } else {
                    callback.onError("Chuyển khoản thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<TransferResponse> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void updateTransfer(String id, TransferRequest request, TransferCallback<Transfer> callback) {
        apiService.updateTransfer(id, request).enqueue(new Callback<TransferResponse>() {
            @Override
            public void onResponse(@NonNull Call<TransferResponse> call, @NonNull Response<TransferResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(mapToTransfer(response.body()));
                } else {
                    callback.onError("Cập nhật thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<TransferResponse> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void deleteTransfer(String id, TransferCallback<Void> callback) {
        apiService.deleteTransfer(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Xóa thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
}
