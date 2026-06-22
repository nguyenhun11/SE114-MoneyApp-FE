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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import com.example.moneyapp.R;
import com.example.moneyapp.model.Category;
import com.example.moneyapp.model.CategoryType;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.viewmodel.CategoryViewModel;

import java.util.ArrayList;

public class CategoryFragment extends BaseFragment {

    private RecyclerView rvCategories;
    private CategoryGroupAdapter groupAdapter; // Dùng để XEM (Phân nhóm)
    private CategoryAdapter flatAdapter;       // Dùng để SẮP XẾP (Kéo thả)
    private CategoryViewModel viewModel;
    private View layoutEditControls;
    private ItemTouchHelper itemTouchHelper;

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
        layoutEditControls = view.findViewById(R.id.layout_edit_mode_controls);

        // 1. ĐỊNH NGHĨA SỰ KIỆN CLICK CHUNG CHO CẢ 2 ADAPTER
        CategoryAdapter.OnCategoryClickListener clickListener = category -> {
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
        };

        CategoryAdapter.OnCategoryLongClickListener longClickListener = (category, anchorView) -> {
            if (!flatAdapter.isEditMode()) {
                showContextMenu(category, anchorView);
            }
        };

        // 2. KHỞI TẠO CẢ 2 ADAPTER
        groupAdapter = new CategoryGroupAdapter(clickListener);
        groupAdapter.setOnCategoryLongClickListener(longClickListener);

        flatAdapter = new CategoryAdapter(new ArrayList<>(), clickListener);
        flatAdapter.setOnCategoryLongClickListener(longClickListener);

        // 3. SET TRẠNG THÁI MẶC ĐỊNH LÀ XEM (GROUP)
        rvCategories.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvCategories.setAdapter(groupAdapter);

        // 4. CÁC NÚT ĐIỀU KHIỂN
        view.findViewById(R.id.btn_done_reorder).setOnClickListener(v -> exitEditMode(true));
        view.findViewById(R.id.btn_cancel_reorder).setOnClickListener(v -> exitEditMode(false));
        view.findViewById(R.id.btn_adjust_frequent).setOnClickListener(v -> enterEditMode());

        setupHeader(view, "Danh mục", false);

        String[] categoryTabs = {
                "Chi tiêu",
                "Thu nhập",
        };
        setupHeaderTabs(view, categoryTabs,0, index -> {
            CategoryType tabType = (index == 0) ? CategoryType.EXPENSE : CategoryType.INCOME;
            viewModel.setCurrentType(tabType);
            viewModel.loadCategories(tabType);
        });

        // 5. LẮNG NGHE DỮ LIỆU
        viewModel.getCategoriesLiveData().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null && !flatAdapter.isEditMode()) {
                groupAdapter.setData(categories);
            }
        });

        viewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
        });

        viewModel.loadCategories(viewModel.getCurrentType());
    }

    private void enterEditMode() {
        // 1. Lấy dữ liệu dạng phẳng từ GroupAdapter chuyển sang FlatAdapter
        java.util.List<Category> allCategories = groupAdapter.getAllCategoriesFlattened();
        flatAdapter.updateData(allCategories);
        flatAdapter.setEditMode(true);

        // 2. Đổi LayoutManager sang Grid và gắn FlatAdapter
        rvCategories.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rvCategories.setAdapter(flatAdapter);

        // 3. Hiển thị UI và bật Kéo thả
        layoutEditControls.setVisibility(View.VISIBLE);
        setupDragAndDrop();
        Toast.makeText(getContext(), "Chế độ sắp xếp: Kéo thả hạng mục", Toast.LENGTH_SHORT).show();
    }

    private void exitEditMode(boolean saveChanges) {
        flatAdapter.setEditMode(false);
        layoutEditControls.setVisibility(View.GONE);

        // Tắt kéo thả
        if (itemTouchHelper != null) {
            itemTouchHelper.attachToRecyclerView(null);
            itemTouchHelper = null;
        }

        if (saveChanges) {
            saveNewOrders();
            // Cập nhật lại UI nhóm ngay lập tức với thứ tự mới
            groupAdapter.setData(flatAdapter.getCategories());
        } else {
            flatAdapter.restoreBackup();
        }

        // Đổi LayoutManager về lại Linear và gắn GroupAdapter
        rvCategories.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvCategories.setAdapter(groupAdapter);
    }

    // --- LOGIC KÉO THẢ (Giữ nguyên của Dev) ---

    private void setupDragAndDrop() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPos = viewHolder.getBindingAdapterPosition();
                int toPos = target.getBindingAdapterPosition();
                flatAdapter.onItemMove(fromPos, toPos); // Gọi vào FlatAdapter
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {}

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
            }
        };

        itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rvCategories);
    }

    private void saveNewOrders() {
        java.util.List<Category> categories = flatAdapter.getCategories(); // Lấy từ FlatAdapter
        for (int i = 0; i < categories.size(); i++) {
            Category cat = categories.get(i);
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