package com.example.moneyapp.view.category;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.model.Category;
import com.example.moneyapp.view.category.CategoryAdapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CategoryGroupAdapter extends RecyclerView.Adapter<CategoryGroupAdapter.GroupViewHolder> {

    private Map<String, List<Category>> groupedCategories = new LinkedHashMap<>();
    private List<String> groupNames = new ArrayList<>();
    private CategoryAdapter.OnCategoryClickListener listener;
    private CategoryAdapter.OnCategoryLongClickListener longClickListener;
    private boolean isEditMode = false;

    public CategoryGroupAdapter(CategoryAdapter.OnCategoryClickListener listener) {
        this.listener = listener;
    }

    public void setOnCategoryLongClickListener(CategoryAdapter.OnCategoryLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    public void setEditMode(boolean isEditMode) {
        this.isEditMode = isEditMode;
        notifyDataSetChanged(); // Load lại toàn bộ để các adapter con nhận status mới
    }

    public boolean isEditMode() {
        return this.isEditMode;
    }

    public void setData(List<Category> categories) {
        groupedCategories.clear();
        groupNames.clear();
        if (categories == null) {
            notifyDataSetChanged();
            return;
        }
        for (Category category : categories) {
            String group = (category.getGroupName() == null || category.getGroupName().isEmpty()) ? "Khác" : category.getGroupName();
            if (!groupedCategories.containsKey(group)) {
                groupedCategories.put(group, new ArrayList<>());
                groupNames.add(group);
            }
            groupedCategories.get(group).add(category);
        }
        notifyDataSetChanged();
    }

    public List<Category> getAllCategoriesFlattened() {
        List<Category> all = new ArrayList<>();
        for (List<Category> list : groupedCategories.values()) all.addAll(list);
        return all;
    }

    public void restoreBackup() {
        // Gọi restore trên tất cả các child thông qua thông báo cập nhật
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        String groupName = groupNames.get(position);
        List<Category> items = groupedCategories.get(groupName);
        holder.bind(groupName, items, listener, longClickListener, isEditMode);
    }

    @Override
    public int getItemCount() {
        return groupNames.size();
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvGroupName;
        private final RecyclerView rvItems;
        private CategoryAdapter adapter; // Giữ tham chiếu ở đây

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGroupName = itemView.findViewById(R.id.tv_group_name);
            rvItems = itemView.findViewById(R.id.rv_items);
            rvItems.setLayoutManager(new GridLayoutManager(itemView.getContext(), 3));
        }

        public void bind(String groupName, List<Category> items,
                         CategoryAdapter.OnCategoryClickListener listener,
                         CategoryAdapter.OnCategoryLongClickListener longClickListener,
                         boolean isEditMode) {
            tvGroupName.setText(groupName);

            if (adapter == null) {
                adapter = new CategoryAdapter(items, listener);
                adapter.setOnCategoryLongClickListener(longClickListener);
                rvItems.setAdapter(adapter);
            } else {
                adapter.updateData(items);
            }

            adapter.setEditMode(isEditMode);
        }
    }
}