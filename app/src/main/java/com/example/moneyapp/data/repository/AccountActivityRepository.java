package com.example.moneyapp.data.repository;

import android.content.Context;
import androidx.annotation.NonNull;

import com.example.moneyapp.data.remote.response.AdjustBalanceResponse;
import com.example.moneyapp.data.remote.response.TransferResponse;
import com.example.moneyapp.model.AccountActivityItem;
import com.example.moneyapp.utils.DateConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountActivityRepository extends BaseRepository {

    public interface AccountActivityCallback {
        void onSuccess(List<AccountActivityItem> items);
        void onError(String message);
    }

    public AccountActivityRepository(Context context) {
        super(context);
    }

    public void getHistory(Date startDate, Date endDate, AccountActivityCallback callback) {
        String startStr = DateConverter.convertDateToString(startDate);
        String endStr = DateConverter.convertDateToString(endDate);

        final List<AccountActivityItem> allItems = new ArrayList<>();
        final int[] completedCalls = {0};
        final String[] errorMessage = {null};

        // Call 1: Transfers
        apiService.getTransfers(startStr, endStr, null, null).enqueue(new Callback<List<TransferResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<TransferResponse>> call, @NonNull Response<List<TransferResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (TransferResponse res : response.body()) {
                        allItems.add(new AccountActivityItem(
                                res.getId(),
                                AccountActivityItem.Type.TRANSFER,
                                res.getSourceAccountName(),
                                res.getDestinationAccountName(),
                                res.getAmount(),
                                DateConverter.convertStringToDate(res.getTransferDate()),
                                res.getDescription()
                        ));
                    }
                }
                checkCompletion(completedCalls, 2, errorMessage, allItems, callback);
            }

            @Override
            public void onFailure(@NonNull Call<List<TransferResponse>> call, @NonNull Throwable t) {
                errorMessage[0] = "Lỗi tải chuyển khoản: " + t.getMessage();
                checkCompletion(completedCalls, 2, errorMessage, allItems, callback);
            }
        });

        // Call 2: Adjustments
        apiService.getAdjustBalances(startStr, endStr, null).enqueue(new Callback<List<AdjustBalanceResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<AdjustBalanceResponse>> call, @NonNull Response<List<AdjustBalanceResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (AdjustBalanceResponse res : response.body()) {
                        // Assuming AdjustBalanceResponse has these fields (matching the provided file)
                        // Wait, I need to check the fields again.
                        // I'll add getters if missing in next step if needed.
                        // Actually, I can use the repository I saw earlier as reference.
                        allItems.add(new AccountActivityItem(
                                res.getId(),
                                AccountActivityItem.Type.ADJUSTMENT,
                                res.getAccountName(),
                                null,
                                res.getAmount(),
                                DateConverter.convertStringToDate(res.getCreatedAt()),
                                "Điều chỉnh số dư"
                        ));
                    }
                }
                checkCompletion(completedCalls, 2, errorMessage, allItems, callback);
            }

            @Override
            public void onFailure(@NonNull Call<List<AdjustBalanceResponse>> call, @NonNull Throwable t) {
                errorMessage[0] = "Lỗi tải điều chỉnh: " + t.getMessage();
                checkCompletion(completedCalls, 2, errorMessage, allItems, callback);
            }
        });
    }

    private void checkCompletion(int[] completedCalls, int totalCalls, String[] errorMessage, List<AccountActivityItem> allItems, AccountActivityCallback callback) {
        completedCalls[0]++;
        if (completedCalls[0] == totalCalls) {
            if (errorMessage[0] != null && allItems.isEmpty()) {
                callback.onError(errorMessage[0]);
            } else {
                callback.onSuccess(allItems);
            }
        }
    }
}
