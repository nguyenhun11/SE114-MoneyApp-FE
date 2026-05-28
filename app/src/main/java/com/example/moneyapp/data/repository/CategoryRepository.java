package com.example.moneyapp.data.repository;

import android.content.Context;
import androidx.annotation.NonNull;

import com.example.moneyapp.data.remote.request.CategoryRequest;
import com.example.moneyapp.data.remote.request.ReorderCategoryRequest;
import com.example.moneyapp.data.remote.response.CategoryResponse;
import com.example.moneyapp.model.Category;
import com.example.moneyapp.model.CategoryType;
import com.example.moneyapp.utils.DateConverter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryRepository extends BaseRepository {

    public interface CategoryCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    public CategoryRepository(Context context) {
        super(context);
    }

    private Category mapToCategory(CategoryResponse response) {
        return new Category(
                response.getId(),
                response.getName(),
                response.getIconId(),
                response.getColorId(),
                response.getCategoryType() == 1 ? CategoryType.INCOME : CategoryType.EXPENSE,
                response.getSortingOrder(),
                DateConverter.convertStringToDate(response.getCreatedAt()),
                DateConverter.convertStringToDate(response.getLastUpdatedAt())
        );
    }

    public void getExpenseCategories(CategoryCallback<List<Category>> callback) {
        apiService.getAllExpenseCategories().enqueue(new Callback<List<CategoryResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<CategoryResponse>> call, @NonNull Response<List<CategoryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = new ArrayList<>();
                    for (CategoryResponse res : response.body()) {
                        categories.add(mapToCategory(res));
                    }
                    callback.onSuccess(categories);
                } else {
                    callback.onError("Không tải được danh mục chi: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CategoryResponse>> call, @NonNull Throwable throwable) {
                callback.onError("Lỗi kết nối: " + throwable.getMessage());
            }
        });
    }

    public void getIncomeCategories(CategoryCallback<List<Category>> callback) {
        apiService.getAllIncomeCategories().enqueue(new Callback<List<CategoryResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<CategoryResponse>> call, @NonNull Response<List<CategoryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = new ArrayList<>();
                    for (CategoryResponse res : response.body()) {
                        categories.add(mapToCategory(res));
                    }
                    callback.onSuccess(categories);
                } else {
                    callback.onError("Không tải được danh mục thu: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CategoryResponse>> call, @NonNull Throwable throwable) {
                callback.onError("Lỗi kết nối: " + throwable.getMessage());
            }
        });
    }

    public void insertCategory(Category category, CategoryCallback<Void> callback) {
        CategoryRequest request = new CategoryRequest(
                category.getCategoryName(),
                category.getIcon(),
                category.getColor()
        );

        Call<Void> call;
        if (category.getType() == CategoryType.INCOME) {
            call = apiService.createIncomeCategory(request);
        } else {
            call = apiService.createExpenseCategory(request);
        }

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Tạo danh mục thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable throwable) {
                callback.onError("Lỗi kết nối: " + throwable.getMessage());
            }
        });
    }

    public void updateCategory(Category category, CategoryCallback<Void> callback) {
        CategoryRequest request = new CategoryRequest(
                category.getCategoryName(),
                category.getIcon(),
                category.getColor()
        );

        Call<Void> call;
        if (category.getType() == CategoryType.INCOME) {
            call = apiService.updateIncomeCategory(category.getCategoryId(), request);
        } else {
            call = apiService.updateExpenseCategory(category.getCategoryId(), request);
        }

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Cập nhật danh mục thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable throwable) {
                callback.onError("Lỗi kết nối: " + throwable.getMessage());
            }
        });
    }

    public void deleteCategory(String id, String mode, String fallbackId, CategoryCallback<Void> callback) {
        apiService.deleteCategory(id, mode, fallbackId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Xóa danh mục thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable throwable) {
                callback.onError("Lỗi kết nối: " + throwable.getMessage());
            }
        });
    }

    public void reorderCategory(Category category, int newOrder, CategoryCallback<Void> callback) {
        ReorderCategoryRequest request = new ReorderCategoryRequest(newOrder);
        Call<Void> call;
        if (category.getType() == CategoryType.INCOME) {
            call = apiService.reorderIncomeCategory(category.getCategoryId(), request);
        } else {
            call = apiService.reorderExpenseCategory(category.getCategoryId(), request);
        }

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Đổi thứ tự thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable throwable) {
                callback.onError("Lỗi kết nối: " + throwable.getMessage());
            }
        });
    }
}
