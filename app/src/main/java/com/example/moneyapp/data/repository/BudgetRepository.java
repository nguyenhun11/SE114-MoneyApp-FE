package com.example.moneyapp.data.repository;

import android.content.Context;

import com.example.moneyapp.data.remote.request.BudgetRequest;
import com.example.moneyapp.data.remote.response.BudgetResponse;

import java.util.List;

import retrofit2.Call;

public class BudgetRepository extends BaseRepository {
    public BudgetRepository(Context context) {
        super(context);
    }

    public Call<List<BudgetResponse>> getAllBudgets() {
        return apiService.getAllBudgets();
    }

    public Call<BudgetResponse> createBudget(BudgetRequest request) {
        return apiService.createBudget(request);
    }

    public Call<Void> updateBudget(int id, BudgetRequest request) {
        return apiService.updateBudget(id, request);
    }

    public Call<Void> deleteBudget(int id) {
        return apiService.deleteBudget(id);
    }
}
