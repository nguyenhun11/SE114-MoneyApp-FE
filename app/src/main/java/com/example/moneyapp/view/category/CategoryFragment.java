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
import androidx.recyclerview.widget.LinearLayoutManager; // Cập nhật import này
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.model.CategoryType;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.viewmodel.CategoryViewModel;

public class CategoryFragment extends BaseFragment {

    private RecyclerView rvCategories;
    private CategoryGroupAdapter groupAdapter; // Đổi sang Group Adapter
    private CategoryViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(CategoryViewModel.class);

        rvCategories = view.findViewById(R.id.rv_category_groups);
        rvCategories.setLayoutManager(new LinearLayoutManager(requireContext()));

        groupAdapter = new CategoryGroupAdapter(category -> {
            String message = getString(R.string.menu_item_default) + ": " + category.getCategoryName();
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        });
        rvCategories.setAdapter(groupAdapter);

        setupHeader(view, R.string.category_list_title, true);

        setupIncomeExpenseTabs(view, viewModel.getCurrentType() == CategoryType.EXPENSE, isExpense -> {
            CategoryType newType = isExpense ? CategoryType.EXPENSE : CategoryType.INCOME;
            viewModel.setCurrentType(newType);
            viewModel.loadCategories(newType);
        });

        viewModel.getCategoriesLiveData().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                groupAdapter.setData(categories); // Gọi setData() để chia nhóm
            }
        });

        viewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.loadCategories(viewModel.getCurrentType());
    }

    @Override
    protected void onFabClick() {
        Bundle bundle = new Bundle();
        bundle.putInt("type", viewModel.getCurrentType() == CategoryType.EXPENSE ? 0 : 1);
        Navigation.findNavController(requireView()).navigate(R.id.action_categoryFragment_to_addCategoryFragment, bundle);
    }

    @Override
    protected boolean shouldShowBottomNavigation() {
        return false;
    }
}