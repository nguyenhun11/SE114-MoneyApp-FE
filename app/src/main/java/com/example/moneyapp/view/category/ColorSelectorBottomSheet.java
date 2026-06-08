package com.example.moneyapp.view.category;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.utils.AppResourceManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ColorSelectorBottomSheet extends BottomSheetDialogFragment {

    public interface OnColorSelectedListener {
        void onColorSelected(int colorId);
    }

    private OnColorSelectedListener listener;

    public static ColorSelectorBottomSheet newInstance(OnColorSelectedListener listener) {
        ColorSelectorBottomSheet fragment = new ColorSelectorBottomSheet();
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
        tvTitle.setText("Chọn màu sắc");

        RecyclerView rvItems = view.findViewById(R.id.rv_items);
        rvItems.setLayoutManager(new GridLayoutManager(getContext(), 4));
        rvItems.setAdapter(new ColorAdapter());
    }

    private class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selector_color, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            int colorValue = AppResourceManager.getColor(position);
            holder.viewColor.setBackgroundTintList(ColorStateList.valueOf(colorValue));
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onColorSelected(position);
                }
                dismiss();
            });
        }

        @Override
        public int getItemCount() {
            return AppResourceManager.getColorCount();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            View viewColor;

            ViewHolder(View itemView) {
                super(itemView);
                viewColor = itemView.findViewById(R.id.view_color);
            }
        }
    }
}
