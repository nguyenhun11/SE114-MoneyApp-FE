package com.example.moneyapp.view.home;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.data.local.PreferenceManager;
import com.example.moneyapp.data.remote.response.DashboardOverviewResponse;
import com.example.moneyapp.data.remote.response.QuestResponse;
import com.example.moneyapp.data.repository.PendingTransactionRepository;
import com.example.moneyapp.utils.AppResourceManager;
import com.example.moneyapp.utils.CurrencyFormatter;
import com.example.moneyapp.utils.DialogHelper;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.view.category.CategorySummaryAdapter;
import com.example.moneyapp.view.components.TimeSelectorView;
import com.example.moneyapp.viewmodel.HomeViewModel;
import com.example.moneyapp.viewmodel.ProfileViewModel;
import com.example.moneyapp.viewmodel.QuestViewModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends BaseFragment {

    private RecyclerView rvCategories;
    private CategorySummaryAdapter adapter;
    private View chartsWrapper;
    private View layoutDashboardOverview;

    private View pieChartContainer;
    private View linearChartContainer;
    private AppBarLayout appBarLayout;

    private PieChart pieChart;
    private TextView tvTotalAmountPie;
    private TextView tvTotalAmountLinear;
    private TextView tvMenuQuestStatus;

    private HomeViewModel homeViewModel;
    private QuestViewModel questViewModel;
    private ProfileViewModel profileViewModel;

    private boolean isExpenseTab = true;
    private Date currentStartDate;
    private Date currentEndDate;

    private CardView cardPendingBanner;
    private TextView tvPendingBannerText;
    private PendingTransactionRepository pendingRepository;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        questViewModel = new ViewModelProvider(requireActivity()).get(QuestViewModel.class);
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        rvCategories = view.findViewById(R.id.rv_categories);
        pieChartContainer = view.findViewById(R.id.pie_chart_container);
        linearChartContainer = view.findViewById(R.id.linear_chart_container);
        appBarLayout = view.findViewById(R.id.statistics);
        chartsWrapper = view.findViewById(R.id.charts_wrapper);
        layoutDashboardOverview = view.findViewById(R.id.layout_dashboard_overview);

        pieChart = view.findViewById(R.id.main_pie_chart);
        tvTotalAmountPie = view.findViewById(R.id.tv_total_amount_pie);
        tvTotalAmountLinear = view.findViewById(R.id.tv_total_amount_linear);

        tvMenuQuestStatus = view.findViewById(R.id.tv_menu_quests_status_exp);

        View btnMenuStatExp = view.findViewById(R.id.btn_stat_exp);
        View btnMenuBudgetExp = view.findViewById(R.id.btn_budget_exp);
        View btnMenuGoalsExp = view.findViewById(R.id.btn_goals_exp);
        View btnMenuCityExp = view.findViewById(R.id.btn_city_exp);
        View btnMenuQuestsExp = view.findViewById(R.id.btn_quests_exp);
        View btnMenuSettingsExp = view.findViewById(R.id.btn_settings_exp);

        View btnMenuStatCol = view.findViewById(R.id.btn_stat_col);
        View btnMenuBudgetCol = view.findViewById(R.id.btn_budget_col);
        View btnMenuGoalsCol = view.findViewById(R.id.btn_goals_col);
        View btnMenuCityCol = view.findViewById(R.id.btn_city_col);
        View btnMenuQuestsCol = view.findViewById(R.id.btn_quests_col);
        View btnMenuSettingsCol = view.findViewById(R.id.btn_settings_col);
        NavOptions slideOptions = new NavOptions.Builder()
                .setEnterAnim(R.anim.slide_in_right).setExitAnim(R.anim.slide_out_left)
                .setPopEnterAnim(R.anim.slide_in_left).setPopExitAnim(R.anim.slide_out_right).build();

// 1. Thống kê
        View.OnClickListener statListener = v -> Navigation.findNavController(v).navigate(R.id.statisticsFragment, null, slideOptions);
        if (btnMenuStatExp != null) btnMenuStatExp.setOnClickListener(statListener);
        if (btnMenuStatCol != null) btnMenuStatCol.setOnClickListener(statListener);

        // 2. Ngân sách
        View.OnClickListener budgetListener = v -> Navigation.findNavController(v).navigate(R.id.budgetFragment, null, slideOptions);
        if (btnMenuBudgetExp != null) btnMenuBudgetExp.setOnClickListener(budgetListener);
        if (btnMenuBudgetCol != null) btnMenuBudgetCol.setOnClickListener(budgetListener);

        // 3. Tiết kiệm (Goals)
        View.OnClickListener goalsListener = v -> Navigation.findNavController(v).navigate(R.id.goalFragment, null, slideOptions);
        if (btnMenuGoalsExp != null) btnMenuGoalsExp.setOnClickListener(goalsListener);
        if (btnMenuGoalsCol != null) btnMenuGoalsCol.setOnClickListener(goalsListener);

        // 4. Thành phố (City)
        View.OnClickListener cityListener = v -> Navigation.findNavController(v).navigate(R.id.cityFragment, null, slideOptions);
        if (btnMenuCityExp != null) btnMenuCityExp.setOnClickListener(cityListener);
        if (btnMenuCityCol != null) btnMenuCityCol.setOnClickListener(cityListener);

        // 5. Nhiệm vụ (Quests)
        View.OnClickListener questsListener = v -> Navigation.findNavController(v).navigate(R.id.questFragment, null, slideOptions);
        if (btnMenuQuestsExp != null) btnMenuQuestsExp.setOnClickListener(questsListener);
        if (btnMenuQuestsCol != null) btnMenuQuestsCol.setOnClickListener(questsListener);

        // 6. Cài đặt (Settings)
        View.OnClickListener settingsListener = v -> Navigation.findNavController(v).navigate(R.id.settingsFragment, null, slideOptions);
        if (btnMenuSettingsExp != null) btnMenuSettingsExp.setOnClickListener(settingsListener);
        if (btnMenuSettingsCol != null) btnMenuSettingsCol.setOnClickListener(settingsListener);

        TimeSelectorView timeSelector = view.findViewById(R.id.time_selector);
        timeSelector.setOnTimeRangeChangeListener((startDate, endDate) -> {
            currentStartDate = startDate;
            currentEndDate = endDate;
            homeViewModel.setTimeRangeAndReload(startDate, endDate);
        });

        setupRecyclerView();
        setupPieChart();
        setupScrollBehavior();

        int initialTab = PreferenceManager.getInstance(requireContext()).getLastHomeTab();
        String[] homeTabs = {"Tổng quan", "Chi tiêu", "Thu nhập"};

        setupHeaderTabs(view, homeTabs, initialTab, index -> {
            handleTabSwitch(index);
        });

        observeViewModel();
        observeDashboardData(view);

        homeViewModel.fetchDashboardOverview();
        observeBffDashboard(view);

        // Khởi tạo repository truy cập các giao dịch nháp chờ duyệt
        pendingRepository = new PendingTransactionRepository(requireActivity().getApplication());

        // Ánh xạ các view liên quan đến Banner báo giao dịch nháp
        cardPendingBanner = view.findViewById(R.id.card_pending_banner);
        tvPendingBannerText = view.findViewById(R.id.tv_pending_banner_text);

        // Thiết lập sự kiện click vào banner để mở màn hình duyệt
        if (cardPendingBanner != null) {
            cardPendingBanner.setOnClickListener(v ->
                    Navigation.findNavController(v).navigate(R.id.pendingTransactionsFragment)
            );
        }
    }

    private void handleTabSwitch(int index) {
        PreferenceManager.getInstance(requireContext()).setLastHomeTab(index);
        CollapsingToolbarLayout collapsingToolbar = requireView().findViewById(R.id.collapsing_toolbar);

        View timeSelector = requireView().findViewById(R.id.time_selector);
        View menuTopHalf = requireView().findViewById(R.id.menu_top_half);
        View menuBottomHalf = requireView().findViewById(R.id.menu_bottom_half);

        updateCollapsingHeights(index);

        if (index == 0) { // TAB TỔNG QUAN
            layoutDashboardOverview.setVisibility(View.VISIBLE);
            rvCategories.setVisibility(View.GONE);

            pieChartContainer.setVisibility(View.GONE);
            linearChartContainer.setVisibility(View.GONE);
            if (timeSelector != null) timeSelector.setVisibility(View.GONE);

            if (menuTopHalf != null) menuTopHalf.setVisibility(View.VISIBLE);
            if (menuBottomHalf != null) menuBottomHalf.setVisibility(View.VISIBLE);

            appBarLayout.setExpanded(true, false);
            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) collapsingToolbar.getLayoutParams();
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
            collapsingToolbar.setLayoutParams(params);

            homeViewModel.setTabTypeAndReload(0);
        } else { // TAB CHI TIÊU / THU NHẬP
            layoutDashboardOverview.setVisibility(View.GONE);
            rvCategories.setVisibility(View.VISIBLE);

            pieChartContainer.setVisibility(View.VISIBLE);
            linearChartContainer.setVisibility(View.INVISIBLE);
            if (timeSelector != null) timeSelector.setVisibility(View.VISIBLE);

            if (menuTopHalf != null) menuTopHalf.setVisibility(View.GONE);
            if (menuBottomHalf != null) menuBottomHalf.setVisibility(View.GONE);

            appBarLayout.setExpanded(true, true);
            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) collapsingToolbar.getLayoutParams();
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
            collapsingToolbar.setLayoutParams(params);

            int vmIndex = (index == 1) ? 0 : 1;
            PreferenceManager.getInstance(requireContext()).setLastTabType(vmIndex);
            homeViewModel.setTabTypeAndReload(vmIndex);
        }
    }

    private void updateCollapsingHeights(int index) {
        if (getView() == null || chartsWrapper == null) return;

        View topSlice = getView().findViewById(R.id.top_slice);
        CollapsingToolbarLayout collapsingToolbar = getView().findViewById(R.id.collapsing_toolbar);
        if (topSlice == null || collapsingToolbar == null) return;

        int topHeight = topSlice.getHeight();
        ViewGroup.LayoutParams params = chartsWrapper.getLayoutParams();
        int gapForBorder = dpToPx(24);

        if (index == 0) {
            params.height = dpToPx(110);
            chartsWrapper.setLayoutParams(params);

            collapsingToolbar.setMinimumHeight(topHeight + gapForBorder);
        } else {
            params.height = dpToPx(250);
            chartsWrapper.setLayoutParams(params);

            int linearHeight = (linearChartContainer != null && linearChartContainer.getVisibility() == View.VISIBLE)
                    ? linearChartContainer.getHeight() : dpToPx(80);

            collapsingToolbar.setMinimumHeight(topHeight + linearHeight + gapForBorder);
        }
    }

    private void observeViewModel() {
        homeViewModel.getTotalBalance().observe(getViewLifecycleOwner(), balance -> {
            String displayBalance = "0";
            String currencyStr = PreferenceManager.getInstance(requireContext()).getDefaultCurrency();
            if (balance != null) {
                String format = "%,.0f " + (currencyStr != null ? currencyStr : "VND");
                displayBalance = String.format(Locale.getDefault(), format, balance);

                displayBalance = displayBalance.replace(",", ".");
            } else {
                displayBalance = "0 " + (currencyStr != null ? currencyStr : "VND");
            }
            setupBalanceSelector(requireView(), getString(R.string.total_balance), displayBalance, false, null, null, null, null);
        });

        questViewModel.getQuests().observe(getViewLifecycleOwner(), quests -> {
            if (quests != null && tvMenuQuestStatus != null) {
                int completedCount = 0;
                for (QuestResponse q : quests) {
                    if (q.isCompleted() && !q.isClaimed()) completedCount++;
                }

                tvMenuQuestStatus.setText("Nhiệm vụ (" + completedCount + ")");

                if (completedCount > 0) {
                    tvMenuQuestStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorDanger));
                } else {
                    tvMenuQuestStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorOnSurface));
                }
            }
        });
        questViewModel.fetchQuests();

        homeViewModel.getCategoryExpenses().observe(getViewLifecycleOwner(), items -> {
            adapter.updateData(items);
            populateCharts(items);
        });

        homeViewModel.getChartTotalAmount().observe(getViewLifecycleOwner(), total -> {
            if (total == null || total == 0) {
                if (tvTotalAmountPie != null) tvTotalAmountPie.setText("0đ");
                if (tvTotalAmountLinear != null) tvTotalAmountLinear.setText("0đ");
            } else {
                String formattedTotal = String.format("%,.0f", total).replace(",", ".");
                if (tvTotalAmountPie != null) tvTotalAmountPie.setText(formattedTotal);
                if (tvTotalAmountLinear != null) tvTotalAmountLinear.setText(formattedTotal);
            }
        });

        homeViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) DialogHelper.showSimpleDialog(requireContext(), "Lỗi", error);
        });
    }

    private void observeDashboardData(View view) {
        TextView tvCityLevel = view.findViewById(R.id.tv_city_level_home);
        TextView tvProsperity = view.findViewById(R.id.tv_prosperity_home);
        TextView tvStability = view.findViewById(R.id.tv_stability_home);

        View cardUserProfile = view.findViewById(R.id.card_user_profile);
        TextView tvUserName = view.findViewById(R.id.tv_user_name);
        TextView tvStreakCount = view.findViewById(R.id.tv_streak_count);
        ImageView ivUserAvatar = view.findViewById(R.id.iv_user_avatar);

        View cardCityStats = view.findViewById(R.id.card_city_stats);
        if (cardCityStats != null) {
            cardCityStats.setOnClickListener(v ->
                    Navigation.findNavController(v).navigate(R.id.cityFragment)
            );
        }

        profileViewModel.fetchUserData();

        // 1. LẮNG NGHE DATA USER & CẬP NHẬT GIAO DIỆN PROFILE (Đã xóa code cũ gây crash)
        profileViewModel.currentUser.observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                if (tvUserName != null) tvUserName.setText(user.getName());
                if (tvStreakCount != null) tvStreakCount.setText(user.getDailyStreak() + " ngày");

                // Tạo avatar mặc định (Icon hình người màu xám)
                com.mikepenz.iconics.IconicsDrawable defaultAvatar = new com.mikepenz.iconics.IconicsDrawable(requireContext(), "gmd-person");
                defaultAvatar.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorOnSurfaceVariant), android.graphics.PorterDuff.Mode.SRC_IN);

                if (ivUserAvatar != null) {
                    if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                        com.bumptech.glide.Glide.with(requireContext())
                                .load(user.getProfileImageUrl())
                                .placeholder(defaultAvatar) // Hiện icon trong lúc chờ mạng tải
                                .error(defaultAvatar)       // Nếu link ảnh lỗi, quay về icon mặc định
                                .circleCrop()               // Ép cắt tròn
                                .into(ivUserAvatar);
                    } else {
                        // Nếu user chưa có ảnh mạng, set icon mặc định
                        ivUserAvatar.setImageDrawable(defaultAvatar);
                    }
                }
            }
        });

        // 2. CHUYỂN TRANG PROFILE
        if (cardUserProfile != null) {
            cardUserProfile.setOnClickListener(v ->
                    Navigation.findNavController(v).navigate(R.id.profileFragment)
            );
        }

        // 3. LẮNG NGHE DATA CITY
        profileViewModel.cityData.observe(getViewLifecycleOwner(), city -> {
            if (city != null && tvCityLevel != null) {
                tvCityLevel.setText("Cấp " + city.getLevel());
                tvProsperity.setText(String.valueOf(city.getProsperityPoints()));
                tvStability.setText(String.valueOf(city.getStabilityPoints()));
            }
        });
    }

    private void observeBffDashboard(View view) {
        MaterialCardView cardSmartInsights = view.findViewById(R.id.card_smart_insights);
        IconicsImageView ivInsightIcon = view.findViewById(R.id.iv_insight_icon);
        TextView tvInsightTitle = view.findViewById(R.id.tv_insight_title);
        TextView tvInsightMessage = view.findViewById(R.id.tv_insight_message);

        View layoutRecent = view.findViewById(R.id.layout_recent_transactions);
        LinearLayout containerRecent = view.findViewById(R.id.container_recent_items);

        View layoutBudget = view.findViewById(R.id.layout_budget_alerts);
        LinearLayout containerBudget = view.findViewById(R.id.container_budget_alerts);

        View layoutQuests = view.findViewById(R.id.layout_pending_quests);
        LinearLayout containerQuests = view.findViewById(R.id.container_pending_quests);
        View layoutGoals = view.findViewById(R.id.layout_goal_highlights);
        LinearLayout containerGoals = view.findViewById(R.id.container_goal_highlights);

        View headerRecent = view.findViewById(R.id.header_recent_transactions);
        if (headerRecent != null) {
            headerRecent.setOnClickListener(v ->
                    Navigation.findNavController(v).navigate(R.id.historyFragment) // Sang trang Lịch sử
            );
        }

        View headerBudget = view.findViewById(R.id.header_budget_alerts);
        if (headerBudget != null) {
            headerBudget.setOnClickListener(v ->
                    Navigation.findNavController(v).navigate(R.id.budgetFragment) // Sang trang Ngân sách
            );
        }

        View headerQuests = view.findViewById(R.id.header_pending_quests);
        if (headerQuests != null) {
            headerQuests.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.questFragment));
        }

        View headerGoals = view.findViewById(R.id.header_goal_highlights);
        if (headerGoals != null) {
            headerGoals.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.goalFragment));
        }

        homeViewModel.getDashboardOverview().observe(getViewLifecycleOwner(), overview -> {
            if (overview == null) return;

            // 1. XỬ LÝ SMART INSIGHTS
            if (overview.getSmartInsights() != null && !overview.getSmartInsights().isEmpty()) {
                cardSmartInsights.setVisibility(View.VISIBLE);
                com.example.moneyapp.data.remote.response.DashboardOverviewResponse.SmartInsight insight = overview.getSmartInsights().get(0);

                tvInsightTitle.setText(insight.getTitle());
                tvInsightMessage.setText(insight.getMessage());

                int colorRes = R.color.colorInfo;
                String iconName = "gmd_lightbulb";

                if ("DANGER".equals(insight.getType())) {
                    colorRes = R.color.colorDanger;
                    iconName = "gmd_warning";
                } else if ("SUCCESS".equals(insight.getType())) {
                    colorRes = R.color.colorSuccess;
                    iconName = "gmd_check_circle";
                }

                int actualColor = ContextCompat.getColor(requireContext(), colorRes);
                cardSmartInsights.setStrokeColor(actualColor); // Hết lỗi nhé!

                ivInsightIcon.setIcon(new com.mikepenz.iconics.IconicsDrawable(requireContext(), iconName));
                ivInsightIcon.setColorFilter(actualColor);
                tvInsightTitle.setTextColor(actualColor);
            } else {
                cardSmartInsights.setVisibility(View.GONE);
            }

            LayoutInflater inflater = LayoutInflater.from(requireContext());

            // 2. XỬ LÝ GIAO DỊCH GẦN ĐÂY (List đơn giản)
            if (overview.getRecentTransactions() != null && !overview.getRecentTransactions().isEmpty()) {
                layoutRecent.setVisibility(View.VISIBLE);
                containerRecent.removeAllViews();

                for (DashboardOverviewResponse.RecentTransaction tx : overview.getRecentTransactions()) {
                    View itemTx = inflater.inflate(R.layout.item_recent_transaction, containerRecent, false);

                    IconicsImageView ivIcon = itemTx.findViewById(R.id.iv_category_icon);
                    TextView tvName = itemTx.findViewById(R.id.tv_category_name);
                    TextView tvAmount = itemTx.findViewById(R.id.tv_amount);

                    ivIcon.setIcon(new IconicsDrawable(requireContext(), AppResourceManager.getIconName(tx.getIconId())));
                    ivIcon.setColorFilter(AppResourceManager.getColor(tx.getColorId()));

                    tvName.setText(tx.getCategoryName());

                    String amountStr = CurrencyFormatter.formatVND(tx.getAmount());
                    String currencyStr = PreferenceManager.getInstance(requireContext()).getDefaultCurrency();
                    if ("EXPENSE".equalsIgnoreCase(tx.getType())) {
                        tvAmount.setText("-" + amountStr + " " + currencyStr);
                        tvAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorDanger));
                    } else {
                        if ("INCOME".equalsIgnoreCase(tx.getType())) {
                            tvAmount.setText("+" + amountStr + " " + currencyStr);
                            tvAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorSuccess));
                        } else {
                            tvAmount.setText(amountStr + " " + currencyStr);
                        }
                    }

                    containerRecent.addView(itemTx);
                }
            } else {
                layoutRecent.setVisibility(View.GONE);
            }

            // 3. XỬ LÝ CẢNH BÁO NGÂN SÁCH (Tái sử dụng thẻ Budget)
            if (overview.getBudgetAlerts() != null && !overview.getBudgetAlerts().isEmpty()) {
                layoutBudget.setVisibility(View.VISIBLE);
                containerBudget.removeAllViews();

                for (com.example.moneyapp.data.remote.response.DashboardOverviewResponse.BudgetAlert alert : overview.getBudgetAlerts()) {
                    // Tái sử dụng trọn vẹn item_budget.xml
                    View itemBudget = inflater.inflate(R.layout.item_budget, containerBudget, false);

                    TextView tvCatName = itemBudget.findViewById(R.id.tv_category_name);
                    TextView tvSpentSummary = itemBudget.findViewById(R.id.tv_spent_summary);
                    TextView tvPercentage = itemBudget.findViewById(R.id.tv_percentage);
                    TextView tvCycleInfo = itemBudget.findViewById(R.id.tv_cycle_info);
                    com.google.android.material.progressindicator.LinearProgressIndicator pbBudget = itemBudget.findViewById(R.id.pb_budget);

                    tvCatName.setText(alert.getName());
                    tvCycleInfo.setText("Cảnh báo ngân sách");
                    tvCycleInfo.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorWarning));

                    tvSpentSummary.setText("Đã chi: " + com.example.moneyapp.utils.CurrencyFormatter.formatVND(alert.getUsedAmount()) + " / " + com.example.moneyapp.utils.CurrencyFormatter.formatVND(alert.getAmount()));

                    pbBudget.setProgress(Math.min(alert.getPercent(), 100));

                    int indicatorColor = ContextCompat.getColor(requireContext(),
                            "OVER".equals(alert.getStatus()) ? R.color.colorDanger : R.color.colorWarning);

                    pbBudget.setIndicatorColor(indicatorColor);

                    if ("OVER".equals(alert.getStatus())) {
                        tvPercentage.setText("Vượt mức " + alert.getPercent() + "%");
                        tvPercentage.setTextColor(indicatorColor);
                        tvSpentSummary.setTextColor(indicatorColor);
                    } else {
                        tvPercentage.setText(alert.getPercent() + "%");
                        tvPercentage.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorWarning));
                    }

                    containerBudget.addView(itemBudget);
                }
            } else {
                layoutBudget.setVisibility(View.GONE);
            }

            // 4. XỬ LÝ NHIỆM VỤ ƯU TIÊN
            if (overview.getPendingQuests() != null && !overview.getPendingQuests().isEmpty()) {
                layoutQuests.setVisibility(View.VISIBLE);
                containerQuests.removeAllViews();

                for (com.example.moneyapp.data.remote.response.DashboardOverviewResponse.PendingQuest quest : overview.getPendingQuests()) {
                    // Tận dụng item mini
                    View itemQuest = inflater.inflate(R.layout.item_dashboard_quest, containerQuests, false);
                    TextView tvQuestTitle = itemQuest.findViewById(R.id.tv_quest_title);
                    TextView tvQuestProgress = itemQuest.findViewById(R.id.tv_quest_progress);
                    com.google.android.material.progressindicator.LinearProgressIndicator pbQuest = itemQuest.findViewById(R.id.pb_quest);

                    tvQuestTitle.setText(quest.getTitle());
                    tvQuestProgress.setText(quest.getCurrentProgress() + " / " + quest.getTarget());

                    int progressPercent = quest.getTarget() > 0 ? (int) (((float) quest.getCurrentProgress() / quest.getTarget()) * 100) : 0;
                    pbQuest.setProgress(progressPercent);

                    containerQuests.addView(itemQuest);
                }
            } else {
                layoutQuests.setVisibility(View.GONE);
            }

            // 5. XỬ LÝ MỤC TIÊU TIẾT KIỆM
            if (overview.getGoalHighlights() != null && !overview.getGoalHighlights().isEmpty()) {
                layoutGoals.setVisibility(View.VISIBLE);
                containerGoals.removeAllViews();

                for (com.example.moneyapp.data.remote.response.DashboardOverviewResponse.GoalHighlight goal : overview.getGoalHighlights()) {
                    // Do Mục tiêu có UI hệt như Budget, ta TÁI SỬ DỤNG LẠI item_budget.xml luôn cho lẹ!
                    View itemGoal = inflater.inflate(R.layout.item_budget, containerGoals, false);

                    TextView tvCatName = itemGoal.findViewById(R.id.tv_category_name);
                    TextView tvSpentSummary = itemGoal.findViewById(R.id.tv_spent_summary);
                    TextView tvPercentage = itemGoal.findViewById(R.id.tv_percentage);
                    TextView tvCycleInfo = itemGoal.findViewById(R.id.tv_cycle_info);
                    com.google.android.material.progressindicator.LinearProgressIndicator pbGoal = itemGoal.findViewById(R.id.pb_budget);

                    tvCatName.setText(goal.getName());
                    tvCycleInfo.setText("Đang tiết kiệm");
                    tvCycleInfo.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorSuccess)); // Chữ màu xanh

                    tvSpentSummary.setText(com.example.moneyapp.utils.CurrencyFormatter.formatVND(goal.getCurrentAmount()) + " / " + com.example.moneyapp.utils.CurrencyFormatter.formatVND(goal.getTargetAmount()));

                    pbGoal.setProgress(Math.min(goal.getProgressPercent(), 100));
                    pbGoal.setIndicatorColor(ContextCompat.getColor(requireContext(), R.color.colorSuccess)); // Thanh chạy màu xanh lá

                    tvPercentage.setText(goal.getProgressPercent() + "%");
                    tvPercentage.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorSuccess));

                    containerGoals.addView(itemGoal);
                }
            } else {
                layoutGoals.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Cập nhật lại số lượng giao dịch chờ duyệt khi fragment hiển thị lại (onResume)
        checkPendingTransactions();
    }

    /**
     * Truy vấn số lượng giao dịch chờ duyệt trong Database.
     * Cập nhật ẩn/hiện Banner và số lượng tương ứng lên giao diện.
     */
    private void checkPendingTransactions() {
        if (pendingRepository == null) return;

        pendingRepository.getPendingCount(new PendingTransactionRepository.PendingCountCallback() {
            @Override
            public void onSuccess(int count) {
                // Đảm bảo chạy code giao diện trên Main Thread
                mainHandler.post(() -> {
                    // Kiểm tra null-safety phòng trường hợp Fragment đã hủy view
                    if (getView() == null || cardPendingBanner == null || tvPendingBannerText == null) {
                        return;
                    }

                    if (count > 0) {
                        // Hiển thị banner và thiết lập thông điệp số lượng
                        cardPendingBanner.setVisibility(View.VISIBLE);
                        tvPendingBannerText.setText("Bạn có " + count + " giao dịch chờ duyệt tự động!");
                    } else {
                        // Ẩn banner nếu không có giao dịch nháp nào
                        cardPendingBanner.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onError(String message) {
                // Log lỗi ra hệ thống khi xảy ra sự cố truy vấn
                mainHandler.post(() ->
                        android.util.Log.e("HomeFragment", "Lỗi kiểm tra giao dịch chờ duyệt: " + message)
                );
            }
        });
    }

    private void populateCharts(List<PieChartItem> items) {
        if (items == null || items.isEmpty()) {
            int emptyColor = ContextCompat.getColor(requireContext(), R.color.colorEmpty);
            ArrayList<PieEntry> emptyEntries = new ArrayList<>();
            emptyEntries.add(new PieEntry(100f, ""));
            PieDataSet emptyDataSet = new PieDataSet(emptyEntries, "");
            emptyDataSet.setColor(emptyColor);
            emptyDataSet.setDrawValues(false);

            pieChart.setData(new PieData(emptyDataSet));
            pieChart.animateY(0);
            pieChart.invalidate();

            LinearLayout customLinearChart = requireView().findViewById(R.id.custom_linear_chart);
            if (customLinearChart != null) {
                customLinearChart.removeAllViews();
                View segment = new View(getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                segment.setLayoutParams(params);
                segment.setBackgroundColor(emptyColor);
                customLinearChart.addView(segment);
            }
            return;
        }

        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            PieChartItem item = items.get(i);
            pieEntries.add(new PieEntry(item.getPercentage(), item.getName()));
            colors.add(item.getColor());
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(colors);
        pieDataSet.setDrawValues(false);
        pieDataSet.setSelectionShift(5f);
        pieDataSet.setSliceSpace(4f);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.animateY(0);
        pieChart.invalidate();

        LinearLayout customLinearChart = requireView().findViewById(R.id.custom_linear_chart);
        customLinearChart.removeAllViews();

        for (int i = 0; i < items.size(); i++) {
            PieChartItem item = items.get(i);
            View segment = new View(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, item.getPercentage());
            if (i < items.size() - 1) params.setMarginEnd(dpToPx(4));
            segment.setLayoutParams(params);
            segment.setBackgroundColor(item.getColor());
            customLinearChart.addView(segment);
        }
    }

    private void setupScrollBehavior() {
        View topSlice = requireView().findViewById(R.id.top_slice);
        com.google.android.material.appbar.CollapsingToolbarLayout collapsingToolbar = requireView().findViewById(R.id.collapsing_toolbar);

        View menuTopExpanded = requireView().findViewById(R.id.menu_top_expanded);
        View menuTopCollapsed = requireView().findViewById(R.id.menu_top_collapsed);
        View menuBottomHalf = requireView().findViewById(R.id.menu_bottom_half);

        chartsWrapper.post(() -> {
            if (topSlice == null || linearChartContainer == null) return;

            int topHeight = topSlice.getHeight();

            android.widget.FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) chartsWrapper.getLayoutParams();
            params.topMargin = topHeight;
            chartsWrapper.setLayoutParams(params);

            int currentTab = PreferenceManager.getInstance(requireContext()).getLastHomeTab();
            updateCollapsingHeights(currentTab);

            if (appBarLayout != null) {
                linearChartContainer.setAlpha(0f);
                linearChartContainer.setVisibility(View.INVISIBLE);
                pieChartContainer.setAlpha(1f);
                pieChartContainer.setVisibility(View.VISIBLE);

                if (menuTopExpanded != null) {
                    menuTopExpanded.setAlpha(1f);
                    menuTopExpanded.setVisibility(View.VISIBLE);
                }
                if (menuTopCollapsed != null) {
                    menuTopCollapsed.setAlpha(0f);
                    menuTopCollapsed.setVisibility(View.INVISIBLE);
                }
                if (menuBottomHalf != null) {
                    menuBottomHalf.setAlpha(1f);
                    menuBottomHalf.setVisibility(View.VISIBLE);
                }

                appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {
                    int totalScrollRange = appBarLayout1.getTotalScrollRange();
                    if (totalScrollRange == 0) return;

                    float percentage = (float) Math.abs(verticalOffset) / totalScrollRange;

                    float expandedAlpha = Math.max(0f, 1f - (percentage * 2.5f));
                    float expandedScale = 0.8f + (0.2f * expandedAlpha);

                    float collapsedAlpha = Math.max(0f, Math.min(1f, (percentage - 0.5f) * 2f));
                    float translateY = 30f * (1f - collapsedAlpha);

                    if (layoutDashboardOverview != null && layoutDashboardOverview.getVisibility() == View.VISIBLE) {

                        pieChartContainer.setVisibility(View.GONE);
                        linearChartContainer.setVisibility(View.GONE);

                        if (menuTopExpanded != null) {
                            menuTopExpanded.setAlpha(expandedAlpha);
                            menuTopExpanded.setVisibility(expandedAlpha <= 0f ? View.INVISIBLE : View.VISIBLE);
                        }
                        if (menuBottomHalf != null) {
                            menuBottomHalf.setAlpha(expandedAlpha);
                            menuBottomHalf.setVisibility(expandedAlpha <= 0f ? View.INVISIBLE : View.VISIBLE);
                        }
                        if (menuTopCollapsed != null) {
                            menuTopCollapsed.setAlpha(collapsedAlpha);
                            menuTopCollapsed.setVisibility(collapsedAlpha > 0f ? View.VISIBLE : View.INVISIBLE);
                        }
                    } else {
                        if (menuTopExpanded != null) menuTopExpanded.setVisibility(View.GONE);
                        if (menuBottomHalf != null) menuBottomHalf.setVisibility(View.GONE);
                        if (menuTopCollapsed != null) menuTopCollapsed.setVisibility(View.GONE);

                        pieChartContainer.setAlpha(expandedAlpha);
                        pieChartContainer.setScaleX(expandedScale);
                        pieChartContainer.setScaleY(expandedScale);
                        pieChartContainer.setVisibility(expandedAlpha <= 0f ? View.INVISIBLE : View.VISIBLE);

                        linearChartContainer.setAlpha(collapsedAlpha);
                        linearChartContainer.setTranslationY(translateY);
                        linearChartContainer.setVisibility(collapsedAlpha > 0f ? View.VISIBLE : View.INVISIBLE);
                    }
                });
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new CategorySummaryAdapter(new ArrayList<>());
        adapter.setOnCategoryClickListener((categoryId, categoryName) -> {
            Bundle bundle = new Bundle();
            bundle.putInt("tabType", isExpenseTab ? 1 : 2);
            bundle.putString("categoryId", categoryId);
            bundle.putString("categoryName", categoryName);
            if (currentStartDate != null) bundle.putLong("startDate", currentStartDate.getTime());
            if (currentEndDate != null) bundle.putLong("endDate", currentEndDate.getTime());

            NavOptions fadeOptions = new NavOptions.Builder().setEnterAnim(R.anim.fade_in).setExitAnim(R.anim.fade_out).setPopEnterAnim(R.anim.fade_in).setPopExitAnim(R.anim.fade_out).build();
            Navigation.findNavController(requireView()).navigate(R.id.historyFragment, bundle, fadeOptions);
        });
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCategories.setAdapter(adapter);
    }

    private void setupPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(70f);
        pieChart.setTransparentCircleRadius(55f);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setDrawEntryLabels(false);
        pieChart.setDrawCenterText(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.animate().cancel();
        pieChart.setExtraOffsets(10f, 10f, 10f, 10f);
    }

    @Override
    protected void onFabClick() {
        NavOptions navOptions = new NavOptions.Builder().setEnterAnim(R.anim.slide_in_right).setExitAnim(R.anim.slide_out_left).setPopEnterAnim(R.anim.slide_in_left).setPopExitAnim(R.anim.slide_out_right).build();
        Navigation.findNavController(requireView()).navigate(R.id.transactionEntryFragment, null, navOptions);
    }

    @Override
    protected String getFabLabel() {
        return "Thêm giao dịch";
    }

    private int dpToPx(int dp) {
        return Math.round((float) dp * getResources().getDisplayMetrics().density);
    }
}