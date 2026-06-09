package com.example.moneyapp.view;

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
                uiHandler.setBottomNavigationVisibility(shouldShowBottomNavigation());

                if (shouldShowFAB()) {
                    uiHandler.updateFAB(getFabIcon(), v -> onFabClick());
                    uiHandler.setFABVisibility(true);
                } else {
                    uiHandler.setFABVisibility(false);
                }
            }
        }
    }

    protected boolean shouldShowFAB() {
        return true;
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
    private void setupActionButtons(View view,
                                    @DrawableRes int leftIcon, View.OnClickListener leftListener,
                                    @DrawableRes int rightIcon, View.OnClickListener rightListener) {
        ImageView btnLeft = view.findViewById(R.id.btn_action_left);
        ImageView btnRight = view.findViewById(R.id.btn_action_right);

        if (btnLeft != null) {
            if (leftIcon != 0) {
                btnLeft.setVisibility(View.VISIBLE);
                btnLeft.setImageResource(leftIcon);
                btnLeft.setOnClickListener(leftListener);
            } else {
                btnLeft.setVisibility(View.GONE);
                btnLeft.setOnClickListener(null);
            }
        }

        if (btnRight != null) {
            if (rightIcon != 0) {
                btnRight.setVisibility(View.VISIBLE);
                btnRight.setImageResource(rightIcon);
                btnRight.setOnClickListener(rightListener);
            } else {
                btnRight.setVisibility(View.GONE);
                btnRight.setOnClickListener(null);
            }
        }
    }

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
        int leftIcon = showBackBtn ? R.drawable.ic_back : 0;
        View.OnClickListener leftListener = showBackBtn ? v -> Navigation.findNavController(v).navigateUp() : null;
        setupHeader(view, titleText, leftIcon, leftListener, 0, null);
    }

    protected void setupHeader(View view, String titleText,
                               @DrawableRes int leftIcon, View.OnClickListener leftListener,
                               @DrawableRes int rightIcon, View.OnClickListener rightListener) {
        TextView tvTitle = view.findViewById(R.id.tv_header_title);
        if (tvTitle != null) tvTitle.setText(titleText);
        setupActionButtons(view, leftIcon, leftListener, rightIcon, rightListener);
    }

    //endregion

    //region Balance Selector Setup
    /**
     * Cấu hình bộ chọn tài khoản/số dư
     * @param canSelect true nếu cho phép nhấn để chọn tài khoản (hiện mũi tên)
     */
    protected void setupBalanceSelector(View view, String accountName, String balance, boolean canSelect) {
        setupBalanceSelector(view, accountName, balance, canSelect, 0, null, 0, null);
    }

    protected void setupBalanceSelector(View view, String accountName, String balance, boolean canSelect,
                                        @DrawableRes int leftIcon, View.OnClickListener leftListener,
                                        @DrawableRes int rightIcon, View.OnClickListener rightListener) {
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
        setupActionButtons(view, leftIcon, leftListener, rightIcon, rightListener);
    }

    private void showAccountPopup() {
        Toast.makeText(getContext(), "Chọn nguồn tiền", Toast.LENGTH_SHORT).show();
    }
    //endregion

    //region Tabs Setup
    protected void setupIncomeExpenseTabs(View view, TabSwitchListener listener) {
        setupIncomeExpenseTabs(view, true, listener);
    }

    protected void setupIncomeExpenseTabs(View view, boolean initialIsExpense, TabSwitchListener listener) {
        TextView tvTabExpense = view.findViewById(R.id.tv_tab_expense);
        TextView tvTabIncome = view.findViewById(R.id.tv_tab_income);
        View animatedIndicator = view.findViewById(R.id.view_tab_indicator);

        if (tvTabExpense == null || tvTabIncome == null || animatedIndicator == null) return;

        tvTabExpense.setOnClickListener(v -> handleTabSwitch(true, tvTabExpense, tvTabIncome, animatedIndicator, listener));
        tvTabIncome.setOnClickListener(v -> handleTabSwitch(false, tvTabExpense, tvTabIncome, animatedIndicator, listener));

        // Set initial state without animation if possible, or just call handleTabSwitch
        view.post(() -> {
            handleTabSwitch(initialIsExpense, tvTabExpense, tvTabIncome, animatedIndicator, null);
        });
    }

    private void handleTabSwitch(boolean isExpense, TextView tvExpense, TextView tvIncome, View animatedIndicator, TabSwitchListener listener) {
        int colorSelected = ContextCompat.getColor(requireContext(), R.color.tabSelectedColor);
        int colorUnselected = ContextCompat.getColor(requireContext(), R.color.colorOnSurfaceVariant);

        if (isExpense) {
            tvExpense.setTextColor(colorSelected);
            tvExpense.setTypeface(null, Typeface.BOLD);
            tvIncome.setTextColor(colorUnselected);
            tvIncome.setTypeface(null, Typeface.NORMAL);

            animatedIndicator.animate().translationX(0).setDuration(250).start();
        } else {
            tvIncome.setTextColor(colorSelected);
            tvIncome.setTypeface(null, Typeface.BOLD);
            tvExpense.setTextColor(colorUnselected);
            tvExpense.setTypeface(null, Typeface.NORMAL);

            float distance = tvIncome.getX() - tvExpense.getX();
            animatedIndicator.animate().translationX(distance).setDuration(250).start();
        }

        if (listener != null) {
            listener.onTabSwitched(isExpense);
        }
    }

    /**
     * Cấu hình 3 tabs: Chung, Chi, Thu
     */
    protected void setupThreeTabs(View view, ThreeTabSwitchListener listener) {
        TextView tvGeneral = view.findViewById(R.id.tv_tab_general);
        TextView tvExpense = view.findViewById(R.id.tv_tab_expense);
        TextView tvIncome = view.findViewById(R.id.tv_tab_income);
        View animatedIndicator = view.findViewById(R.id.view_tab_indicator);

        if (tvGeneral == null || tvExpense == null || tvIncome == null || animatedIndicator == null) return;

        tvGeneral.setOnClickListener(v -> handleThreeTabSwitch(0, tvGeneral, tvExpense, tvIncome, animatedIndicator, listener));
        tvExpense.setOnClickListener(v -> handleThreeTabSwitch(1, tvGeneral, tvExpense, tvIncome, animatedIndicator, listener));
        tvIncome.setOnClickListener(v -> handleThreeTabSwitch(2, tvGeneral, tvExpense, tvIncome, animatedIndicator, listener));
    }

    private void handleThreeTabSwitch(int index, TextView tv0, TextView tv1, TextView tv2, View animatedIndicator, ThreeTabSwitchListener listener) {
        int colorSelected = ContextCompat.getColor(requireContext(), R.color.tabSelectedColor);
        int colorUnselected = ContextCompat.getColor(requireContext(), R.color.colorOnSurfaceVariant);

        TextView[] tabs = {tv0, tv1, tv2};
        for (int i = 0; i < tabs.length; i++) {
            if (i == index) {
                tabs[i].setTextColor(colorSelected);
                tabs[i].setTypeface(null, Typeface.BOLD);
            } else {
                tabs[i].setTextColor(colorUnselected);
                tabs[i].setTypeface(null, Typeface.NORMAL);
            }
        }

        float translationX = 0;
        if (index == 1) {
            translationX = tv1.getX() - tv0.getX();
        } else if (index == 2) {
            translationX = tv2.getX() - tv0.getX();
        }
        
        animatedIndicator.animate().translationX(translationX).setDuration(250).start();

        if (listener != null) {
            listener.onTabSwitched(index);
        }
    }

    public interface TabSwitchListener {
        void onTabSwitched(boolean isExpense);
    }

    public interface ThreeTabSwitchListener {
        /**
         * @param index 0: General, 1: Expense, 2: Income
         */
        void onTabSwitched(int index);
    }
    //endregion
}
