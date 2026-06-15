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
import com.google.android.material.snackbar.Snackbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.model.Category;
import com.example.moneyapp.model.CategoryType;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.viewmodel.CategoryViewModel;

import java.util.ArrayList;

public class CategoryFragment extends BaseFragment {

    private RecyclerView rvCategories;
    private CategoryAdapter categoryAdapter;
    private CategoryViewModel viewModel;
    private View layoutEditControls;
    private ItemTouchHelper itemTouchHelper;
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
            if ("Khác".equals(category.getCategoryName())) {
                Snackbar.make(requireView(), "Đây là một hạng mục dịch vụ và không thể được chỉnh sửa", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Đóng", v -> {})
                        .show();
                return;
            }

            Bundle bundle = new Bundle();
            bundle.putString("categoryId", category.getCategoryId());
            bundle.putString("categoryName", category.getCategoryName());
            bundle.putDouble("monthlyTarget", category.getMonthlyTarget() != null ? category.getMonthlyTarget() : 0.0);
            bundle.putInt("colorId", category.getColor());
            bundle.putInt("iconId", category.getIcon());
            bundle.putString("groupId", category.getGroupId());
            bundle.putInt("type", category.getType() == CategoryType.EXPENSE ? 0 : 1);
            
            Navigation.findNavController(requireView()).navigate(R.id.action_categoryFragment_to_addCategoryFragment, bundle);
        });

        categoryAdapter.setOnCategoryLongClickListener((category, anchorView) -> {
            if (!categoryAdapter.isEditMode()) {
                showContextMenu(category, anchorView);
            }
        });

        rvCategories.setAdapter(categoryAdapter);
        layoutEditControls = view.findViewById(R.id.layout_edit_mode_controls);
        
        view.findViewById(R.id.btn_done_reorder).setOnClickListener(v -> exitEditMode(true));
        view.findViewById(R.id.btn_cancel_reorder).setOnClickListener(v -> exitEditMode(false));

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

        // Nút điều chỉnh hạng mục thường dùng
        view.findViewById(R.id.btn_adjust_frequent).setOnClickListener(v -> {
            enterEditMode();
        });

        // Load dữ liệu ban đầu từ trạng thái đã lưu
        viewModel.loadCategories(viewModel.getCurrentType());
    }

    private void enterEditMode() {
        categoryAdapter.setEditMode(true);
        layoutEditControls.setVisibility(View.VISIBLE);
        setupDragAndDrop();
        Toast.makeText(getContext(), "Chế độ sắp xếp: Kéo thả hạng mục", Toast.LENGTH_SHORT).show();
    }

    private void exitEditMode(boolean saveChanges) {
        categoryAdapter.setEditMode(false);
        layoutEditControls.setVisibility(View.GONE);
        if (itemTouchHelper != null) {
            itemTouchHelper.attachToRecyclerView(null);
            itemTouchHelper = null;
        }

        if (saveChanges) {
            saveNewOrders();
        } else {
            categoryAdapter.restoreBackup();
        }
    }

    private void setupDragAndDrop() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPos = viewHolder.getBindingAdapterPosition();
                int toPos = target.getBindingAdapterPosition();
                categoryAdapter.onItemMove(fromPos, toPos);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Not supported
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                // We save only when "Done" is clicked now, not on every drop
            }
        };

        itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rvCategories);
    }

    private void saveNewOrders() {
        java.util.List<Category> categories = categoryAdapter.getCategories();
        for (int i = 0; i < categories.size(); i++) {
            Category cat = categories.get(i);
            viewModel.reorderCategory(cat, i);
        }
    }

    private void showContextMenu(com.example.moneyapp.model.Category category, View anchorView) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), anchorView);
        
        // Không cho phép xóa hạng mục "Khác" mặc định
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
}
