package com.example.moneyapp.ui.category;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.model.Category;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CategoryGroupAdapter extends RecyclerView.Adapter<CategoryGroupAdapter.GroupViewHolder> {

    private Map<String, List<Category>> groupedCategories = new LinkedHashMap<>();
    private List<String> groupNames = new ArrayList<>();
    private CategoryAdapter.OnCategoryClickListener listener;

    public CategoryGroupAdapter(CategoryAdapter.OnCategoryClickListener listener) {
        this.listener = listener;
    }

    public void setData(List<Category> categories) {
        groupedCategories.clear();
        groupNames.clear();
        for (Category category : categories) {
            String group = category.getGroupName();
            if (!groupedCategories.containsKey(group)) {
                groupedCategories.put(group, new ArrayList<>());
                groupNames.add(group);
            }
            groupedCategories.get(group).add(category);
        }
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
        holder.bind(groupName, items, listener);
    }

    @Override
    public int getItemCount() {
        return groupNames.size();
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvGroupName;
        private final RecyclerView rvItems;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGroupName = itemView.findViewById(R.id.tv_group_name);
            rvItems = itemView.findViewById(R.id.rv_items);
        }

        public void bind(String groupName, List<Category> items, CategoryAdapter.OnCategoryClickListener listener) {
            tvGroupName.setText(groupName);
            CategoryAdapter adapter = new CategoryAdapter(items, listener);
            rvItems.setLayoutManager(new GridLayoutManager(itemView.getContext(), 3));
            rvItems.setAdapter(adapter);
        }
    }
}
