package com.example.moneyapp.data.repository;

import android.content.Context;
import androidx.annotation.NonNull;

import com.example.moneyapp.data.remote.request.CategoryGroupRequest;
import com.example.moneyapp.data.remote.request.CategoryGroupResponse;
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
                response.getCategoryName(),
                response.getType() == 1 ? CategoryType.INCOME : CategoryType.EXPENSE,
                response.getGroupId(),
                response.getGroupName(),
                response.getMonthlyTarget(),
                response.getColorId(),
                response.getIconId(),
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

    public void createCategory(Category category, CategoryCallback<Void> callback) {
        CategoryRequest request = new CategoryRequest(
                category.getCategoryName(),
                category.getMonthlyTarget(),
                category.getColor(),
                category.getIcon(),
                category.getGroupId()
        );


        Call<CategoryResponse> call;
        if (category.getType() == CategoryType.INCOME) {
            call = apiService.createIncomeCategory(request);
        } else {
            call = apiService.createExpenseCategory(request);
        }

        call.enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(@NonNull Call<CategoryResponse> call, @NonNull Response<CategoryResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Tạo danh mục thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<CategoryResponse> call, @NonNull Throwable throwable) {
                callback.onError("Lỗi kết nối: " + throwable.getMessage());
            }
        });
    }

    public void updateCategory(Category category, CategoryCallback<Void> callback) {
        CategoryRequest request = new CategoryRequest(
                category.getCategoryName(),
                category.getMonthlyTarget(),
                category.getColor(),
                category.getIcon(),
                category.getGroupId()
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

    public void createExpenseCategoryGroup(CategoryGroupRequest request, CategoryCallback<CategoryGroupResponse> callback) {
        apiService.createExpenseCategoryGroup(request).enqueue(new Callback<CategoryGroupResponse>() {
            @Override
            public void onResponse(@NonNull Call<CategoryGroupResponse> call, @NonNull Response<CategoryGroupResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Tạo nhóm chi thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<CategoryGroupResponse> call, @NonNull Throwable throwable) {
                callback.onError("Lỗi kết nối: " + throwable.getMessage());
            }
        });
    }

    public void createIncomeCategoryGroup(CategoryGroupRequest request, CategoryCallback<CategoryGroupResponse> callback) {
        apiService.createIncomeCategoryGroup(request).enqueue(new Callback<CategoryGroupResponse>() {
            @Override
            public void onResponse(@NonNull Call<CategoryGroupResponse> call, @NonNull Response<CategoryGroupResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Tạo nhóm thu thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<CategoryGroupResponse> call, @NonNull Throwable throwable) {
                callback.onError("Lỗi kết nối: " + throwable.getMessage());
            }
        });
    }

    public void getAllExpenseCategoryGroups(CategoryCallback<List<CategoryGroupResponse>> callback) {
        apiService.getAllExpenseCategoryGroups().enqueue(new Callback<List<CategoryGroupResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<CategoryGroupResponse>> call, @NonNull Response<List<CategoryGroupResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CategoryGroupResponse>> call, @NonNull Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getAllIncomeCategoryGroups(CategoryCallback<List<CategoryGroupResponse>> callback) {
        apiService.getAllIncomeCategoryGroups().enqueue(new Callback<List<CategoryGroupResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<CategoryGroupResponse>> call, @NonNull Response<List<CategoryGroupResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CategoryGroupResponse>> call, @NonNull Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}
