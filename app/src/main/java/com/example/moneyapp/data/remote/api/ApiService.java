package com.example.moneyapp.data.remote.api;

import com.example.moneyapp.data.remote.request.CategoryRequest;
import com.example.moneyapp.data.remote.request.ChangePasswordRequest;
import com.example.moneyapp.data.remote.request.CheckInRequest;
import com.example.moneyapp.data.remote.request.GoogleLoginRequest;
import com.example.moneyapp.data.remote.request.LoginRequest;
import com.example.moneyapp.data.remote.request.LogoutRequest;
import com.example.moneyapp.data.remote.request.RegisterRequest;
import com.example.moneyapp.data.remote.request.ReorderCategoryRequest;
import com.example.moneyapp.data.remote.request.TransactionRequest;
import com.example.moneyapp.data.remote.request.UserProfileRequest;
import com.example.moneyapp.data.remote.response.AccountResponse;
import com.example.moneyapp.data.remote.response.AdjustBalanceResponse;
import com.example.moneyapp.data.remote.response.AuthResponse;
import com.example.moneyapp.data.remote.response.CashFlowBarDto;
import com.example.moneyapp.data.remote.response.CategoryPieChartDto;
import com.example.moneyapp.data.remote.response.CategoryResponse;
import com.example.moneyapp.data.remote.response.StackedBarChartDto;
import com.example.moneyapp.data.remote.response.TransactionResponse;
import com.example.moneyapp.data.remote.response.TransferResponse;
import com.example.moneyapp.data.remote.response.UserProfileResponse;

import java.util.List;

import kotlinx.serialization.Polymorphic;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface ApiService {
    //region Auth
    @POST("/api/Auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);
    @POST("/api/Auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);
    @POST("api/Auth/google-login")
    Call<AuthResponse> googleLogin(@Body GoogleLoginRequest request);
    @POST("/api/Auth/logout")
    Call<Void> logout(@Body LogoutRequest request);
    @POST("/api/Auth/refresh-token")
    Call<AuthResponse> refreshToken();
    @POST("api/Auth/change-password")
    Call<Void> changePassword(@Body ChangePasswordRequest request);
    //endregion

    //region User
    @GET("/api/User")
    Call<UserProfileResponse> getUserProfile(String clientToday);
    @POST("api/User/checkin")
    Call<Void> checkIn(@Body CheckInRequest request);
    @PUT("/api/User")
    Call<Void> updateUserProfile(@Body UserProfileRequest request);
    @DELETE("/api/User")
    Call<Void> deleteUser(String mode);
    //endregion

    //region Account
    @GET("api/Account")
    Call<List<AccountResponse>> getAllAccounts();
    @GET("api/Account/{id}")
    Call<AccountResponse> getAccountById(String id);
    @GET("api/Account/total-balance")
    Call<Double> getTotalBalance();
    @POST("api/Account")
    Call<AccountResponse> createAccount(@Body AccountResponse request);
    @PUT("api/Account")
    Call<AccountResponse> updateAccount(@Body AccountResponse request);
    @DELETE("api/Account/{id}")
    Call<Void> deleteAccount(String id, String mode, String fallbackAccountId);
    //endregion

    //region Category
    @GET("/api/Category/expense")
    Call<List<CategoryResponse>> getAllExpenseCategories();
    @GET("/api/Category/income")
    Call<List<CategoryResponse>> getAllIncomeCategories();
    @GET("/api/Category/{id}")
    Call<CategoryResponse> getCategoryById(String id);
    @POST("/api/Category/expense")
    Call<Void> createExpenseCategory(@Body CategoryRequest request);
    @POST("/api/Category/income")
    Call<Void> createIncomeCategory(@Body CategoryRequest request);
    @PUT("/api/Category/expense/{id}")
    Call<Void> updateExpenseCategory(String id, @Body CategoryRequest request);
    @PUT("/api/Category/income/{id}")
    Call<Void> updateIncomeCategory(String id, @Body CategoryRequest request);
    @PUT("/api/Category/expense/reorder/{id}")
    Call<Void> reorderExpenseCategory(String id, @Body ReorderCategoryRequest request);
    @PUT("/api/Category/income/reorder/{id}")
    Call<Void> reorderIncomeCategory(String id, @Body ReorderCategoryRequest request);
    @DELETE("/api/Category/{id}")
    Call<Void> deleteCategory(String id, String mode, String fallbackCategoryId);
    //endregion

    //region Transaction
    @GET("/api/Transaction")
    Call<List<TransactionResponse>> getTransactions(String startDate,
                                                    String endDate,
                                                    int categoryType,
                                                    String accountId,
                                                    String categoryId);
    @GET("/api/Transaction/{id}")
    Call<TransactionResponse> getTransactionById(String id);
    @POST("/api/Transaction")
    Call<TransactionResponse> createTransaction(@Body TransactionRequest request);
    @PUT("/api/Transaction/{id}")
    Call<TransactionResponse> updateTransaction(String id, @Body TransactionRequest request);
    @DELETE("/api/Transaction/{id}")
    Call<Void> deleteTransaction(String id);
    //endregion

    //region Transfer
    @GET("/api/Transfer")
    Call<List<TransferResponse>> getTransfers(String startDate,
                                              String endDate,
                                              String source,
                                              String destination);
    @GET("/api/Transfer/{id}")
    Call<TransferResponse> getTransferById(String id);
    @POST("/api/Transfer")
    Call<TransferResponse> createTransfer(@Body TransactionRequest request);
    @PUT("/api/Transfer/{id}")
    Call<TransferResponse> updateTransfer(String id, @Body TransactionRequest request);
    @DELETE("/api/Transfer/{id}")
    Call<Void> deleteTransfer(String id);
    //endregion

    //region Adjust Balance
    @GET("/api/AdjustBalance")
    Call<List<AdjustBalanceResponse>> getAdjustBalances(String startDate,
                                                        String endDate,
                                                        String accountId);
    @POST("/api/AdjustBalance")
    Call<Void> adjustBalance(@Body TransactionRequest request);
    //endregion

    //region Statistic
    @GET("/api/Statistic/pie-chart/expense")
    Call<List<CategoryPieChartDto>> getExpensePieChart(String startDate,
                                                       String endDate,
                                                       int timeZoneOffset);
    @GET("/api/Statistic/pie-chart/income")
    Call<List<CategoryPieChartDto>> getIncomePieChart(String startDate,
                                                      String endDate,
                                                      int timeZoneOffset);
    @GET("/api/Statistic/stacked-bar-chart/expense")
    Call<List<StackedBarChartDto>> getExpenseStackedBarChart(String startDate,
                                                             String endDate,
                                                             int groupBy,
                                                             int timeZoneOffset);
    @GET("/api/Statistic/stacked-bar-chart/income")
    Call<List<StackedBarChartDto>> getIncomeStackedBarChart(String startDate,
                                                            String endDate,
                                                            int groupBy,
                                                            int timeZoneOffset);
    @GET("/api/Statistic/bar-chart/cashflow")
    Call<List<CashFlowBarDto>> getCashFlowBarChart(String startDate,
                                                   String endDate,
                                                   int groupBy,
                                                   int timeZoneOffset);
    //endregion


}
