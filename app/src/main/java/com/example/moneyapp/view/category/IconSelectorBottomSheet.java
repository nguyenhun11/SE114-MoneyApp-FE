package com.example.moneyapp.view.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.utils.AppResourceManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class IconSelectorBottomSheet extends BottomSheetDialogFragment {

    public interface OnIconSelectedListener {
        void onIconSelected(int iconId);
    }

    private OnIconSelectedListener listener;

    public static IconSelectorBottomSheet newInstance(OnIconSelectedListener listener) {
        IconSelectorBottomSheet fragment = new IconSelectorBottomSheet();
        fragment.listener = listener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_selector_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvTitle = view.findViewById(R.id.tv_sheet_title);
        tvTitle.setText("Chọn biểu tượng");

        RecyclerView rvItems = view.findViewById(R.id.rv_items);
        rvItems.setLayoutManager(new GridLayoutManager(getContext(), 4));
        rvItems.setAdapter(new IconAdapter());
    }

    private class IconAdapter extends RecyclerView.Adapter<IconAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selector_icon, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            int iconRes = AppResourceManager.getIconRes(position);
            holder.ivIcon.setImageResource(iconRes);
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onIconSelected(position);
                }
                dismiss();
            });
        }

        @Override
        public int getItemCount() {
            return AppResourceManager.getIconCount();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivIcon;

            ViewHolder(View itemView) {
                super(itemView);
                ivIcon = itemView.findViewById(R.id.iv_icon);
            }
        }
    }
}
