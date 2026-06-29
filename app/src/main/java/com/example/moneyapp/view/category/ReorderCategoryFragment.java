package com.example.moneyapp.view.category;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.data.remote.response.CategoryGroupResponse;
import com.example.moneyapp.model.Category;
import com.example.moneyapp.model.CategoryType;
import com.example.moneyapp.utils.DialogHelper;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.viewmodel.CategoryViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class ReorderCategoryFragment extends BaseFragment {

    private RecyclerView rvReorder;
    private ReorderCategoryAdapter adapter;
    private CategoryViewModel viewModel;

    private List<CategoryGroupResponse> currentGroups = new ArrayList<>();
    private List<Category> currentCategories = new ArrayList<>();
    private boolean isDragChanged = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reorder_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(CategoryViewModel.class);

        setupHeader(view, "Sắp xếp hạng mục",
                "gmd_arrow_back", v -> requireActivity().onBackPressed(),
                "gmd_check", v -> onSaveClicked());

        rvReorder = view.findViewById(R.id.rv_reorder_categories);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adapter != null && adapter.getItemViewType(position) == ReorderCategoryAdapter.ListItem.TYPE_HEADER) {
                    return 3;
                }
                return 1;
            }
        });
        rvReorder.setLayoutManager(gridLayoutManager);

        CategoryType type = viewModel.getCurrentType() != null ? viewModel.getCurrentType() : CategoryType.EXPENSE;
        viewModel.loadCategories(type);

        viewModel.getGroupsLiveData().observe(getViewLifecycleOwner(), groups -> {
            if (groups != null) {
                currentGroups = groups;
                buildFlattenedList();
            }
        });

        viewModel.getCategoriesLiveData().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                currentCategories = categories;
                buildFlattenedList();
            }
        });

        viewModel.getSaveSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                viewModel.loadCategories(viewModel.getCurrentType());
                viewModel.resetSaveSuccess();
            }
        });
    }

    private void buildFlattenedList() {
        if (currentGroups.isEmpty()) return;

        List<ReorderCategoryAdapter.ListItem> flattenedList = new ArrayList<>();

        for (CategoryGroupResponse group : currentGroups) {
            String groupName = (group.getGroupName() != null && !group.getGroupName().isEmpty())
                    ? group.getGroupName() : "Nhóm chưa đặt tên";

            boolean isEmpty = true;
            for (Category cat : currentCategories) {
                if (cat.getGroupId() != null && cat.getGroupId().equals(group.getId())) {
                    isEmpty = false;
                    break;
                }
            }

            flattenedList.add(new ReorderCategoryAdapter.HeaderItem(group.getId(), groupName, isEmpty));

            for (Category cat : currentCategories) {
                if (cat.getGroupId() != null && cat.getGroupId().equals(group.getId())) {
                    flattenedList.add(new ReorderCategoryAdapter.CategoryItem(cat));
                }
            }
        }

        if (adapter == null) {
            adapter = new ReorderCategoryAdapter(flattenedList, this::showDeleteGroupDialog);
            rvReorder.setAdapter(adapter);
            setupDragAndDrop();
        } else {
            if (!isDragChanged) {
                adapter.setItems(flattenedList);
            }
        }
    }

    private void setupDragAndDrop() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, 0) {

            @Override
            public int getDragDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return super.getDragDirs(recyclerView, viewHolder);
            }

            @Override
            public boolean canDropOver(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder current, @NonNull RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPos = viewHolder.getBindingAdapterPosition();
                int toPos = target.getBindingAdapterPosition();
                adapter.onItemMove(fromPos, toPos);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) { }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                isDragChanged = true;
                updateLocalStateFromAdapter();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rvReorder);
    }

    private void updateLocalStateFromAdapter() {
        List<ReorderCategoryAdapter.ListItem> items = adapter.getItems();
        String currentHeaderId = null;

        for (ReorderCategoryAdapter.ListItem item : items) {
            if (item.getType() == ReorderCategoryAdapter.ListItem.TYPE_HEADER) {
                ReorderCategoryAdapter.HeaderItem header = (ReorderCategoryAdapter.HeaderItem) item;
                currentHeaderId = header.groupId;
                header.isEmpty = true; // Mặc định cho là trống
            } else if (item.getType() == ReorderCategoryAdapter.ListItem.TYPE_ITEM) {
                for (ReorderCategoryAdapter.ListItem h : items) {
                    if (h.getType() == ReorderCategoryAdapter.ListItem.TYPE_HEADER &&
                            ((ReorderCategoryAdapter.HeaderItem) h).groupId.equals(currentHeaderId)) {
                        ((ReorderCategoryAdapter.HeaderItem) h).isEmpty = false;
                        break;
                    }
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
    private void onSaveClicked() {
        if (!isDragChanged) {
            requireActivity().onBackPressed();
            return;
        }

        Toast.makeText(getContext(), "Đang lưu thay đổi...", Toast.LENGTH_SHORT).show();
        saveNewOrdersAndGroups();

        isDragChanged = false;
        requireActivity().onBackPressed();
    }

    private void saveNewOrdersAndGroups() {
        List<ReorderCategoryAdapter.ListItem> items = adapter.getItems();
        String currentGroupId = null;
        String currentGroupName = null;
        int categoryOrderIndex = 0;
        int groupOrderIndex = 0;

        for (ReorderCategoryAdapter.ListItem item : items) {
            if (item.getType() == ReorderCategoryAdapter.ListItem.TYPE_HEADER) {
                ReorderCategoryAdapter.HeaderItem header = (ReorderCategoryAdapter.HeaderItem) item;
                currentGroupId = header.groupId;
                currentGroupName = header.groupName;
                categoryOrderIndex = 0;

                viewModel.reorderCategoryGroup(currentGroupId, groupOrderIndex);
                groupOrderIndex++;

            } else if (item.getType() == ReorderCategoryAdapter.ListItem.TYPE_ITEM) {
                Category cat = ((ReorderCategoryAdapter.CategoryItem) item).category;

                if (cat.getGroupId() == null || !cat.getGroupId().equals(currentGroupId)) {
                    cat.setGroupId(currentGroupId);
                    cat.setGroupName(currentGroupName);

                    viewModel.updateCategory(cat);
                }

                viewModel.reorderCategory(cat, categoryOrderIndex);
                categoryOrderIndex++;
            }
        }
    }

    private void showDeleteGroupDialog(String groupId, String groupName) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Xóa nhóm trống")
                .setMessage("Bạn có chắc muốn xóa nhóm '" + groupName + "' không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    viewModel.deleteCategoryGroup(groupId);
                    Toast.makeText(getContext(), "Đang xóa nhóm...", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showAddGroupDialog() {
        EditText input = new EditText(requireContext());
        input.setHint("Ví dụ: Đầu tư, Đóng tiền học...");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        FrameLayout container = new FrameLayout(requireContext());
        FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(64, 32, 64, 0);
        input.setLayoutParams(params);
        container.addView(input);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Thêm nhóm mới")
                .setView(container)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String groupName = input.getText().toString().trim();
                    if (!groupName.isEmpty()) {
                        CategoryType type = viewModel.getCurrentType() != null ? viewModel.getCurrentType() : CategoryType.EXPENSE;
                        viewModel.createCategoryGroup(type, groupName);
                        Toast.makeText(getContext(), "Đang tạo nhóm...", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override protected String getFabIcon() { return "gmd_add"; }
    @Override protected String getFabLabel() { return "Thêm nhóm mới"; }
    @Override protected void onFabClick() { showAddGroupDialog(); }
    @Override protected boolean shouldShowBottomNavigation() { return false; }
}
