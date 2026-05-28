package com.example.moneyapp.data.repository;

import android.content.Context;
import androidx.annotation.NonNull;

import com.example.moneyapp.data.remote.request.TransactionRequest;
import com.example.moneyapp.data.remote.response.TransactionResponse;
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
        // Lưu ý: Response trả về accountId, categoryId.
        // Tạm thời map vào các field String account, category của model Transaction.
        Transaction transaction = new Transaction();
        transaction.setTransactionId(response.getId());
        transaction.setAccountId(response.getAccountId());
        transaction.setCategoryId(response.getCategoryId());
        transaction.setAmount(response.getAmount());
        transaction.setDate(DateConverter.convertStringToDate(response.getDate()));
        transaction.setDescription(response.getNote());
        transaction.setImageUrls(response.getImageUrls());
        // Type có thể cần được xác định từ Category hoặc API cần trả thêm type.
        // Tạm thời để mặc định hoặc xử lý ở tầng ViewModel nếu cần.
        return transaction;
    }

    public void getFilteredTransactions(
            Date startDate,
            Date endDate,
            Integer categoryType,
            String accountId,
            String categoryId,
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
                            callback.onError("Không tải được giao dịch: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<TransactionResponse>> call, @NonNull Throwable throwable) {
                        callback.onError("Lỗi kết nối: " + throwable.getMessage());
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
                    callback.onError("Không tìm thấy giao dịch: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<TransactionResponse> call, @NonNull Throwable throwable) {
                callback.onError("Lỗi kết nối: " + throwable.getMessage());
            }
        });
    }

    public void createTransaction(Transaction transaction, TransactionCallback<Transaction> callback) {
        TransactionRequest request = new TransactionRequest(
                transaction.getAccountId(),
                transaction.getCategoryId(),
                transaction.getAmount(),
                DateConverter.convertDateToString(transaction.getDate()),
                transaction.getDescription(),
                transaction.getImageUrls()
        );

        apiService.createTransaction(request).enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(@NonNull Call<TransactionResponse> call, @NonNull Response<TransactionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(mapToTransaction(response.body()));
                } else {
                    callback.onError("Tạo giao dịch thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<TransactionResponse> call, @NonNull Throwable throwable) {
                callback.onError("Lỗi kết nối: " + throwable.getMessage());
            }
        });
    }

    public void updateTransaction(Transaction transaction, TransactionCallback<Transaction> callback) {
        TransactionRequest request = new TransactionRequest(
                transaction.getAccountId(),
                transaction.getCategoryId(),
                transaction.getAmount(),
                DateConverter.convertDateToString(transaction.getDate()),
                transaction.getDescription(),
                transaction.getImageUrls()
        );

        apiService.updateTransaction(transaction.getTransactionId(), request).enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(@NonNull Call<TransactionResponse> call, @NonNull Response<TransactionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(mapToTransaction(response.body()));
                } else {
                    callback.onError("Cập nhật thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<TransactionResponse> call, @NonNull Throwable throwable) {
                callback.onError("Lỗi kết nối: " + throwable.getMessage());
            }
        });
    }

    public void deleteTransaction(String id, TransactionCallback<Void> callback) {
        apiService.deleteTransaction(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Xóa thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable throwable) {
                callback.onError("Lỗi kết nối: " + throwable.getMessage());
            }
        });
    }
}
