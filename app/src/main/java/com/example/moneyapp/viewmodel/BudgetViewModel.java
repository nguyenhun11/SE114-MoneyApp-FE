package com.example.moneyapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moneyapp.data.remote.request.BudgetRequest;
import com.example.moneyapp.data.remote.response.BudgetResponse;
import com.example.moneyapp.data.repository.BudgetRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BudgetViewModel extends AndroidViewModel {
    private final BudgetRepository repository;
    private final MutableLiveData<List<BudgetResponse>> budgets = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> operationSuccess = new MutableLiveData<>();

    public BudgetViewModel(@NonNull Application application) {
        super(application);
        this.repository = new BudgetRepository(application);
    }

    public LiveData<List<BudgetResponse>> getBudgets() { return budgets; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<Boolean> getOperationSuccess() { return operationSuccess; }

    public void fetchBudgets() {
        loading.setValue(true);
        repository.getAllBudgets().enqueue(new Callback<List<BudgetResponse>>() {
            @Override
            public void onResponse(Call<List<BudgetResponse>> call, Response<List<BudgetResponse>> response) {
                loading.setValue(false);
                if (response.isSuccessful()) {
                    budgets.setValue(response.body());
                } else {
                    error.setValue("Failed to fetch budgets");
                }
            }

            @Override
            public void onFailure(Call<List<BudgetResponse>> call, Throwable t) {
                loading.setValue(false);
                error.setValue(t.getMessage());
            }
        });
    }

    public void createBudget(BudgetRequest request) {
        loading.setValue(true);
        repository.createBudget(request).enqueue(new Callback<BudgetResponse>() {
            @Override
            public void onResponse(Call<BudgetResponse> call, Response<BudgetResponse> response) {
                loading.setValue(false);
                if (response.isSuccessful()) {
                    operationSuccess.setValue(true);
                    fetchBudgets();
                } else {
                    error.setValue("Failed to create budget");
                }
            }

            @Override
            public void onFailure(Call<BudgetResponse> call, Throwable t) {
                loading.setValue(false);
                error.setValue(t.getMessage());
            }
        });
    }

    public void updateBudget(int id, BudgetRequest request) {
        loading.setValue(true);
        repository.updateBudget(id, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                loading.setValue(false);
                if (response.isSuccessful()) {
                    operationSuccess.setValue(true);
                    fetchBudgets();
                } else {
                    error.setValue("Failed to update budget");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                loading.setValue(false);
                error.setValue(t.getMessage());
            }
        });
    }

    public void deleteBudget(int id) {
        loading.setValue(true);
        repository.deleteBudget(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                loading.setValue(false);
                if (response.isSuccessful()) {
                    operationSuccess.setValue(true);
                    fetchBudgets();
                } else {
                    error.setValue("Failed to delete budget");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                loading.setValue(false);
                error.setValue(t.getMessage());
            }
        });
    }
    
    public void resetOperationSuccess() {
        operationSuccess.setValue(false);
    }
}
