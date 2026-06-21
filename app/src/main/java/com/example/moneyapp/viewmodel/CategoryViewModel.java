package com.example.moneyapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moneyapp.model.Category;
import com.example.moneyapp.data.repository.CategoryRepository;
import com.example.moneyapp.model.CategoryType;
import com.example.moneyapp.data.remote.request.CategoryGroupRequest;
import com.example.moneyapp.data.remote.response.CategoryGroupResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CategoryViewModel extends AndroidViewModel {
    private final CategoryRepository repository;
    private final MutableLiveData<List<Category>> categoriesLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> saveSuccess = new MutableLiveData<>();
    private CategoryType currentType = CategoryType.EXPENSE;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        repository = new CategoryRepository(application);
    }

    public CategoryType getCurrentType() {
        return currentType;
    }

    public void setCurrentType(CategoryType type) {
        this.currentType = type;
    }

    public LiveData<List<Category>> getCategoriesLiveData() {
        return categoriesLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public LiveData<Boolean> getSaveSuccess() {
        return saveSuccess;
    }

    public void loadCategories(CategoryType type) {
        this.currentType = type;
        
        // Luôn xóa dữ liệu cũ trước khi tải mới để tránh UI bị lag hoặc hiển thị sai
        categoriesLiveData.setValue(new ArrayList<>());

        CategoryRepository.CategoryCallback<List<CategoryGroupResponse>> groupCallback = new CategoryRepository.CategoryCallback<List<CategoryGroupResponse>>() {
            @Override
            public void onSuccess(List<CategoryGroupResponse> result) {
                if (result == null || result.isEmpty()) {
                    createDefaultCategories(type);
                } else {
                    // Nếu đã có nhóm, thử lấy hạng mục
                    fetchCategories(type, result);
                }
            }

            @Override
            public void onError(String message) {
                errorLiveData.postValue(message);
            }
        };

        if (type == CategoryType.INCOME) {
            repository.getAllIncomeCategoryGroups(groupCallback);
        } else {
            repository.getAllExpenseCategoryGroups(groupCallback);
        }
    }

    private void fetchCategories(CategoryType type, List<CategoryGroupResponse> groups) {
        CategoryRepository.CategoryCallback<List<Category>> callback = new CategoryRepository.CategoryCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> result) {
                if (result == null || result.isEmpty()) {
                    // Nếu đã có nhóm nhưng chưa có hạng mục, dùng nhóm đầu tiên để tạo hạng mục mẫu
                    if (groups != null && !groups.isEmpty()) {
                        CategoryGroupResponse targetGroup = groups.get(0);
                        createDefaultCategoriesInGroup(type, targetGroup);
                    } else {
                        createDefaultCategories(type);
                    }
                } else {
                    categoriesLiveData.postValue(result);
                }
            }

            @Override
            public void onError(String message) {
                errorLiveData.postValue(message);
            }
        };

        if (type == CategoryType.INCOME) {
            repository.getIncomeCategories(callback);
        } else {
            repository.getExpenseCategories(callback);
        }
    }

    private void createDefaultCategoriesInGroup(CategoryType type, CategoryGroupResponse group) {
        if (type == CategoryType.EXPENSE) {
            addCategory(new Category(null, "Ăn uống", CategoryType.EXPENSE, group.getId(), group.getGroupName(), 0.0, 4, 11, 0, new Date(), new Date()));
            addCategory(new Category(null, "Di chuyển", CategoryType.EXPENSE, group.getId(), group.getGroupName(), 0.0, 1, 12, 1, new Date(), new Date()));
            addCategory(new Category(null, "Mua sắm", CategoryType.EXPENSE, group.getId(), group.getGroupName(), 0.0, 9, 10, 2, new Date(), new Date()));
            addCategory(new Category(null, "Khác", CategoryType.EXPENSE, group.getId(), group.getGroupName(), 0.0, 5, 15, 3, new Date(), new Date()));
        } else if (type == CategoryType.INCOME) {
            addCategory(new Category(null, "Lương", CategoryType.INCOME, group.getId(), group.getGroupName(), 0.0, 2, 14, 0, new Date(), new Date()));
            addCategory(new Category(null, "Thưởng", CategoryType.INCOME, group.getId(), group.getGroupName(), 0.0, 4, 5, 1, new Date(), new Date()));
            addCategory(new Category(null, "Khác", CategoryType.INCOME, group.getId(), group.getGroupName(), 0.0, 5, 15, 2, new Date(), new Date()));
        }
    }

    private void createDefaultCategories(CategoryType type) {
        CategoryGroupRequest groupRequest = new CategoryGroupRequest(type == CategoryType.EXPENSE ? "Chi tiêu" : "Thu nhập");
        
        CategoryRepository.CategoryCallback<CategoryGroupResponse> groupCallback = new CategoryRepository.CategoryCallback<CategoryGroupResponse>() {
            @Override
            public void onSuccess(CategoryGroupResponse group) {
                createDefaultCategoriesInGroup(type, group);
            }

            @Override
            public void onError(String message) {
                errorLiveData.postValue("Lỗi tạo nhóm: " + message);
            }
        };

        if (type == CategoryType.EXPENSE) {
            repository.createExpenseCategoryGroup(groupRequest, groupCallback);
        } else {
            repository.createIncomeCategoryGroup(groupRequest, groupCallback);
        }
    }

    public void addCategory(Category category) {
        if (category.getGroupId() == null) {
            // Sử dụng tên nhóm mặc định duy nhất cho mỗi loại để tránh phân mảnh
            String defaultGroupName = (category.getType() == CategoryType.EXPENSE ? "Chi tiêu" : "Thu nhập");
            CategoryGroupRequest groupRequest = new CategoryGroupRequest(defaultGroupName);
            
            CategoryRepository.CategoryCallback<CategoryGroupResponse> groupCallback = new CategoryRepository.CategoryCallback<CategoryGroupResponse>() {
                @Override
                public void onSuccess(CategoryGroupResponse group) {
                    category.setGroupId(group.getId());
                    category.setGroupName(group.getGroupName());
                    
                    // Gọi repository để tạo hạng mục thật
                    performActualCreate(category);
                }

                @Override
                public void onError(String message) {
                    errorLiveData.postValue("Lỗi tạo nhóm: " + message);
                }
            };

            if (category.getType() == CategoryType.EXPENSE) {
                repository.createExpenseCategoryGroup(groupRequest, groupCallback);
            } else {
                repository.createIncomeCategoryGroup(groupRequest, groupCallback);
            }
            return;
        }

        performActualCreate(category);
    }

    private void performActualCreate(Category category) {
        repository.createCategory(category, new CategoryRepository.CategoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                saveSuccess.postValue(true);
                loadCategories(currentType);
            }

            @Override
            public void onError(String message) {
                errorLiveData.postValue(message);
            }
        });
    }

    public void updateCategory(Category category) {
        repository.updateCategory(category, new CategoryRepository.CategoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                saveSuccess.postValue(true);
            }

            @Override
            public void onError(String message) {
                errorLiveData.postValue(message);
            }
        });
    }

    public void deleteCategory(String id, String mode, String fallbackId) {
        repository.deleteCategory(id, mode, fallbackId, new CategoryRepository.CategoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                saveSuccess.postValue(true);
                loadCategories(currentType);
            }

            @Override
            public void onError(String message) {
                errorLiveData.postValue(message);
            }
        });
    }

    public void reorderCategory(Category category, int newOrder) {
        repository.reorderCategory(category, newOrder, new CategoryRepository.CategoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                saveSuccess.postValue(true);
            }

            @Override
            public void onError(String message) {
                errorLiveData.postValue(message);
            }
        });
    }
}
