package com.example.moneyapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moneyapp.data.remote.request.GoalRequest;
import com.example.moneyapp.data.remote.response.GoalRecordDeleteResponse;
import com.example.moneyapp.data.remote.response.GoalRecordResponse;
import com.example.moneyapp.data.remote.response.GoalTransactionResponse;
import com.example.moneyapp.data.repository.GoalRepository;
import com.example.moneyapp.model.Goal;

import java.util.List;

public class GoalViewModel extends AndroidViewModel {

    private final GoalRepository goalRepository;

    private final MutableLiveData<List<Goal>> goals = new MutableLiveData<>();
    private final MutableLiveData<List<GoalRecordResponse>> goalRecords = new MutableLiveData<>();

    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isOperationSuccess = new MutableLiveData<>();

    public GoalViewModel(@NonNull Application application) {
        super(application);
        goalRepository = new GoalRepository(application);
    }

    public LiveData<List<Goal>> getGoals() { return goals; }
    public LiveData<List<GoalRecordResponse>> getGoalRecords() { return goalRecords; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<Boolean> getIsOperationSuccess() { return isOperationSuccess; }

    public void fetchGoals() {
        isLoading.setValue(true);
        goalRepository.getAllGoals(new GoalRepository.GoalCallback<List<Goal>>() {
            @Override
            public void onSuccess(List<Goal> result) {
                goals.setValue(result);
                isLoading.setValue(false);
            }

            @Override
            public void onError(String message) {
                error.setValue(message);
                isLoading.setValue(false);
            }
        });
    }

    public void addGoal(GoalRequest request) {
        isLoading.setValue(true);
        goalRepository.createGoal(request, new GoalRepository.GoalCallback<Goal>() {
            @Override
            public void onSuccess(Goal result) {
                isOperationSuccess.setValue(true);
                isLoading.setValue(false);
                fetchGoals();
            }

            @Override
            public void onError(String message) {
                error.setValue(message);
                isLoading.setValue(false);
            }
        });
    }

    public void updateGoal(int id, GoalRequest request) {
        isLoading.setValue(true);
        goalRepository.updateGoal(id, request, new GoalRepository.GoalCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                isOperationSuccess.setValue(true);
                isLoading.setValue(false);
                fetchGoals();
            }

            @Override
            public void onError(String message) {
                error.setValue(message);
                isLoading.setValue(false);
            }
        });
    }

    public void deleteGoal(int id) {
        isLoading.setValue(true);
        goalRepository.deleteGoal(id, new GoalRepository.GoalCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                isOperationSuccess.setValue(true);
                isLoading.setValue(false);
                fetchGoals();
            }

            @Override
            public void onError(String message) {
                error.setValue(message);
                isLoading.setValue(false);
            }
        });
    }

    public void depositToGoal(int goalId, double amount, String accountId) {
        isLoading.setValue(true);
        // Code sạch sẽ tuyệt đối, Backend sẽ tự lo liệu mọi logic trừ/khóa tiền
        goalRepository.depositToGoal(goalId, amount, accountId, new GoalRepository.GoalCallback<GoalTransactionResponse>() {
            @Override
            public void onSuccess(GoalTransactionResponse result) {
                isOperationSuccess.setValue(true);
                isLoading.setValue(false);
                fetchGoals(); // Cập nhật lại UI màn hình chính
            }

            @Override
            public void onError(String message) {
                error.setValue(message);
                isLoading.setValue(false);
            }
        });
    }

    public void withdrawFromGoal(int goalId, double amount, String accountId) {
        isLoading.setValue(true);
        goalRepository.withdrawFromGoal(goalId, amount, accountId, new GoalRepository.GoalCallback<GoalTransactionResponse>() {
            @Override
            public void onSuccess(GoalTransactionResponse result) {
                isOperationSuccess.setValue(true);
                isLoading.setValue(false);
                fetchGoals(); // Cập nhật lại UI màn hình chính
            }

            @Override
            public void onError(String message) {
                error.setValue(message);
                isLoading.setValue(false);
            }
        });
    }

    // ==========================================
    // 3. LỊCH SỬ GIAO DỊCH (RECORDS)
    // ==========================================

    public void fetchGoalRecords(int goalId) {
        isLoading.setValue(true);
        goalRepository.getGoalRecords(goalId, new GoalRepository.GoalCallback<List<GoalRecordResponse>>() {
            @Override
            public void onSuccess(List<GoalRecordResponse> result) {
                goalRecords.setValue(result);
                isLoading.setValue(false);
            }

            @Override
            public void onError(String message) {
                error.setValue(message);
                isLoading.setValue(false);
            }
        });
    }

    public void deleteGoalRecord(int recordId, int currentGoalId) {
        isLoading.setValue(true);
        goalRepository.deleteGoalRecord(recordId, new GoalRepository.GoalCallback<GoalRecordDeleteResponse>() {
            @Override
            public void onSuccess(GoalRecordDeleteResponse result) {
                isOperationSuccess.setValue(true);
                isLoading.setValue(false);
                // Sau khi xóa lịch sử, cần tải lại danh sách lịch sử VÀ tải lại danh sách mục tiêu
                fetchGoalRecords(currentGoalId);
                fetchGoals();
            }

            @Override
            public void onError(String message) {
                error.setValue(message);
                isLoading.setValue(false);
            }
        });
    }

    public void resetOperationStatus() {
        isOperationSuccess.setValue(false);
    }
}