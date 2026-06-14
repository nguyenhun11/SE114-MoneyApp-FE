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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.model.Category;
import com.example.moneyapp.model.CategoryType;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.viewmodel.CategoryViewModel;

public class CategoryFragment extends BaseFragment {

    private RecyclerView rvCategories;
    private CategoryGroupAdapter groupAdapter; // Chỉ dùng GroupAdapter
    private CategoryViewModel viewModel;
    private View layoutEditControls;

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

        // Khởi tạo Adapter
        groupAdapter = new CategoryGroupAdapter(category -> {
            String message = "Hạng mục: " + category.getCategoryName();
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        });

        // Gắn listener cho sự kiện nhấn giữ
        groupAdapter.setOnCategoryLongClickListener((category, anchorView) -> {
            if (!groupAdapter.isEditMode()) {
                showContextMenu(category, anchorView);
            }
        });

        rvCategories.setAdapter(groupAdapter);
        layoutEditControls = view.findViewById(R.id.layout_edit_mode_controls);

        view.findViewById(R.id.btn_done_reorder).setOnClickListener(v -> exitEditMode(true));
        view.findViewById(R.id.btn_cancel_reorder).setOnClickListener(v -> exitEditMode(false));

        setupHeader(view, "Danh mục", true);

        setupIncomeExpenseTabs(view, viewModel.getCurrentType() == CategoryType.EXPENSE, isExpense -> {
            CategoryType newType = isExpense ? CategoryType.EXPENSE : CategoryType.INCOME;
            viewModel.setCurrentType(newType);
            viewModel.loadCategories(newType);
        });

        viewModel.getCategoriesLiveData().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                groupAdapter.setData(categories);
            }
        });

        viewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
        });

        view.findViewById(R.id.btn_adjust_frequent).setOnClickListener(v -> enterEditMode());

        viewModel.loadCategories(viewModel.getCurrentType());
    }

    private void enterEditMode() {
        groupAdapter.setEditMode(true);
        layoutEditControls.setVisibility(View.VISIBLE);
        Toast.makeText(getContext(), "Chế độ sắp xếp: Đang bật", Toast.LENGTH_SHORT).show();
    }

    private void exitEditMode(boolean saveChanges) {
        groupAdapter.setEditMode(false);
        layoutEditControls.setVisibility(View.GONE);

        if (saveChanges) {
            saveNewOrders();
        } else {
            groupAdapter.restoreBackup();
        }
    }

    private void saveNewOrders() {
        // Lưu ý: Với CategoryGroupAdapter, bạn cần duyệt qua danh sách các nhóm
        // để lấy danh sách hạng mục phẳng sau khi đã kéo thả
        java.util.List<Category> allCategories = groupAdapter.getAllCategoriesFlattened();
        for (int i = 0; i < allCategories.size(); i++) {
            Category cat = allCategories.get(i);
            viewModel.reorderCategory(cat, i);
        }
    }

    private void showContextMenu(Category category, View anchorView) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), anchorView);
        if (!"Khác".equals(category.getCategoryName())) {
            popupMenu.getMenu().add(0, 1, 0, "Xóa");
        }
        popupMenu.getMenu().add(0, 2, 0, "Sắp xếp");

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == 1) {
                viewModel.deleteCategory(category.getCategoryId(), "soft_delete", null);
                return true;
            } else if (item.getItemId() == 2) {
                enterEditMode();
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

    @Override
    protected boolean shouldShowBottomNavigation() { return false; }
}