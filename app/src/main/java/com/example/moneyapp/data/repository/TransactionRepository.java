package com.example.moneyapp.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.moneyapp.data.remote.request.TransactionRequest;
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
        CategoryType type = null; // Giá trị mặc định an toàn

        if (response.getType() != null) {
            if (response.getType() == 0) {
                type = CategoryType.EXPENSE; // Chi tiêu
            } else if (response.getType() == 1) {
                type = CategoryType.INCOME;  // Thu nhập
            }
        }

        return new Transaction(
                response.getId(),
                response.getAccountId(),
                response.getAccountName(),
                response.getCategoryId(),
                response.getCategoryName(),
                type, // Truyền type chuẩn xác vào đây
                response.getOriginalAmount(),
                response.getCurrencyCode(),
                response.getAccountAmount(),
                response.getBaseAmount(),
                response.getExchangeRate(),
                DateConverter.convertStringToDate(response.getDate()),
                response.getNote(),
                response.getCategoryColorId(),
                response.getCategoryIconId(),
                response.getAccountColorId(),
                response.getAccountIconId(),
                response.getImageUrls(),
                DateConverter.convertStringToDate(response.getCreatedAt())
        );
    }

    private TransactionRequest mapToRequest(Transaction transaction) {
        // Chuyển kiểu Date sang String (Sử dụng DateConverter bạn đã có sẵn)
        String dateStr = DateConverter.convertDateToString(transaction.getDate());

        return new TransactionRequest(
                transaction.getAccountId(),
                transaction.getCategoryId(),
                transaction.getOriginalAmount(),
                transaction.getCurrencyCode(),
                dateStr,
                transaction.getNote(),
                transaction.getImageUrls()
        );
    }

    public void getFilteredTransactions(Date startDate, Date endDate, CategoryType categoryType, String accountId, String categoryId, TransactionCallback<List<Transaction>> callback) {

        String startStr = DateConverter.convertDateToString(startDate);
        String endStr = DateConverter.convertDateToString(endDate);

        Integer typeValue = (categoryType != null) ? categoryType.getValue() : null;
        apiService.getTransactions(startStr, endStr, typeValue, accountId, categoryId).enqueue(new Callback<List<TransactionResponse>>() {
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
        // 1. Map Transaction model thành TransactionRequest để gửi lên API
        TransactionRequest request = mapToRequest(transaction);

        // 2. Gọi API POST
        apiService.createTransaction(request).enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(@NonNull Call<TransactionResponse> call, @NonNull Response<TransactionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // API trả về Response -> Map ngược lại thành Transaction Model và báo UI
                    callback.onSuccess(mapToTransaction(response.body()));
                } else {
                    callback.onError("Thêm giao dịch thất bại. Mã lỗi: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<TransactionResponse> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối khi thêm giao dịch: " + t.getMessage());
            }
        });
    }

    public void updateTransaction(Transaction transaction, TransactionCallback<Transaction> callback) {
        // 1. Map Transaction model thành TransactionRequest để gửi lên API
        TransactionRequest request = mapToRequest(transaction);

        // 2. Gọi API PUT
        apiService.updateTransaction(transaction.getTransactionId(), request).enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(@NonNull Call<TransactionResponse> call, @NonNull Response<TransactionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // API trả về Response -> Map ngược lại thành Transaction Model và báo UI
                    callback.onSuccess(mapToTransaction(response.body()));
                } else {
                    callback.onError("Cập nhật thất bại. Mã lỗi: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<TransactionResponse> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối khi cập nhật: " + t.getMessage());
            }
        });
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
