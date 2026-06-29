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

    // LiveData quan sát danh sách
    private final MutableLiveData<List<Category>> categoriesLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<CategoryGroupResponse>> groupsLiveData = new MutableLiveData<>();

    // LiveData quan sát trạng thái / chi tiết
    private final MutableLiveData<Category> selectedCategoryLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> saveSuccess = new MutableLiveData<>();

    // 💥 THÊM BIẾN QUẢN LÝ TRẠNG THÁI LOADING
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    private CategoryType currentType = CategoryType.EXPENSE;

    private final java.util.Set<CategoryType> initializingTypes = java.util.Collections.synchronizedSet(new java.util.HashSet<>());

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        repository = new CategoryRepository(application);
    }

    // region Getters & Setters cho LiveData
    public CategoryType getCurrentType() { return currentType; }
    public void setCurrentType(CategoryType type) { this.currentType = type; }

    public LiveData<List<Category>> getCategoriesLiveData() { return categoriesLiveData; }
    public LiveData<List<CategoryGroupResponse>> getGroupsLiveData() { return groupsLiveData; }
    public LiveData<Category> getSelectedCategoryLiveData() { return selectedCategoryLiveData; }
    public LiveData<String> getErrorLiveData() { return errorLiveData; }
    public LiveData<Boolean> getSaveSuccess() { return saveSuccess; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    /**
     * Đặt lại trạng thái saveSuccess sau khi View đã tiêu thụ Event
     */
    public void resetSaveSuccess() { saveSuccess.setValue(false); }
    // endregion

    // region Tải Dữ Liệu Khởi Tạo (Fetch & Default Setup)
    public void loadCategories(CategoryType type) {
        this.currentType = type;
        isLoading.setValue(true);
        categoriesLiveData.setValue(new ArrayList<>());

        CategoryRepository.CategoryCallback<List<CategoryGroupResponse>> groupCallback = new CategoryRepository.CategoryCallback<List<CategoryGroupResponse>>() {
            @Override
            public void onSuccess(List<CategoryGroupResponse> result) {
                groupsLiveData.postValue(result != null ? result : new ArrayList<>());

                if (result == null || result.isEmpty()) {
                    createDefaultCategories(type);
                } else {
                    fetchCategories(type, result);
                }
            }

            @Override
            public void onError(String message) {
                errorLiveData.postValue(message);
                isLoading.postValue(false);
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
                    if (groups != null && !groups.isEmpty()) {
                        CategoryGroupResponse targetGroup = groups.get(0);
                        createDefaultCategoriesInGroup(type, targetGroup);
                    } else {
                        createDefaultCategories(type);
                    }
                } else {
                    categoriesLiveData.postValue(result);
                    isLoading.postValue(false);
                }
            }

            @Override
            public void onError(String message) {
                errorLiveData.postValue(message);
                isLoading.postValue(false);
            }
        };

        if (type == CategoryType.INCOME) {
            repository.getIncomeCategories(callback);
        } else {
            repository.getExpenseCategories(callback);
        }
    }

    private void createDefaultCategoriesInGroup(CategoryType type, CategoryGroupResponse group) {
        if (initializingTypes.contains(type)) return;
        initializingTypes.add(type);

        List<Category> defaults = new ArrayList<>();
        if (type == CategoryType.EXPENSE) {
            defaults.add(new Category(null, "Ăn uống", CategoryType.EXPENSE, group.getId(), group.getGroupName(), 4, 11, 0, new Date(), new Date()));
            defaults.add(new Category(null, "Di chuyển", CategoryType.EXPENSE, group.getId(), group.getGroupName(),  1, 12, 1, new Date(), new Date()));
            defaults.add(new Category(null, "Mua sắm", CategoryType.EXPENSE, group.getId(), group.getGroupName(), 9, 10, 2, new Date(), new Date()));
            defaults.add(new Category(null, "Khác", CategoryType.EXPENSE, group.getId(), group.getGroupName(), 5, 15, 3, new Date(), new Date()));
        } else if (type == CategoryType.INCOME) {
            defaults.add(new Category(null, "Lương", CategoryType.INCOME, group.getId(), group.getGroupName(),  2, 14, 0, new Date(), new Date()));
            defaults.add(new Category(null, "Thưởng", CategoryType.INCOME, group.getId(), group.getGroupName(),  4, 5, 1, new Date(), new Date()));
            defaults.add(new Category(null, "Khác", CategoryType.INCOME, group.getId(), group.getGroupName(),  5, 15, 2, new Date(), new Date()));
        }

        if (defaults.isEmpty()) {
            initializingTypes.remove(type);
            isLoading.postValue(false);
            return;
        }

        final int total = defaults.size();
        final int[] completed = {0};

        for (Category cat : defaults) {
            repository.createCategory(cat, new CategoryRepository.CategoryCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    checkCompletion();
                }

                @Override
                public void onError(String message) {
                    checkCompletion();
                }

                private void checkCompletion() {
                    synchronized (completed) {
                        completed[0]++;
                        if (completed[0] == total) {
                            initializingTypes.remove(type);
                            loadCategories(type);
                        }
                    }
                }
            });
        }
    }

    private void createDefaultCategories(CategoryType type) {
        CategoryGroupRequest groupRequest = new CategoryGroupRequest(type == CategoryType.EXPENSE ? "Chi tiêu" : "Thu nhập");

        CategoryRepository.CategoryCallback<CategoryGroupResponse> groupCallback = new CategoryRepository.CategoryCallback<CategoryGroupResponse>() {
            @Override
            public void onSuccess(CategoryGroupResponse group) {
                List<CategoryGroupResponse> currentList = groupsLiveData.getValue();
                if (currentList == null) currentList = new ArrayList<>();
                currentList.add(group);
                groupsLiveData.postValue(currentList);

                createDefaultCategoriesInGroup(type, group);
            }

            @Override
            public void onError(String message) {
                errorLiveData.postValue("Lỗi tạo nhóm: " + message);
                isLoading.postValue(false);
            }
        };

        if (type == CategoryType.EXPENSE) {
            repository.createExpenseCategoryGroup(groupRequest, groupCallback);
        } else {
            repository.createIncomeCategoryGroup(groupRequest, groupCallback);
        }
    }
    // endregion

    // region API HẠNG MỤC (Category) - CRUD & Reorder
    public void getCategoryById(String id) {
        repository.getCategoryById(id, new CategoryRepository.CategoryCallback<Category>() {
            @Override
            public void onSuccess(Category result) {
                selectedCategoryLiveData.postValue(result);
            }

            @Override
            public void onError(String message) {
                errorLiveData.postValue(message);
            }
        });
    }

    public void loadCategoriesByGroupId(String groupId) {
        isLoading.setValue(true);
        repository.getCategoriesByGroupId(groupId, new CategoryRepository.CategoryCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> result) {
                categoriesLiveData.postValue(result != null ? result : new ArrayList<>());
                isLoading.postValue(false);
            }

            @Override
            public void onError(String message) {
                errorLiveData.postValue(message);
                isLoading.postValue(false);
            }
        });
    }

    private interface OnGroupReadyAction {
        void execute(Category category);
    }

    public void addCategory(Category category) {
        if (category.getGroupId() == null) {
            createGroupThenProceed(category, this::performActualCreate);
        } else {
            performActualCreate(category);
        }
    }

    public void updateCategory(Category category) {
        if (category.getGroupId() == null) {
            createGroupThenProceed(category, this::performActualUpdate);
        } else {
            performActualUpdate(category);
        }
    }

    private void createGroupThenProceed(Category category, OnGroupReadyAction nextAction) {
        CategoryGroupRequest groupRequest = new CategoryGroupRequest(category.getGroupName());

        CategoryRepository.CategoryCallback<CategoryGroupResponse> groupCallback = new CategoryRepository.CategoryCallback<CategoryGroupResponse>() {
            @Override
            public void onSuccess(CategoryGroupResponse group) {
                category.setGroupId(group.getId());
                category.setGroupName(group.getGroupName());
                nextAction.execute(category);
            }

            @Override
            public void onError(String message) {
                errorLiveData.postValue("Lỗi khởi tạo nhóm mới: " + message);
            }
        };

        if (category.getType() == CategoryType.INCOME) {
            repository.createIncomeCategoryGroup(groupRequest, groupCallback);
        } else {
            repository.createExpenseCategoryGroup(groupRequest, groupCallback);
        }
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

    private void performActualUpdate(Category category) {
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
    // endregion

    // region BỔ SUNG: API NHÓM DANH MỤC (Category Group) - CRUD & Reorder

    /**
     * Tạo một Nhóm Danh Mục mới thủ công từ giao diện
     */
    public void createCategoryGroup(CategoryType type, String groupName) {
        CategoryGroupRequest request = new CategoryGroupRequest(groupName);
        CategoryRepository.CategoryCallback<CategoryGroupResponse> callback = new CategoryRepository.CategoryCallback<CategoryGroupResponse>() {
            @Override
            public void onSuccess(CategoryGroupResponse result) {
                saveSuccess.postValue(true);
            }

            @Override
            public void onError(String message) {
                errorLiveData.postValue("Tạo nhóm thất bại: " + message);
            }
        };

        if (type == CategoryType.INCOME) {
            repository.createIncomeCategoryGroup(request, callback);
        } else {
            repository.createExpenseCategoryGroup(request, callback);
        }
    }

    /**
     * Cập nhật thông tin (Tên nhóm) của Nhóm danh mục
     */
    public void updateCategoryGroup(String id, String newGroupName) {
        CategoryGroupRequest request = new CategoryGroupRequest(newGroupName);
        repository.updateCategoryGroup(id, request, new CategoryRepository.CategoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                saveSuccess.postValue(true);
                loadCategories(currentType);
            }

            @Override
            public void onError(String message) {
                errorLiveData.postValue("Sửa nhóm thất bại: " + message);
            }
        });
    }

    /**
     * Xóa một Nhóm danh mục theo ID
     */
    public void deleteCategoryGroup(String id) {
        repository.deleteCategoryGroup(id, new CategoryRepository.CategoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                saveSuccess.postValue(true);
                loadCategories(currentType);
            }

            @Override
            public void onError(String message) {
                errorLiveData.postValue("Xóa nhóm thất bại: " + message);
            }
        });
    }

    /**
     * Thay đổi thứ tự sắp xếp của Nhóm danh mục
     */
    public void reorderCategoryGroup(String id, int newOrder) {
        repository.reorderCategoryGroup(id, newOrder, new CategoryRepository.CategoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                saveSuccess.postValue(true);
                loadCategories(currentType);
            }

            @Override
            public void onError(String message) {
                errorLiveData.postValue("Đổi vị trí nhóm thất bại: " + message);
            }
        });
    }
    // endregion
}