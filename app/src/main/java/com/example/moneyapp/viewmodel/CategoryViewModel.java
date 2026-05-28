package com.example.moneyapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moneyapp.model.Category;
import com.example.moneyapp.data.repository.CategoryRepository;
import com.example.moneyapp.model.CategoryType;

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
        CategoryRepository.CategoryCallback<List<Category>> callback = new CategoryRepository.CategoryCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> result) {
                categoriesLiveData.postValue(result);
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

    public void addCategory(Category category) {
        repository.insertCategory(category, new CategoryRepository.CategoryCallback<Void>() {
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
}
