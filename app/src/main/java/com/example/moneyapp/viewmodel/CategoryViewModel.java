package com.example.moneyapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moneyapp.data.local.entity.Category;
import com.example.moneyapp.data.repository.CategoryRepository;

import java.util.List;

public class CategoryViewModel extends AndroidViewModel {
    private final CategoryRepository repository;
    private final MutableLiveData<List<Category>> categoriesLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> saveSuccess = new MutableLiveData<>();
    private int currentType = 2; // Mặc định là Expense (2)

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        repository = new CategoryRepository(application);
    }

    public int getCurrentType() {
        return currentType;
    }

    public void setCurrentType(int type) {
        this.currentType = type;
    }

    public LiveData<List<Category>> getCategoriesLiveData() {
        return categoriesLiveData;
    }

    public LiveData<Boolean> getSaveSuccess() {
        return saveSuccess;
    }

    public void loadCategories(int type) {
        repository.getAllCategoriesByType(type, categoriesLiveData::postValue);
    }

    public void addCategory(Category category) {
        repository.insertCategory(category, () -> saveSuccess.postValue(true));
    }

    public void updateCategory(Category category) {
        repository.updateCategory(category, null);
    }
}
