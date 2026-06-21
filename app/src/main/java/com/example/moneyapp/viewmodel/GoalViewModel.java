package com.example.moneyapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moneyapp.data.remote.request.GoalRequest;
import com.example.moneyapp.data.remote.request.TransactionRequest;
import com.example.moneyapp.data.remote.response.CategoryGroupResponse;
import com.example.moneyapp.data.repository.CategoryRepository;
import com.example.moneyapp.data.repository.GoalRepository;
import com.example.moneyapp.data.repository.TransactionRepository;
import com.example.moneyapp.model.Category;
import com.example.moneyapp.model.CategoryType;
import com.example.moneyapp.model.Goal;
import com.example.moneyapp.model.Transaction;

import java.util.Date;
import java.util.List;

public class GoalViewModel extends AndroidViewModel {
    private final GoalRepository goalRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    private final MutableLiveData<List<Goal>> goals = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isOperationSuccess = new MutableLiveData<>();

    public GoalViewModel(@NonNull Application application) {
        super(application);
        goalRepository = new GoalRepository(application);
        transactionRepository = new TransactionRepository(application);
        categoryRepository = new CategoryRepository(application);
    }

    public LiveData<List<Goal>> getGoals() { return goals; }
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

    public void depositToGoal(Goal goal, double amount, String accountId) {
        isLoading.setValue(true);
        
        // 1. Tìm hoặc tạo Hạng mục "Tiết kiệm"
        categoryRepository.getExpenseCategories(new CategoryRepository.CategoryCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> categories) {
                Category foundCategory = null;
                for (Category c : categories) {
                    if ("Tiết kiệm".equalsIgnoreCase(c.getCategoryName())) {
                        foundCategory = c;
                        break;
                    }
                }
                
                if (foundCategory != null) {
                    Category finalFoundCategory = foundCategory;
                    if (foundCategory.getIcon() != 17) {
                        // Tự động sửa lại icon cho đúng
                        foundCategory.setIcon(17);
                        categoryRepository.updateCategory(foundCategory, new CategoryRepository.CategoryCallback<Void>() {
                            @Override
                            public void onSuccess(Void result) {
                                performDeposit(goal, amount, accountId, finalFoundCategory.getCategoryId());
                            }

                            @Override
                            public void onError(String message) {
                                // Vẫn cho phép nạp tiền dù lỗi cập nhật icon
                                performDeposit(goal, amount, accountId, finalFoundCategory.getCategoryId());
                            }
                        });
                    } else {
                        performDeposit(goal, amount, accountId, foundCategory.getCategoryId());
                    }
                } else {
                    // Nếu chưa có, tạo mới hạng mục "Tiết kiệm" trong nhóm đầu tiên tìm thấy
                    categoryRepository.getAllExpenseCategoryGroups(new CategoryRepository.CategoryCallback<List<CategoryGroupResponse>>() {
                        @Override
                        public void onSuccess(List<CategoryGroupResponse> groups) {
                            String groupId = (groups != null && !groups.isEmpty()) ? groups.get(0).getId() : null;
                            
                            Category newCat = new Category(null, "Tiết kiệm", CategoryType.EXPENSE, groupId, null, 0.0, 0, 17, 0, null, null);
                            categoryRepository.createCategory(newCat, new CategoryRepository.CategoryCallback<Void>() {
                                @Override
                                public void onSuccess(Void result) {
                                    // Gọi lại để lấy ID mới tạo
                                    categoryRepository.getExpenseCategories(new CategoryRepository.CategoryCallback<List<Category>>() {
                                        @Override
                                        public void onSuccess(List<Category> updatedCats) {
                                            for (Category c : updatedCats) {
                                                if ("Tiết kiệm".equalsIgnoreCase(c.getCategoryName())) {
                                                    performDeposit(goal, amount, accountId, c.getCategoryId());
                                                    return;
                                                }
                                            }
                                            error.setValue("Không tìm thấy hạng mục Tiết kiệm sau khi tạo");
                                            isLoading.setValue(false);
                                        }

                                        @Override
                                        public void onError(String message) {
                                            error.setValue(message);
                                            isLoading.setValue(false);
                                        }
                                    });
                                }

                                @Override
                                public void onError(String message) {
                                    error.setValue(message);
                                    isLoading.setValue(false);
                                }
                            });
                        }

                        @Override
                        public void onError(String message) {
                            error.setValue("Không lấy được nhóm hạng mục: " + message);
                            isLoading.setValue(false);
                        }
                    });
                }
            }

            @Override
            public void onError(String message) {
                error.setValue(message);
                isLoading.setValue(false);
            }
        });
    }

    private void performDeposit(Goal goal, double amount, String accountId, String categoryId) {
        // 2. Cập nhật Goal Balance
        goalRepository.depositToGoal(goal.getId(), amount, new GoalRepository.GoalCallback<Goal>() {
            @Override
            public void onSuccess(Goal updatedGoal) {
                // 3. Tạo Transaction chi tiêu
                Transaction transaction = new Transaction(
                        null, accountId, null, categoryId, null,
                        CategoryType.EXPENSE, amount, new Date(),
                        "Nạp tiền mục tiêu: " + goal.getName(),
                        0, 0, 0, 0, null, null
                );
                
                transactionRepository.createTransaction(transaction, new TransactionRepository.TransactionCallback<Transaction>() {
                    @Override
                    public void onSuccess(Transaction result) {
                        isOperationSuccess.setValue(true);
                        isLoading.setValue(false);
                        fetchGoals();
                    }

                    @Override
                    public void onError(String message) {
                        // Vẫn đánh dấu thành công vì Goal đã được cập nhật, 
                        // nhưng báo lỗi cho phần Transaction
                        error.setValue("Đã nạp tiền nhưng lỗi tạo giao dịch: " + message);
                        isOperationSuccess.setValue(true); 
                        isLoading.setValue(false);
                        fetchGoals();
                    }
                });
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
