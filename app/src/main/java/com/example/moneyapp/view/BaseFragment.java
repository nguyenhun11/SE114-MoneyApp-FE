package com.example.moneyapp.view;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.moneyapp.R;
import com.example.moneyapp.utils.DialogHelper;
import com.google.android.material.tabs.TabLayout;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

public abstract class BaseFragment extends Fragment {

    public interface HeaderTabListener {
        void onTabSwitched(int index);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            MainUIHandler uiHandler = mainActivity.getUiHandler();
            if (uiHandler != null) {
                uiHandler.setBottomNavigationVisibility(shouldShowBottomNavigation());

                if (shouldShowFAB()) {
                    uiHandler.updateFAB(getFabIcon(), getFabLabel(), getFabBackgroundColorRes(), v -> onFabClick());
                    uiHandler.setFABVisibility(true);
                } else {
                    uiHandler.setFABVisibility(false);
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        setFabEnabled(true);
    }

    //region Float Action Button and Bottom navigation
    protected boolean shouldShowFAB() {
        return true;
    }

    protected String getFabIcon() {
        return "gmd_add";
    }
    protected int getFabBackgroundColorRes() {
        return 0;
    }
    protected String getFabLabel() {
        return "Thêm giao dịch";
    }
    protected void onFabClick() { }
    protected void setFabEnabled(boolean isEnabled) {
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            MainUIHandler uiHandler = mainActivity.getUiHandler();
            if (uiHandler != null) {
                uiHandler.setFABEnabled(isEnabled);
            }
        }
    }
    protected boolean shouldShowBottomNavigation() {
        return true;
    }
    //endregion

    //region Header Setup
    private Drawable getShrunkIcon(String iconName, int color) {
        IconicsDrawable drawable = new IconicsDrawable(requireContext(), iconName);
        drawable.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);
        int paddingDp = 2;
        if ("gmd_navigate_before".equals(iconName) || "gmd_arrow_back".equals(iconName)) {
            paddingDp = 0;
        }
        int paddingPx = (int) (paddingDp * getResources().getDisplayMetrics().density);
        return new InsetDrawable(drawable, paddingPx);
    }

    private void setupActionButtons(View view,
                                    String leftIconName, View.OnClickListener leftListener,
                                    String rightIconName, View.OnClickListener rightListener) {

        IconicsImageView btnLeft = view.findViewById(R.id.btn_action_left);
        IconicsImageView btnRight = view.findViewById(R.id.btn_action_right);
        int iconColor = ContextCompat.getColor(requireContext(), R.color.colorOnPrimary);

        // ĐÃ SỬA: Nếu truyền NULL thì bỏ qua (giữ nguyên XML). Nếu truyền RỖNG ("") thì mới ẨN.
        if (btnLeft != null && leftIconName != null) {
            if (leftIconName.isEmpty()) {
                btnLeft.setVisibility(View.GONE);
                btnLeft.setOnClickListener(null);
            } else {
                btnLeft.setVisibility(View.VISIBLE);
                btnLeft.setImageDrawable(getShrunkIcon(leftIconName, iconColor));
                btnLeft.setOnClickListener(leftListener);
            }
        }

        if (btnRight != null && rightIconName != null) {
            if (rightIconName.isEmpty()) {
                btnRight.setVisibility(View.GONE);
                btnRight.setOnClickListener(null);
            } else {
                btnRight.setVisibility(View.VISIBLE);
                btnRight.setImageDrawable(getShrunkIcon(rightIconName, iconColor));
                btnRight.setOnClickListener(rightListener);
            }
        }
    }

    protected void setupHeader(View view, @StringRes int titleResId, boolean showBackBtn) {
        setupHeader(view, getString(titleResId), showBackBtn);
    }

    protected void setupHeader(View view, String titleText, boolean showBackBtn) {
        String leftIcon = showBackBtn ? "gmd_arrow_back" : "";
        View.OnClickListener leftListener = showBackBtn ? v -> Navigation.findNavController(v).navigateUp() : null;
        setupHeader(view, titleText, leftIcon, leftListener, "", null);
    }

    protected void setupHeader(View view,  @StringRes int titleResId,
                               String leftIconName, View.OnClickListener leftListener,
                               String rightIconName, View.OnClickListener rightListener) {
        setupHeader(view, getString(titleResId), leftIconName, leftListener, rightIconName, rightListener);
    }

    protected void setupHeader(View view, String titleText,
                               String leftIconName, View.OnClickListener leftListener,
                               String rightIconName, View.OnClickListener rightListener) {
        TextView tvTitle = view.findViewById(R.id.tv_header_title);
        if (tvTitle != null) tvTitle.setText(titleText);

        setupActionButtons(view, leftIconName, leftListener, rightIconName, rightListener);

        // Luôn gán sự kiện cho nút Menu nếu XML có khai báo
        View btnMenu = view.findViewById(R.id.btn_menu);
        if (btnMenu != null) {
            btnMenu.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).openRightSideMenu();
                }
            });
        }
    }
    //endregion

    //region Balance Selector Setup
    protected void setupBalanceSelector(View view, String accountName, String balance) {
        setupBalanceSelector(view, accountName, balance, true, null, null, null, null);
    }

    protected void setupBalanceSelector(View view, String accountName, String balance, boolean canSelect,
                                        String leftIconName, View.OnClickListener leftListener,
                                        String rightIconName, View.OnClickListener rightListener) {
        View selector = view.findViewById(R.id.btn_select_account);
        TextView tvAccount = view.findViewById(R.id.tv_account_name);
        TextView tvAmount = view.findViewById(R.id.tv_total_amount);

        if (tvAccount != null) tvAccount.setText(accountName);
        if (tvAmount != null) tvAmount.setText(balance);

        setupActionButtons(view, leftIconName, leftListener, rightIconName, rightListener);

        View btnMenu = view.findViewById(R.id.btn_menu);
        if (btnMenu != null) {
            btnMenu.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).openRightSideMenu();
                }
            });
        }
    }

    private void showAccountPopup() {
        DialogHelper.showSimpleDialog(getContext(), "Thông báo", "Chọn nguồn tiền");
    }
    //endregion

    //region Tabs Setup
    protected void setupHeaderTabs(View view, String[] tabTitles, int preSelectedTab, HeaderTabListener listener) {
        TabLayout tabLayout = view.findViewById(R.id.tab_layout_header);
        if (tabLayout == null || tabTitles == null || tabTitles.length == 0) return;
        tabLayout.removeAllTabs();

        if (tabTitles.length <= 3) {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        } else {
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        }

        for (String title : tabTitles) {
            tabLayout.addTab(tabLayout.newTab().setText(title));
        }

        TabLayout.Tab defaultTab = tabLayout.getTabAt(preSelectedTab);
        if (defaultTab != null) {
            defaultTab.select();
            setTabTypeface(defaultTab, Typeface.BOLD);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setTabTypeface(tab, Typeface.BOLD);
                if (listener != null) listener.onTabSwitched(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                setTabTypeface(tab, Typeface.NORMAL);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        view.post(() -> {
            if (listener != null) listener.onTabSwitched(preSelectedTab);
        });
    }

    private void setTabTypeface(TabLayout.Tab tab, int style) {
        if (tab == null || tab.view == null) return;
        for (int i = 0; i < tab.view.getChildCount(); i++) {
            View child = tab.view.getChildAt(i);
            if (child instanceof TextView) {
                ((TextView) child).setTypeface(null, style);
            }
        }
    }

    protected void setTabsEnabled(boolean enabled) {
        TabLayout tabLayout = requireView().findViewById(R.id.tab_layout_header);
        if (tabLayout != null) {
            LinearLayout tabStrip = ((LinearLayout) tabLayout.getChildAt(0));
            tabStrip.setEnabled(enabled);
            for (int i = 0; i < tabStrip.getChildCount(); i++) {
                tabStrip.getChildAt(i).setClickable(enabled);
            }
            tabLayout.setAlpha(enabled ? 1.0f : 0.5f);
        }
    }
    //endregion

    protected void hideKeyboard() {
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            ScrollView mainScrollView = requireView().findViewById(R.id.main_scroll_view);
            if (mainScrollView != null && mainScrollView.getChildAt(0) != null) {
                mainScrollView.getChildAt(0).requestFocus();
            }
        }
    }
}