package com.example.moneyapp.view.category;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.model.Category;
import com.example.moneyapp.utils.AppResourceManager;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.Collections;
import java.util.List;

public class ReorderCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnGroupActionListener {
        void onDeleteGroup(String groupId, String groupName);
    }

    public static abstract class ListItem {
        public static final int TYPE_HEADER = 0;
        public static final int TYPE_ITEM = 1;
        public abstract int getType();
    }

    public static class HeaderItem extends ListItem {
        public String groupId;
        public String groupName;
        public boolean isEmpty; // Cờ kiểm tra nhóm trống

        public HeaderItem(String groupId, String groupName, boolean isEmpty) {
            this.groupId = groupId;
            this.groupName = groupName;
            this.isEmpty = isEmpty;
        }
        @Override public int getType() { return TYPE_HEADER; }
    }

    public static class CategoryItem extends ListItem {
        public Category category;
        public CategoryItem(Category category) { this.category = category; }
        @Override public int getType() { return TYPE_ITEM; }
    }

    private List<ListItem> items;
    private final OnGroupActionListener actionListener;

    public ReorderCategoryAdapter(List<ListItem> items, OnGroupActionListener actionListener) {
        this.items = items;
        this.actionListener = actionListener;
    }

    public void setItems(List<ListItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    public List<ListItem> getItems() {
        return items;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ListItem.TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_group, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ListItem item = items.get(position);
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind((HeaderItem) item, actionListener);
        } else if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).bind(((CategoryItem) item).category);
        }
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(items, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(items, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvGroupName;
        private final RecyclerView rvItemsCon;
        private final View btnDelete;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGroupName = itemView.findViewById(R.id.tv_group_name);
            rvItemsCon = itemView.findViewById(R.id.rv_items);
            btnDelete = itemView.findViewById(R.id.btn_delete_group);
        }

        public void bind(HeaderItem header, OnGroupActionListener listener) {
            tvGroupName.setText(header.groupName);
            tvGroupName.setVisibility(View.VISIBLE);

            if (btnDelete != null) {
                btnDelete.setVisibility(header.isEmpty ? View.VISIBLE : View.GONE);
                btnDelete.setOnClickListener(v -> {
                    if (listener != null) listener.onDeleteGroup(header.groupId, header.groupName);
                });
            }

            itemView.setPadding(0, 48, 0, 16);
            if (rvItemsCon != null) rvItemsCon.setVisibility(View.GONE);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final View viewColorCircle;
        private final IconicsImageView ivIcon;
        private final TextView tvName;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            viewColorCircle = itemView.findViewById(R.id.view_color_circle);
            ivIcon = itemView.findViewById(R.id.iv_category_icon);
            tvName = itemView.findViewById(R.id.tv_category_name);
        }

        public void bind(Category category) {
            tvName.setText(category.getCategoryName());
            int colorValue = AppResourceManager.getColor(category.getColor());
            viewColorCircle.setBackgroundTintList(ColorStateList.valueOf(colorValue));

            Context context = itemView.getContext();
            ivIcon.setImageDrawable(AppResourceManager.getWhiteIcon(context, category.getIcon()));
            ivIcon.setImageTintList(ColorStateList.valueOf(android.graphics.Color.WHITE));
        }
    }
}