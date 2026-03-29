package com.example.moneyapp.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.moneyapp.R;

public abstract class BaseFragment extends Fragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Cấu hình FAB khi fragment được hiển thị
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.updateFAB(getFabIcon(), v -> onFabClick());
        }
    }

    /**
     * Trả về icon cho FAB của fragment này. 
     * Mặc định là dấu cộng trắng.
     */
    @DrawableRes
    protected int getFabIcon() {
        return R.drawable.ic_add_white;
    }

    /**
     * Xử lý khi click vào FAB.
     * Fragment con cần override để thực hiện hành động riêng.
     */
    protected void onFabClick() {
        // Mặc định không làm gì hoặc thực hiện hành động chung
    }

    protected void setupHeader(View view, String titleText, boolean showBackBtn) {
        TextView tvTitle = view.findViewById(R.id.tv_header_title);
        ImageView btnBack = view.findViewById(R.id.btn_back);

        if (tvTitle != null) {
            tvTitle.setText(titleText);
        }

        if (btnBack != null) {
            if (showBackBtn) {
                btnBack.setVisibility(View.VISIBLE);
                btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
            } else {
                btnBack.setVisibility(View.INVISIBLE);
            }
        }
    }

    protected void setupHeader(View view, int titleResId, boolean showBackBtn) {
        TextView tvTitle = view.findViewById(R.id.tv_header_title);
        ImageView btnBack = view.findViewById(R.id.btn_back);

        if (tvTitle != null) {
            tvTitle.setText(titleResId);
        }

        if (btnBack != null) {
            if (showBackBtn) {
                btnBack.setVisibility(View.VISIBLE);
                btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
            } else {
                btnBack.setVisibility(View.INVISIBLE);
            }
        }
    }

    protected void setupIncomeExpenseTabs(View view, TabSwitchListener listener) {
        TextView tvExpense = view.findViewById(R.id.tv_tab_expense);
        TextView tvIncome = view.findViewById(R.id.tv_tab_income);
        View animatedIndicator = view.findViewById(R.id.animated_indicator);

        if (tvExpense == null || tvIncome == null || animatedIndicator == null) return;

        tvExpense.setOnClickListener(v -> handleTabSwitch(true, tvExpense, tvIncome, animatedIndicator, listener));
        tvIncome.setOnClickListener(v -> handleTabSwitch(false, tvExpense, tvIncome, animatedIndicator, listener));
    }

    private void handleTabSwitch(boolean isExpense, TextView tvExpense, TextView tvIncome, View animatedIndicator, TabSwitchListener listener) {
        int colorSelected = ContextCompat.getColor(requireContext(), R.color.tabSelectedColor);
        int colorUnselected = ContextCompat.getColor(requireContext(), R.color.colorOnSurfaceVariant);

        if (isExpense) {
            tvExpense.setTextColor(colorSelected);
            tvExpense.setTypeface(null, Typeface.BOLD);
            tvIncome.setTextColor(colorUnselected);
            tvIncome.setTypeface(null, Typeface.NORMAL);

            animatedIndicator.animate()
                    .translationX(0)
                    .setDuration(250)
                    .start();
        } else {
            tvIncome.setTextColor(colorSelected);
            tvIncome.setTypeface(null, Typeface.BOLD);
            tvExpense.setTextColor(colorUnselected);
            tvExpense.setTypeface(null, Typeface.NORMAL);

            float distance = tvIncome.getX() - tvExpense.getX();
            animatedIndicator.animate()
                    .translationX(distance)
                    .setDuration(250)
                    .start();
        }

        if (listener != null) {
            listener.onTabSwitched(isExpense);
        }
    }

    public interface TabSwitchListener {
        void onTabSwitched(boolean isExpense);
    }
}
