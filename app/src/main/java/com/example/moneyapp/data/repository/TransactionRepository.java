package com.example.moneyapp.data.repository;

import android.content.Context;
import androidx.annotation.NonNull;

import com.example.moneyapp.data.remote.response.TransactionResponse;
import com.example.moneyapp.model.CategoryType;
import com.example.moneyapp.model.Transaction;
import com.example.moneyapp.utils.DateConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionRepository extends BaseRepository {

    public interface TransactionCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    public TransactionRepository(Context context) {
        super(context);
    }

    private Transaction mapToTransaction(TransactionResponse response) {
        CategoryType type = (response.getAmount() != null && response.getAmount() < 0)
                ? CategoryType.EXPENSE : CategoryType.INCOME;

        return new Transaction(
                response.getId(),
                response.getAccountId(),
                response.getAccountName(),
                response.getCategoryId(),
                response.getCategoryName(),
                type,
                response.getAmount(),
                DateConverter.convertStringToDate(response.getDate()),
                response.getNote(),
                response.getImageUrls()
        );
    }

    public void getFilteredTransactions(
            Date startDate, Date endDate, Integer categoryType,
            String accountId, String categoryId,
            TransactionCallback<List<Transaction>> callback) {

        String startStr = DateConverter.convertDateToString(startDate);
        String endStr = DateConverter.convertDateToString(endDate);

        apiService.getTransactions(startStr, endStr, categoryType, accountId, categoryId)
                .enqueue(new Callback<List<TransactionResponse>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<TransactionResponse>> call, @NonNull Response<List<TransactionResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Transaction> transactions = new ArrayList<>();
                            for (TransactionResponse res : response.body()) {
                                transactions.add(mapToTransaction(res));
                            }
                            callback.onSuccess(transactions);
                        } else {
                            callback.onError("Lỗi tải dữ liệu: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<TransactionResponse>> call, @NonNull Throwable t) {
                        callback.onError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    public void getTransactionById(String id, TransactionCallback<Transaction> callback) {
        apiService.getTransactionById(id).enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(@NonNull Call<TransactionResponse> call, @NonNull Response<TransactionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(mapToTransaction(response.body()));
                } else {
                    callback.onError("Không tìm thấy giao dịch");
                }
            }
            @Override
            public void onFailure(@NonNull Call<TransactionResponse> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối");
            }
        });
    }

    public void createTransaction(Transaction transaction, TransactionCallback<Transaction> callback) {
        callback.onError("Chức năng đang được cập nhật");
    }

    public void updateTransaction(Transaction transaction, TransactionCallback<Transaction> callback) {
        callback.onError("Chức năng đang được cập nhật");
    }

    public void deleteTransaction(String id, TransactionCallback<Void> callback) {
        apiService.deleteTransaction(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) callback.onSuccess(null);
                else callback.onError("Xóa thất bại");
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối");
            }
        });
    }
}
