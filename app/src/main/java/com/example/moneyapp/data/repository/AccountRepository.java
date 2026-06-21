package com.example.moneyapp.data.repository;

import android.content.Context;
import androidx.annotation.NonNull;

import com.example.moneyapp.data.local.PreferenceManager;
import com.example.moneyapp.data.remote.request.AccountRequest;
import com.example.moneyapp.data.remote.request.ReorderAccountRequest;
import com.example.moneyapp.data.remote.response.AccountResponse;
import com.example.moneyapp.model.Account;
import com.example.moneyapp.utils.DateConverter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountRepository extends BaseRepository {
    private final String currentUserId;

    public interface AccountCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    public AccountRepository(Context context) {
        super(context);
        currentUserId = PreferenceManager.getInstance(context).getUserID();
    }

    private Account mapToAccount(AccountResponse response) {
        return new Account(
                response.getId(),
                response.getAccountName(),
                response.getBalance(),
                response.getCurrencyCode(),
                response.getColorId(),
                response.getIconId(),
                response.getDescription(),
                response.isIncludeInTotalBalance(),
                response.getSortingOrder(),
                DateConverter.convertStringToDate(response.getCreatedAt()),
                DateConverter.convertStringToDate(response.getLastUpdatedAt())
        );
    }

    public void getAllAccounts(AccountCallback<List<Account>> callback) {
        apiService.getAllAccounts().enqueue(new Callback<List<AccountResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<AccountResponse>> call, @NonNull Response<List<AccountResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Account> accounts = new ArrayList<>();
                    for (AccountResponse accountResponse : response.body()) {
                        accounts.add(mapToAccount(accountResponse));
                    }
                    callback.onSuccess(accounts);
                } else {
                    callback.onError("Không tải được danh sách ví: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<AccountResponse>> call, @NonNull Throwable throwable) {
                if (!call.isCanceled()) {
                    callback.onError("Lỗi kết nối mạng: " + throwable.getMessage());
                }
            }
        });
    }

    public void getTotalBalance(AccountCallback<Double> callback) {
        apiService.getTotalBalance().enqueue(new Callback<Double>() {
            @Override
            public void onResponse(@NonNull Call<Double> call, @NonNull Response<Double> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không tải được tổng số dư: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Double> call, @NonNull Throwable throwable) {
                if (!call.isCanceled()) {
                    callback.onError("Lỗi kết nối mạng: " + throwable.getMessage());
                }
            }
        });
    }

    public void getAccounts(String accountId, AccountCallback<Account> callback) {
        apiService.getAccountById(accountId).enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(@NonNull Call<AccountResponse> call, @NonNull Response<AccountResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Nhận 1 cục thì map 1 cục rồi trả ra luôn
                    Account account = mapToAccount(response.body());
                    callback.onSuccess(account);
                } else {
                    callback.onError("Không tìm thấy ví: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccountResponse> call, @NonNull Throwable throwable) {
                if (!call.isCanceled()) {
                    callback.onError("Lỗi kết nối mạng: " + throwable.getMessage());
                }
            }
        });
    }

    public void insertAccount(Account account, AccountCallback<String> callback) {
        if (account == null) {
            callback.onError("Dữ liệu ví bị trống");
            return;
        }

        AccountRequest request = new AccountRequest(
                account.getAccountName(),
                account.getBalance(),
                account.getCurrencyCode(),
                account.getColor(),
                account.getIcon(),
                account.getDescription(),
                account.isIncludeInTotal()
        );

        apiService.createAccount(request).enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(@NonNull Call<AccountResponse> call, @NonNull Response<AccountResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String newAccountId = response.body().getId();
                    callback.onSuccess(newAccountId);
                } else {
                    callback.onError("Tạo ví thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccountResponse> call, @NonNull Throwable throwable) {
                if (!call.isCanceled()) {
                    callback.onError("Lỗi kết nối mạng: " + throwable.getMessage());
                }
            }
        });
    }

    public void updateAccount(Account account, AccountCallback<Void> callback) {
        if (account == null || account.getAccountId() == null || account.getAccountId().isEmpty()) {
            callback.onError("Dữ liệu ví bị trống hoặc mất ID");
            return;
        }

        AccountRequest request = new AccountRequest(
                account.getAccountName(),
                account.getBalance(),
                account.getCurrencyCode(),
                account.getColor(),
                account.getIcon(),
                account.getDescription(),
                account.isIncludeInTotal()
        );

        apiService.updateAccount(account.getAccountId(), request).enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(@NonNull Call<AccountResponse> call, @NonNull Response<AccountResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Cập nhật ví thất bại: Lỗi " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccountResponse> call, @NonNull Throwable throwable) {
                if (!call.isCanceled()) {
                    callback.onError("Lỗi kết nối mạng: " + throwable.getMessage());
                }
            }
        });
    }

    public void reorderAccount(String accountId, int newOrder, AccountCallback<Void> callback) {
        apiService.reorderAccount(accountId, new ReorderAccountRequest(newOrder)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Không thể thay đổi thứ tự: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable throwable) {
                if (!call.isCanceled()) {
                    callback.onError("Lỗi kết nối mạng: " + throwable.getMessage());
                }
            }
        });
    }

    /**
     * Xóa tài khoản với các chế độ khác nhau
     * @param accountId ID của ví cần xóa
     * @param mode Chế độ xóa: "soft_delete", "delete_all", hoặc "move"
     * @param fallbackAccountId ID của ví dự phòng (chỉ cần thiết và bắt buộc khi mode = "move", còn lại truyền null)
     * @param callback Trả kết quả về cho UI
     */
    public void deleteAccount(String accountId, String mode, String fallbackAccountId, AccountCallback<Void> callback) {
        if (accountId == null || accountId.isEmpty()) {
            callback.onError("ID ví cần xóa không hợp lệ");
            return;
        }

        if (mode == null || mode.isEmpty()) {
            mode = "soft_delete";
        }

        if (mode.equalsIgnoreCase("move") && (fallbackAccountId == null || fallbackAccountId.isEmpty())) {
            callback.onError("Vui lòng chọn một ví dự phòng để chuyển dữ liệu sang!");
            return;
        }

        apiService.deleteAccount(accountId, mode.toLowerCase(), fallbackAccountId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Không thể xóa ví: Lỗi " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable throwable) {
                if (!call.isCanceled()) {
                    callback.onError("Lỗi kết nối mạng: " + throwable.getMessage());
                }
            }
        });
    }
}