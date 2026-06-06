package com.example.moneyapp.view.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.model.CategoryType;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.viewmodel.CategoryViewModel;

public class CategoryFragment extends BaseFragment {

    private RecyclerView rvCategoryGroups;
    private CategoryGroupAdapter groupAdapter;
    private CategoryViewModel viewModel;
    private boolean isExpenseTab = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Sử dụng Activity ViewModel để giữ trạng thái khi Fragment bị destroy
        viewModel = new ViewModelProvider(requireActivity()).get(CategoryViewModel.class);
        
        rvCategoryGroups = view.findViewById(R.id.rv_category_groups);
        rvCategoryGroups.setLayoutManager(new LinearLayoutManager(getContext()));
        
        groupAdapter = new CategoryGroupAdapter(category -> {
            String message = getString(R.string.menu_item_default) + ": " + category.getCategoryName();
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        });
        rvCategoryGroups.setAdapter(groupAdapter);

        setupHeader(view, R.string.category_list_title, false);
        
        // Khởi tạo Tab dựa trên giá trị lưu trong ViewModel
        setupIncomeExpenseTabs(view, viewModel.getCurrentType() == CategoryType.EXPENSE, isExpense -> {
            CategoryType newType = isExpense ? CategoryType.EXPENSE : CategoryType.INCOME;
            viewModel.setCurrentType(newType);
            viewModel.loadCategories(newType);
        });

        // Observe LiveData from ViewModel
        viewModel.getCategoriesLiveData().observe(getViewLifecycleOwner(), categories -> {
            groupAdapter.setData(categories);
        });

        // Load dữ liệu ban đầu từ trạng thái đã lưu
        viewModel.loadCategories(viewModel.getCurrentType());
    }

    @Override
    protected void onFabClick() {
        Bundle bundle = new Bundle();
        bundle.putInt("type", viewModel.getCurrentType().ordinal());
        Navigation.findNavController(requireView()).navigate(R.id.action_categoryFragment_to_addCategoryFragment, bundle);
    }
}
