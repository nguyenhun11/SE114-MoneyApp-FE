package com.example.moneyapp.view.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.model.CategoryType;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.viewmodel.CategoryViewModel;

import java.util.ArrayList;

public class CategoryFragment extends BaseFragment {

    private RecyclerView rvCategories;
    private CategoryAdapter categoryAdapter;
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
        
        rvCategories = view.findViewById(R.id.rv_category_groups);
        rvCategories.setLayoutManager(new GridLayoutManager(getContext(), 3));
        
        categoryAdapter = new CategoryAdapter(new ArrayList<>(), category -> {
            String message = getString(R.string.menu_item_default) + ": " + category.getCategoryName();
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        });

        categoryAdapter.setOnCategoryLongClickListener((category, anchorView) -> {
            showContextMenu(category, anchorView);
        });

        rvCategories.setAdapter(categoryAdapter);

        setupHeader(view, R.string.category_list_title, false);
        
        // Khởi tạo Tab dựa trên giá trị lưu trong ViewModel
        setupIncomeExpenseTabs(view, viewModel.getCurrentType() == CategoryType.EXPENSE, isExpense -> {
            CategoryType newType = isExpense ? CategoryType.EXPENSE : CategoryType.INCOME;
            viewModel.setCurrentType(newType);
            viewModel.loadCategories(newType);
        });

        // Observe LiveData from ViewModel
        viewModel.getCategoriesLiveData().observe(getViewLifecycleOwner(), categories -> {
            categoryAdapter.updateData(categories);
        });

        viewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });

        // Load dữ liệu ban đầu từ trạng thái đã lưu
        viewModel.loadCategories(viewModel.getCurrentType());
    }

    private void showContextMenu(com.example.moneyapp.model.Category category, View anchorView) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), anchorView);
        popupMenu.getMenu().add(0, 1, 0, "Xóa");
        
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == 1) {
                // Mặc định dùng soft_delete như kế hoạch
                viewModel.deleteCategory(category.getCategoryId(), "soft_delete", null);
                return true;
            }
            return false;
        });
        
        popupMenu.show();
    }

    @Override
    protected void onFabClick() {
        Bundle bundle = new Bundle();
        bundle.putInt("type", viewModel.getCurrentType() == CategoryType.EXPENSE ? 0 : 1);
        Navigation.findNavController(requireView()).navigate(R.id.action_categoryFragment_to_addCategoryFragment, bundle);
    }
}
