package com.example.moneyapp.data.remote.api;

import com.example.moneyapp.data.remote.request.AccountRequest;
import com.example.moneyapp.data.remote.request.AdjustBalanceRequest;
import com.example.moneyapp.data.remote.request.CategoryGroupRequest;
import com.example.moneyapp.data.remote.response.CategoryGroupResponse;
import com.example.moneyapp.data.remote.request.CategoryRequest;
import com.example.moneyapp.data.remote.request.ChangePasswordRequest;
import com.example.moneyapp.data.remote.request.CheckInRequest;
import com.example.moneyapp.data.remote.request.ForgotPasswordRequest;
import com.example.moneyapp.data.remote.request.GoogleLoginRequest;
import com.example.moneyapp.data.remote.request.LoginRequest;
import com.example.moneyapp.data.remote.request.LogoutRequest;
import com.example.moneyapp.data.remote.request.RefreshTokenRequest;
import com.example.moneyapp.data.remote.request.RegisterRequest;
import com.example.moneyapp.data.remote.request.ReorderAccountRequest;
import com.example.moneyapp.data.remote.request.ReorderCategoryRequest;
import com.example.moneyapp.data.remote.request.ResetPasswordRequest;
import com.example.moneyapp.data.remote.request.TransactionRequest;
import com.example.moneyapp.data.remote.request.TransferRequest;
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

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    //region Auth
    @POST("api/Auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);
    @POST("api/Auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);
    @POST("api/Auth/google-login")
    Call<AuthResponse> googleLogin(@Body GoogleLoginRequest request);
    @POST("api/Auth/logout")
    Call<Void> logout(@Body LogoutRequest request);
    @POST("api/Auth/refresh-token")
    Call<AuthResponse> refreshToken(@Body RefreshTokenRequest request);
    @POST("api/Auth/change-password")
    Call<Void> changePassword(@Body ChangePasswordRequest request);
    @POST("api/Auth/forgot-password")
    Call<Void> forgotPassword(@Body ForgotPasswordRequest request);
    @POST("api/Auth/reset-password")
    Call<Void> resetPassword(@Body ResetPasswordRequest request);
    //endregion

    //region User
    @GET("api/User")
    Call<UserProfileResponse> getUserProfile(@Query("clientToday") String clientToday);
    @POST("api/User/checkin")
    Call<Void> checkIn(@Body CheckInRequest request);
    @PUT("api/User")
    Call<Void> updateUserProfile(@Body UserProfileRequest request);
    @DELETE("api/User")
    Call<Void> deleteUser(@Query("mode") String mode);
    //endregion

    //region Account
    @GET("api/Account")
    Call<List<AccountResponse>> getAllAccounts();
    @GET("api/Account/{id}")
    Call<AccountResponse> getAccountById(@Path("id") String id);
    @GET("api/Account/total-balance")
    Call<Double> getTotalBalance();
    @POST("api/Account")
    Call<AccountResponse> createAccount(@Body AccountRequest request);
    @PUT("api/Account/{id}")
    Call<AccountResponse> updateAccount(@Path("id") String id, @Body AccountRequest request);
    @PUT("api/Account/reorder/{id}")
    Call<Void> reorderAccount(@Path("id") String id, @Body ReorderAccountRequest request);
    @DELETE("api/Account/{id}")
    Call<Void> deleteAccount(
            @Path("id") String id,
            @Query("mode") String mode,
            @Query("fallbackAccountId") String fallbackAccountId
    );
    //endregion

    //region Category Group
    @GET("api/CategoryGroup/expense")
    Call<List<CategoryGroupResponse>> getAllExpenseCategoryGroups();
    @GET("api/CategoryGroup/income")
    Call<List<CategoryGroupResponse>> getAllIncomeCategoryGroups();
    @POST("api/CategoryGroup/expense")
    Call<CategoryGroupResponse> createExpenseCategoryGroup(@Body CategoryGroupRequest request);
    @POST("api/CategoryGroup/income")
    Call<CategoryGroupResponse> createIncomeCategoryGroup(@Body CategoryGroupRequest request);
    @PUT("api/CategoryGroup/{id}")
    Call<Void> updateCategoryGroup(@Path("id") String id, @Body CategoryGroupRequest request);
    @PUT("api/CategoryGroup/reorder/{id}")
    Call<Void> reorderCategoryGroup(@Path("id") String id, @Body ReorderCategoryRequest request);
    @DELETE("api/CategoryGroup/{id}")
    Call<Void> deleteCategoryGroup(@Path("id") String id);
    //endregion

    //region Category
    @GET("api/Category/expense")
    Call<List<CategoryResponse>> getAllExpenseCategories();
    @GET("api/Category/income")
    Call<List<CategoryResponse>> getAllIncomeCategories();
    @GET("api/Category/{id}")
    Call<CategoryResponse> getCategoryById(@Path("id") String id);
    @GET("api/Category/group/{groupId}")
    Call<List<CategoryResponse>> getCategoriesByGroupId(@Path("groupId") String groupId);
    @POST("api/Category/expense")
    Call<CategoryResponse> createExpenseCategory(@Body CategoryRequest request);
    @POST("api/Category/income")
    Call<CategoryResponse> createIncomeCategory(@Body CategoryRequest request);
    @PUT("api/Category/expense/{id}")
    Call<Void> updateExpenseCategory(@Path("id") String id, @Body CategoryRequest request);
    @PUT("api/Category/income/{id}")
    Call<Void> updateIncomeCategory(@Path("id") String id, @Body CategoryRequest request);
    @PUT("api/Category/expense/reorder/{id}")
    Call<Void> reorderExpenseCategory(@Path("id") String id, @Body ReorderCategoryRequest request);
    @PUT("api/Category/income/reorder/{id}")
    Call<Void> reorderIncomeCategory(@Path("id") String id, @Body ReorderCategoryRequest request);
    @DELETE("api/Category/{id}")
    Call<Void> deleteCategory(
            @Path("id") String id,
            @Query("mode") String mode,
            @Query("fallbackCategoryId") String fallbackCategoryId
    );
    //endregion

    //region Transaction
    @GET("api/Transaction")
    Call<List<TransactionResponse>> getTransactions(
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("categoryType") Integer categoryType, // Dùng Integer để cho phép null nếu API cần
            @Query("accountId") String accountId,
            @Query("categoryId") String categoryId
    );
    @GET("api/Transaction/{id}")
    Call<TransactionResponse> getTransactionById(@Path("id") String id);
    @POST("api/Transaction")
    Call<TransactionResponse> createTransaction(@Body TransactionRequest request);
    @PUT("api/Transaction/{id}")
    Call<TransactionResponse> updateTransaction(@Path("id") String id, @Body TransactionRequest request);
    @DELETE("api/Transaction/{id}")
    Call<Void> deleteTransaction(@Path("id") String id);
    //endregion

    //region Transfer
    @GET("api/Transfer")
    Call<List<TransferResponse>> getTransfers(
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("source") String source,
            @Query("destination") String destination
    );
    @GET("api/Transfer/{id}")
    Call<TransferResponse> getTransferById(@Path("id") String id);
    @POST("api/Transfer")
    Call<TransferResponse> createTransfer(@Body TransferRequest request);
    @PUT("api/Transfer/{id}")
    Call<TransferResponse> updateTransfer(@Path("id") String id, @Body TransactionRequest request);
    @DELETE("api/Transfer/{id}")
    Call<Void> deleteTransfer(@Path("id") String id);
    //endregion

    //region Adjust Balance
    @GET("api/AdjustBalance")
    Call<List<AdjustBalanceResponse>> getAdjustBalances(
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("accountId") String accountId
    );
    @POST("api/AdjustBalance")
    Call<Void> adjustBalance(@Body AdjustBalanceRequest request);
    //endregion

    //region Statistic
    @GET("api/Statistic/pie-chart/expense")
    Call<List<CategoryPieChartDto>> getExpensePieChart(
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("timeZoneOffset") int timeZoneOffset
    );
    @GET("api/Statistic/pie-chart/income")
    Call<List<CategoryPieChartDto>> getIncomePieChart(
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("timeZoneOffset") int timeZoneOffset
    );
    @GET("api/Statistic/stacked-bar-chart/expense")
    Call<List<StackedBarChartDto>> getExpenseStackedBarChart(
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("groupBy") int groupBy,
            @Query("timeZoneOffset") int timeZoneOffset
    );
    @GET("api/Statistic/stacked-bar-chart/income")
    Call<List<StackedBarChartDto>> getIncomeStackedBarChart(
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("groupBy") int groupBy,
            @Query("timeZoneOffset") int timeZoneOffset
    );
    @GET("api/Statistic/bar-chart/cashflow")
    Call<List<CashFlowBarDto>> getCashFlowBarChart(
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("groupBy") int groupBy,
            @Query("timeZoneOffset") int timeZoneOffset
    );
    //endregion
}