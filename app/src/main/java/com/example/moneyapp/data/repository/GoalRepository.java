package com.example.moneyapp.data.repository;

import android.content.Context;

import com.example.moneyapp.data.remote.request.DepositRequest;
import com.example.moneyapp.data.remote.request.GoalRequest;
import com.example.moneyapp.data.remote.request.WithdrawRequest;
import com.example.moneyapp.data.remote.response.GoalRecordDeleteResponse;
import com.example.moneyapp.data.remote.response.GoalRecordResponse;
import com.example.moneyapp.data.remote.response.GoalResponse;
import com.example.moneyapp.data.remote.response.GoalTransactionResponse;
import com.example.moneyapp.model.Goal;
import com.example.moneyapp.utils.DateConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoalRepository extends BaseRepository {

    public interface GoalCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    public GoalRepository(Context context) {
        super(context);
    }

    private Goal mapToGoal(GoalResponse response) {
        return new Goal(
                response.getId(),
                response.getName(),
                response.getTargetAmount(),
                response.getCurrentAmount(),
                response.getDeadline(),
                response.getIconId(),
                response.getColorId(),
                response.isActive()
        );
    }
    public void getAllGoals(GoalCallback<List<Goal>> callback) {
        apiService.getAllGoals().enqueue(new Callback<List<GoalResponse>>() {
            @Override
            public void onResponse(Call<List<GoalResponse>> call, Response<List<GoalResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Goal> goals = new ArrayList<>();
                    for (GoalResponse res : response.body()) {
                        goals.add(mapToGoal(res));
                    }
                    callback.onSuccess(goals);
                } else {
                    callback.onError(parseError(response, "Failed to fetch goals"));
                }
            }

            @Override
            public void onFailure(Call<List<GoalResponse>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void createGoal(GoalRequest request, GoalCallback<Goal> callback) {
        apiService.createGoal(request).enqueue(new Callback<GoalResponse>() {
            @Override
            public void onResponse(Call<GoalResponse> call, Response<GoalResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(mapToGoal(response.body()));
                } else {
                    callback.onError(parseError(response, "Failed to create goal"));
                }
            }

            @Override
            public void onFailure(Call<GoalResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void updateGoal(int id, GoalRequest request, GoalCallback<Void> callback) {
        apiService.updateGoal(id, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError(parseError(response, "Failed to update goal"));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void deleteGoal(int id, GoalCallback<Void> callback) {
        apiService.deleteGoal(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError(parseError(response, "Failed to delete goal"));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
    public void depositToGoal(int id, double amount, String accountId, GoalCallback<GoalTransactionResponse> callback) {
        apiService.depositToGoal(id, new DepositRequest(amount, accountId)).enqueue(new Callback<GoalTransactionResponse>() {
            @Override
            public void onResponse(Call<GoalTransactionResponse> call, Response<GoalTransactionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(parseError(response, "Failed to deposit"));
                }
            }

            @Override
            public void onFailure(Call<GoalTransactionResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // ĐÃ THÊM: Hàm rút tiền
    public void withdrawFromGoal(int id, double amount, String accountId, GoalCallback<GoalTransactionResponse> callback) {
        apiService.withdrawFromGoal(id, new WithdrawRequest(amount, accountId)).enqueue(new Callback<GoalTransactionResponse>() {
            @Override
            public void onResponse(Call<GoalTransactionResponse> call, Response<GoalTransactionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(parseError(response, "Failed to withdraw"));
                }
            }

            @Override
            public void onFailure(Call<GoalTransactionResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
    public void getGoalRecords(int id, GoalCallback<List<GoalRecordResponse>> callback) {
        apiService.getGoalRecords(id).enqueue(new Callback<List<GoalRecordResponse>>() {
            @Override
            public void onResponse(Call<List<GoalRecordResponse>> call, Response<List<GoalRecordResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(parseError(response, "Failed to fetch records"));
                }
            }

            @Override
            public void onFailure(Call<List<GoalRecordResponse>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getGoalRecordById(int recordId, GoalCallback<GoalRecordResponse> callback) {
        apiService.getGoalRecordById(recordId).enqueue(new Callback<GoalRecordResponse>() {
            @Override
            public void onResponse(Call<GoalRecordResponse> call, Response<GoalRecordResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(parseError(response, "Failed to fetch record detail"));
                }
            }

            @Override
            public void onFailure(Call<GoalRecordResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getAllGoalRecords(Date startDate, Date endDate, String accountId, GoalCallback<List<GoalRecordResponse>> callback) {
        String startStr = startDate != null ? DateConverter.convertDateToString(startDate) : null;
        String endStr = endDate != null ? DateConverter.convertDateToString(endDate) : null;

        apiService.getAllGoalRecords(startStr, endStr, accountId).enqueue(new Callback<List<GoalRecordResponse>>() {
            @Override
            public void onResponse(Call<List<GoalRecordResponse>> call, Response<List<GoalRecordResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(parseError(response, "Lỗi tải lịch sử tiết kiệm"));
                }
            }
            @Override
            public void onFailure(Call<List<GoalRecordResponse>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void deleteGoalRecord(int recordId, GoalCallback<GoalRecordDeleteResponse> callback) {
        apiService.deleteGoalRecord(recordId).enqueue(new Callback<GoalRecordDeleteResponse>() {
            @Override
            public void onResponse(Call<GoalRecordDeleteResponse> call, Response<GoalRecordDeleteResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(parseError(response, "Failed to delete record"));
                }
            }

            @Override
            public void onFailure(Call<GoalRecordDeleteResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}