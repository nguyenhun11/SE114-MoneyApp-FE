package com.example.moneyapp.data.repository;

import android.content.Context;

import com.example.moneyapp.data.remote.request.DepositRequest;
import com.example.moneyapp.data.remote.request.GoalRequest;
import com.example.moneyapp.data.remote.response.GoalResponse;
import com.example.moneyapp.model.Goal;

import java.util.ArrayList;
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
                    callback.onError("Failed to fetch goals: " + response.code());
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
                    callback.onError("Failed to create goal: " + response.code());
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
                    callback.onError("Failed to update goal: " + response.code());
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
                    callback.onError("Failed to delete goal: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void depositToGoal(int id, double amount, GoalCallback<Goal> callback) {
        apiService.depositToGoal(id, new DepositRequest(amount)).enqueue(new Callback<GoalResponse>() {
            @Override
            public void onResponse(Call<GoalResponse> call, Response<GoalResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(mapToGoal(response.body()));
                } else {
                    callback.onError("Failed to deposit: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<GoalResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}
