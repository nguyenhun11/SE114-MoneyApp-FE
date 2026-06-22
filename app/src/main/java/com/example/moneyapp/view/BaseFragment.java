package com.example.moneyapp.view;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.moneyapp.R;
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
                    uiHandler.updateFAB(getFabIcon(), v -> onFabClick());
                    uiHandler.setFABVisibility(true);
                } else {
                    uiHandler.setFABVisibility(false);
                }
            }
        }
    }

    //region Float Action Button and Bottom navigation
    protected boolean shouldShowFAB() {
        return true;
    }

    protected String getFabIcon() {
        return "gmd_add";
    }

    protected void onFabClick() { }
    protected boolean shouldShowBottomNavigation() {
        return true;
    }

    //endregion

    //region Header Setup
    private Drawable getShrunkIcon(String iconName, int color) {
        IconicsDrawable drawable = new IconicsDrawable(requireContext(), iconName);
        drawable.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);
        int paddingDp = 1;
        if ("gmd_navigate_before".equals(iconName)
                || "gmd_arrow_back".equals(iconName)) {
            paddingDp = 4;
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

        if (btnLeft != null) {
            if (leftIconName != null && !leftIconName.isEmpty()) {
                btnLeft.setVisibility(View.VISIBLE);
                btnLeft.setImageDrawable(getShrunkIcon(leftIconName, iconColor));
                btnLeft.setOnClickListener(leftListener);
            } else {
                btnLeft.setVisibility(View.GONE);
                btnLeft.setOnClickListener(null);
            }
        }

        if (btnRight != null) {
            if (rightIconName != null && !rightIconName.isEmpty()) {
                btnRight.setVisibility(View.VISIBLE);
                btnRight.setImageDrawable(getShrunkIcon(rightIconName, iconColor));
                btnRight.setOnClickListener(rightListener);
            } else {
                btnRight.setVisibility(View.GONE);
                btnRight.setOnClickListener(null);
            }
        }
    }

    protected void setupHeader(View view, @StringRes int titleResId, boolean showBackBtn) {
        setupHeader(view, getString(titleResId), showBackBtn);
    }

    protected void setupHeader(View view, String titleText, boolean showBackBtn) {
        String leftIcon = showBackBtn ? "gmd_navigate_before" : null;
        View.OnClickListener leftListener = showBackBtn ? v -> Navigation.findNavController(v).navigateUp() : null;
        setupHeader(view, titleText, leftIcon, leftListener, null, null);
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

        View ivArrow = view.findViewById(R.id.iv_arrow_down);

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
        setupActionButtons(view, leftIconName, leftListener, rightIconName, rightListener);
    }

    private void showAccountPopup() {
        Toast.makeText(getContext(), "Chọn nguồn tiền", Toast.LENGTH_SHORT).show();
    }
    //endregion

    //region Tabs Setup
    protected void setupHeaderTabs(View view, String[] tabTitles, int preSelectedTab, HeaderTabListener listener) {
        TabLayout tabLayout = view.findViewById(R.id.tab_layout_header);
        if (tabLayout == null || tabTitles == null || tabTitles.length == 0) return;
        tabLayout.removeAllTabs();

        // Tự động phân giải chế độ hiển thị dựa trên số lượng Tab
        if (tabTitles.length <= 3) {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        } else {
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER); // hoặc GRAVITY_CENTER tùy ý
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
                if (listener != null) {
                    listener.onTabSwitched(tab.getPosition());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                setTabTypeface(tab, Typeface.NORMAL);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        // Chạy listener lần đầu
        view.post(() -> {
            if (listener != null) listener.onTabSwitched(preSelectedTab);
        });
    }

    // Can thiệp vào TabLayout để in đậm chữ
    private void setTabTypeface(TabLayout.Tab tab, int style) {
        if (tab == null || tab.view == null) return;
        for (int i = 0; i < tab.view.getChildCount(); i++) {
            View child = tab.view.getChildAt(i);
            if (child instanceof TextView) {
                ((TextView) child).setTypeface(null, style);
            }
        }
    }
    //endregion
}
