package com.example.moneyapp.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.model.Account;
import com.example.moneyapp.model.Category;
import com.example.moneyapp.view.account.AccountPopupAdapter;
import com.example.moneyapp.view.category.CategoryAdapter;
import com.example.moneyapp.view.category.CategoryGroupAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.List;

public class PopupHelper {

    private static View createBaseSheetView(Context context, String title) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_selector_bottom_sheet, null);
        TextView tvTitle = view.findViewById(R.id.tv_sheet_title);
        tvTitle.setText(title);
        return view;
    }

    public static void showAccountFilterPopup(Context context, List<Account> accountList,
                                              String currentAccountId,
                                              boolean showAllOption,
                                              AccountPopupAdapter.OnAccountClickListener listener) {
        BottomSheetDialog dialog = new BottomSheetDialog(context, R.style.TransparentBottomSheetDialog);
        View view = createBaseSheetView(context, "Chọn tài khoản");

        TextView tvSelectAll = view.findViewById(R.id.tv_select_all);
        if (showAllOption) {
            tvSelectAll.setVisibility(View.VISIBLE);
            tvSelectAll.setText("Tất cả tài khoản");
            tvSelectAll.setOnClickListener(v -> {
                if (listener != null) listener.onAccountClick(null);
                dialog.dismiss();
            });
        }

        RecyclerView rvList = view.findViewById(R.id.rv_items);
        rvList.setLayoutManager(new LinearLayoutManager(context));

        AccountPopupAdapter adapter = new AccountPopupAdapter(accountList, currentAccountId, account -> {
            if (listener != null) listener.onAccountClick(account);
            dialog.dismiss();
        });
        rvList.setAdapter(adapter);

        dialog.setContentView(view);
        setupBottomSheetBehavior(dialog, context);
        dialog.show();
    }

    public static void showCategoryFilterPopup(Context context,
                                               List<Category> categoryList,
                                               boolean showAllOption,
                                               CategoryAdapter.OnCategoryClickListener listener) {
        BottomSheetDialog dialog = new BottomSheetDialog(context, R.style.TransparentBottomSheetDialog);
        View view = createBaseSheetView(context, "Chọn hạng mục");

        TextView tvSelectAll = view.findViewById(R.id.tv_select_all);
        if (showAllOption) {
            tvSelectAll.setVisibility(View.VISIBLE);
            tvSelectAll.setText("Tất cả hạng mục");
            tvSelectAll.setOnClickListener(v -> {
                if (listener != null) listener.onCategoryClick(null);
                dialog.dismiss();
            });
        }

        RecyclerView rvList = view.findViewById(R.id.rv_items);
        rvList.setLayoutManager(new LinearLayoutManager(context));

        CategoryGroupAdapter adapter = new CategoryGroupAdapter(category -> {
            if (listener != null) listener.onCategoryClick(category);
            dialog.dismiss();
        });
        adapter.setData(categoryList);

        rvList.setAdapter(adapter);
        dialog.setContentView(view);
        setupBottomSheetBehavior(dialog, context);
        dialog.show();
    }
    public interface OnResourceSelectedListener {
        void onSelected(int id);
    }

    public static void showColorPicker(Context context, OnResourceSelectedListener listener) {
        showPicker(context, true, "Chọn màu sắc", listener);
    }

    public static void showIconPicker(Context context, OnResourceSelectedListener listener) {
        showPicker(context, false, "Chọn biểu tượng", listener);
    }

    public static void showGoalIconPicker(Context context, OnResourceSelectedListener listener) {
        showGoalPicker(context, "Chọn biểu tượng mục tiêu", listener);
    }

    private static void showGoalPicker(Context context, String title, OnResourceSelectedListener listener) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View view = createBaseSheetView(context, title);

        RecyclerView rvList = view.findViewById(R.id.rv_items);
        rvList.setLayoutManager(new GridLayoutManager(context, 4));

        int startIdx = AppResourceManager.getGoalIconStart();
        int count = AppResourceManager.getIconCount() - startIdx;

        RecyclerView.Adapter<RecyclerView.ViewHolder> adapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @Override
            public int getItemCount() {
                return count;
            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(context).inflate(R.layout.item_selector_icon, parent, false);
                return new RecyclerView.ViewHolder(itemView) {};
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                int actualPos = startIdx + position;
                IconicsImageView ivIcon = holder.itemView.findViewById(R.id.iv_icon);
                ivIcon.setImageDrawable(AppResourceManager.getBlackIcon(context, actualPos));

                holder.itemView.setOnClickListener(v -> {
                    if (listener != null) listener.onSelected(actualPos);
                    dialog.dismiss();
                });
            }
        };

        rvList.setAdapter(adapter);
        dialog.setContentView(view);
        setupBottomSheetBehavior(dialog, context);
        dialog.show();
    }

    private static void showPicker(Context context, boolean isColorPicker, String title, OnResourceSelectedListener listener) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View view = createBaseSheetView(context, title);

        RecyclerView rvList = view.findViewById(R.id.rv_items);
        rvList.setLayoutManager(new GridLayoutManager(context, 4));

        RecyclerView.Adapter<RecyclerView.ViewHolder> adapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @Override
            public int getItemCount() {
                return isColorPicker ? AppResourceManager.getColorCount() : AppResourceManager.getIconCount();
            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                int layoutId = isColorPicker ? R.layout.item_selector_color : R.layout.item_selector_icon;
                View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false);
                return new RecyclerView.ViewHolder(itemView) {
                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                if (isColorPicker) {
                    View colorCircle = holder.itemView.findViewById(R.id.view_color);
                    int colorValue = AppResourceManager.getColor(position);
                    colorCircle.setBackgroundTintList(ColorStateList.valueOf(colorValue));
                } else {
                    IconicsImageView ivIcon = holder.itemView.findViewById(R.id.iv_icon);
                    String iconName = AppResourceManager.getIconName(position);
                    ivIcon.setIcon(new IconicsDrawable(context, iconName));
                }

                holder.itemView.setOnClickListener(v -> {
                    if (listener != null) listener.onSelected(position);
                    dialog.dismiss();
                });
            }
        };

        rvList.setAdapter(adapter);
        dialog.setContentView(view);
        setupBottomSheetBehavior(dialog, context);
        dialog.show();
    }

    private static void setupBottomSheetBehavior(BottomSheetDialog dialog, Context context) {
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog d = (BottomSheetDialog) dialogInterface;

            View bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);

            if (bottomSheet != null) {
                int screenHeight = context.getResources().getDisplayMetrics().heightPixels;

                ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
                layoutParams.height = (int) (screenHeight * 0.85);
                bottomSheet.setLayoutParams(layoutParams);

                com.google.android.material.bottomsheet.BottomSheetBehavior<View> behavior =
                        BottomSheetBehavior.from(bottomSheet);

                behavior.setPeekHeight((int) (screenHeight * 0.5));
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }
}