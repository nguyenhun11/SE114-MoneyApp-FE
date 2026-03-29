package com.example.moneyapp.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.moneyapp.R;

public abstract class BaseFragment extends Fragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            MainUIHandler uiHandler = mainActivity.getUiHandler();
            if (uiHandler != null) {
                uiHandler.updateFAB(getFabIcon(), v -> onFabClick());
                uiHandler.setBottomNavigationVisibility(shouldShowBottomNavigation());
            }
        }
    }

    @DrawableRes
    protected int getFabIcon() {
        return R.drawable.ic_add_white;
    }

    protected boolean shouldShowBottomNavigation() {
        return true;
    }

    protected void onFabClick() {
    }

    //region Header Setup
    /**
     * Cấu hình Header với String resource
     */
    protected void setupHeader(View view, @StringRes int titleResId, boolean showBackBtn) {
        setupHeader(view, getString(titleResId), showBackBtn);
    }

    /**
     * Cấu hình Header với String title
     */
    protected void setupHeader(View view, String titleText, boolean showBackBtn) {
        TextView tvTitle = view.findViewById(R.id.tv_header_title);
        ImageView btnBack = view.findViewById(R.id.btn_back);

        if (tvTitle != null) {
            tvTitle.setText(titleText);
        }

        if (btnBack != null) {
            btnBack.setVisibility(showBackBtn ? View.VISIBLE : View.GONE);
            if (showBackBtn) {
                btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
            }
        }
    }
    //endregion

    //region Balance Selector Setup
    /**
     * Cấu hình bộ chọn tài khoản/số dư
     * @param canSelect true nếu cho phép nhấn để chọn tài khoản (hiện mũi tên)
     */
    protected void setupBalanceSelector(View view, String accountName, String balance, boolean canSelect) {
        View selector = view.findViewById(R.id.btn_select_account);
        TextView tvAccount = view.findViewById(R.id.tv_account_name);
        TextView tvAmount = view.findViewById(R.id.tv_total_amount);
        ImageView ivArrow = view.findViewById(R.id.iv_arrow_down);

        if (tvAccount != null) tvAccount.setText(accountName);
        if (tvAmount != null) tvAmount.setText(balance);
        
        if (selector != null) {
            if (canSelect) {
                selector.setOnClickListener(v -> showAccountPopup());
                if (ivArrow != null) ivArrow.setVisibility(View.VISIBLE);
            } else {
                selector.setOnClickListener(null);
                selector.setClickable(false);
                if (ivArrow != null) ivArrow.setVisibility(View.GONE);
            }
        }
    }

    private void showAccountPopup() {
        Toast.makeText(getContext(), "Chọn nguồn tiền", Toast.LENGTH_SHORT).show();
    }
    //endregion

    //region Tabs Setup
    protected void setupIncomeExpenseTabs(View view, TabSwitchListener listener) {
        TextView tvTabLeft = view.findViewById(R.id.tv_tab_left);
        TextView tvTabRight = view.findViewById(R.id.tv_tab_right);
        View animatedIndicator = view.findViewById(R.id.view_tab_indicator);

        if (tvTabLeft == null || tvTabRight == null || animatedIndicator == null) return;

        tvTabLeft.setOnClickListener(v -> handleTabSwitch(true, tvTabLeft, tvTabRight, animatedIndicator, listener));
        tvTabRight.setOnClickListener(v -> handleTabSwitch(false, tvTabLeft, tvTabRight, animatedIndicator, listener));
    }

    private void handleTabSwitch(boolean isLeftTab, TextView tvLeft, TextView tvRight, View animatedIndicator, TabSwitchListener listener) {
        int colorSelected = ContextCompat.getColor(requireContext(), R.color.tabSelectedColor);
        int colorUnselected = ContextCompat.getColor(requireContext(), R.color.colorOnSurfaceVariant);

        if (isLeftTab) {
            tvLeft.setTextColor(colorSelected);
            tvLeft.setTypeface(null, Typeface.BOLD);
            tvRight.setTextColor(colorUnselected);
            tvRight.setTypeface(null, Typeface.NORMAL);

            animatedIndicator.animate().translationX(0).setDuration(250).start();
        } else {
            tvRight.setTextColor(colorSelected);
            tvRight.setTypeface(null, Typeface.BOLD);
            tvLeft.setTextColor(colorUnselected);
            tvLeft.setTypeface(null, Typeface.NORMAL);

            float distance = tvRight.getX() - tvLeft.getX();
            animatedIndicator.animate().translationX(distance).setDuration(250).start();
        }

        if (listener != null) {
            listener.onTabSwitched(isLeftTab);
        }
    }

    public interface TabSwitchListener {
        void onTabSwitched(boolean isLeftTab);
    }
    //endregion
}
