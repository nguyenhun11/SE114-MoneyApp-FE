package com.example.moneyapp.view.home;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import com.example.moneyapp.data.repository.PendingTransactionRepository;
import android.os.Handler;
import android.os.Looper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.data.local.PreferenceManager;
import com.example.moneyapp.data.remote.response.QuestResponse;
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
        tvMenuQuestStatus = view.findViewById(R.id.tv_menu_quests_status);

        View btnMenuStat = view.findViewById(R.id.btn_menu_statistics);
        View btnMenuBudget = view.findViewById(R.id.btn_menu_budget);
        View btnMenuGoals = view.findViewById(R.id.btn_menu_goals);
        View btnMenuCity = view.findViewById(R.id.btn_menu_city);
        View btnMenuQuests = view.findViewById(R.id.btn_menu_quests);
        View btnMenuSettings = view.findViewById(R.id.btn_menu_settings);

        NavOptions slideOptions = new NavOptions.Builder()
                .setEnterAnim(R.anim.slide_in_right).setExitAnim(R.anim.slide_out_left)
                .setPopEnterAnim(R.anim.slide_in_left).setPopExitAnim(R.anim.slide_out_right).build();

        if (btnMenuStat != null) btnMenuStat.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.statisticsFragment, null, slideOptions));
        if (btnMenuBudget != null) btnMenuBudget.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.budgetFragment, null, slideOptions));
        if (btnMenuGoals != null) btnMenuGoals.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.goalFragment, null, slideOptions));
        if (btnMenuCity != null) btnMenuCity.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.cityFragment, null, slideOptions));
        if (btnMenuQuests != null) btnMenuQuests.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.questFragment, null, slideOptions));
        if (btnMenuSettings != null) btnMenuSettings.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.settingsFragment, null, slideOptions));

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

        View cardTopSlice = requireView().findViewById(R.id.card_top_slice);

        if (index == 0) {
            layoutDashboardOverview.setVisibility(View.VISIBLE);
            rvCategories.setVisibility(View.GONE);

            chartsWrapper.setVisibility(View.GONE);

            cardTopSlice.setVisibility(View.INVISIBLE);

            appBarLayout.setExpanded(true, false);
            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) collapsingToolbar.getLayoutParams();
            params.setScrollFlags(0); // Khóa cuộn
            collapsingToolbar.setLayoutParams(params);

            homeViewModel.setTabTypeAndReload(0);

        } else {
            layoutDashboardOverview.setVisibility(View.GONE);
            rvCategories.setVisibility(View.VISIBLE);

            chartsWrapper.setVisibility(View.VISIBLE);

            cardTopSlice.setVisibility(View.VISIBLE);

            appBarLayout.setExpanded(true, true);
            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) collapsingToolbar.getLayoutParams();
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
            collapsingToolbar.setLayoutParams(params);

            int vmIndex = (index == 1) ? 0 : 1;
            PreferenceManager.getInstance(requireContext()).setLastTabType(vmIndex);
            homeViewModel.setTabTypeAndReload(vmIndex);
        }
    }

    private void observeViewModel() {
        homeViewModel.getTotalBalance().observe(getViewLifecycleOwner(), balance -> {
            String displayBalance = "0";
            if (balance != null) {
                displayBalance = String.format(Locale.getDefault(), "%,.0f đ", balance).replace(",", ".");
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

                if(completedCount > 0) {
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

        TextView tvStreakCount = view.findViewById(R.id.tv_streak_count);
        MaterialButton btnCheckin = view.findViewById(R.id.btn_checkin);
        IconicsImageView ivStreakIcon = view.findViewById(R.id.iv_streak_icon);
        TextView tvRestoreStreakHint = view.findViewById(R.id.tv_restore_streak_hint);

        profileViewModel.fetchUserData();

        profileViewModel.cityData.observe(getViewLifecycleOwner(), city -> {
            if (city != null && tvCityLevel != null) {
                tvCityLevel.setText("Cấp " + city.getLevel());
                tvProsperity.setText(String.valueOf(city.getProsperityPoints()));
                tvStability.setText(String.valueOf(city.getStabilityPoints()));
            }
        });

        profileViewModel.currentUser.observe(getViewLifecycleOwner(), user -> {
            if (user != null && tvStreakCount != null) {
                tvStreakCount.setText(user.getDailyStreak() + " ngày");

                if (user.isTodayCheckedIn()) {
                    btnCheckin.setVisibility(View.GONE);
                    ivStreakIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorDanger));
                } else {
                    btnCheckin.setVisibility(View.VISIBLE);
                    btnCheckin.setText("Check-in");
                    btnCheckin.setEnabled(true);
                    btnCheckin.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.colorWarning));
                    btnCheckin.setIcon(null);
                    ivStreakIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorOnSurfaceVariant));
                }
                tvRestoreStreakHint.setVisibility(user.getDailyStreak() == 0 ? View.VISIBLE : View.GONE);
            }
        });

        if (btnCheckin != null) {
            btnCheckin.setOnClickListener(v -> {
                btnCheckin.setEnabled(false);
                profileViewModel.checkInToday();
            });
        }

        if (tvRestoreStreakHint != null) {
            tvRestoreStreakHint.setOnClickListener(v -> {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hãy tải MoneyApp để quản lý tài chính thông minh nhé!");
                sendIntent.setType("text/plain");

                Intent broadcastIntent = new Intent("com.example.moneyapp.SHARE_SUCCESS");
                broadcastIntent.setPackage(requireContext().getPackageName());

                PendingIntent pi = PendingIntent.getBroadcast(
                        requireContext(), 0, broadcastIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
                );

                Intent shareIntent = Intent.createChooser(sendIntent, "Khôi phục chuỗi qua...", pi.getIntentSender());
                startActivity(shareIntent);
            });
        }
    }

    private void observeBffDashboard(View view) {
        // ĐÃ FIX LỖI: Ép kiểu rõ ràng về MaterialCardView để có hàm setStrokeColor
        com.google.android.material.card.MaterialCardView cardSmartInsights = view.findViewById(R.id.card_smart_insights);
        IconicsImageView ivInsightIcon = view.findViewById(R.id.iv_insight_icon);
        TextView tvInsightTitle = view.findViewById(R.id.tv_insight_title);
        TextView tvInsightMessage = view.findViewById(R.id.tv_insight_message);

        View layoutRecent = view.findViewById(R.id.layout_recent_transactions);
        LinearLayout containerRecent = view.findViewById(R.id.container_recent_items);

        View layoutBudget = view.findViewById(R.id.layout_budget_alerts);
        LinearLayout containerBudget = view.findViewById(R.id.container_budget_alerts);

        homeViewModel.getDashboardOverview().observe(getViewLifecycleOwner(), overview -> {
            if (overview == null) return;

            // ==========================================
            // 1. XỬ LÝ SMART INSIGHTS
            // ==========================================
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

            // ==========================================
            // 2. XỬ LÝ GIAO DỊCH GẦN ĐÂY (List đơn giản)
            // ==========================================
            if (overview.getRecentTransactions() != null && !overview.getRecentTransactions().isEmpty()) {
                layoutRecent.setVisibility(View.VISIBLE);
                containerRecent.removeAllViews();

                for (com.example.moneyapp.data.remote.response.DashboardOverviewResponse.RecentTransaction tx : overview.getRecentTransactions()) {
                    // Load layout mini dành riêng cho giao dịch trên Dashboard
                    View itemTx = inflater.inflate(R.layout.item_recent_transaction, containerRecent, false);

                    IconicsImageView ivIcon = itemTx.findViewById(R.id.iv_category_icon);
                    TextView tvName = itemTx.findViewById(R.id.tv_category_name);
                    TextView tvAmount = itemTx.findViewById(R.id.tv_amount);

                    ivIcon.setIcon(new com.mikepenz.iconics.IconicsDrawable(requireContext(), com.example.moneyapp.utils.AppResourceManager.getIconName(tx.getIconId())));
                    ivIcon.setColorFilter(com.example.moneyapp.utils.AppResourceManager.getColor(tx.getColorId()));

                    tvName.setText(tx.getCategoryName());

                    String amountStr = com.example.moneyapp.utils.CurrencyFormatter.formatVND(tx.getAmount());
                    if ("EXPENSE".equals(tx.getType())) {
                        tvAmount.setText("-" + amountStr);
                        tvAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorDanger));
                    } else {
                        tvAmount.setText("+" + amountStr);
                        tvAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorSuccess));
                    }

                    containerRecent.addView(itemTx);
                }
            } else {
                layoutRecent.setVisibility(View.GONE);
            }

            // ==========================================
            // 3. XỬ LÝ CẢNH BÁO NGÂN SÁCH (Tái sử dụng thẻ Budget)
            // ==========================================
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

        chartsWrapper.post(() -> {
            int topHeight = topSlice.getHeight();
            int linearHeight = linearChartContainer.getHeight();
            int wrapperMarginBottom = 0;
            ViewGroup.LayoutParams wrapParams = chartsWrapper.getLayoutParams();
            if (wrapParams instanceof ViewGroup.MarginLayoutParams) {
                wrapperMarginBottom = ((ViewGroup.MarginLayoutParams) wrapParams).bottomMargin;
            }

            android.widget.FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) chartsWrapper.getLayoutParams();
            params.topMargin = topHeight;
            chartsWrapper.setLayoutParams(params);

            collapsingToolbar.setMinimumHeight(topHeight + linearHeight + wrapperMarginBottom);

            if (appBarLayout != null) {
                linearChartContainer.setAlpha(0f);
                linearChartContainer.setVisibility(View.INVISIBLE);
                pieChartContainer.setAlpha(1f);
                pieChartContainer.setScaleX(1f);
                pieChartContainer.setScaleY(1f);
                pieChartContainer.setVisibility(View.VISIBLE);

                appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
                    int totalScrollRange = appBarLayout.getTotalScrollRange();
                    if (totalScrollRange == 0) return;

                    float percentage = (float) Math.abs(verticalOffset) / totalScrollRange;
                    float pieAlpha = Math.max(0f, 1f - (percentage * 2.5f));
                    pieChartContainer.setAlpha(pieAlpha);
                    pieChartContainer.setScaleX(0.8f + (0.2f * pieAlpha));
                    pieChartContainer.setScaleY(0.8f + (0.2f * pieAlpha));

                    float linearAlpha = Math.max(0f, Math.min(1f, (percentage - 0.5f) * 2f));
                    linearChartContainer.setAlpha(linearAlpha);
                    linearChartContainer.setTranslationY(30f * (1f - linearAlpha));

                    pieChartContainer.setVisibility(pieAlpha <= 0f ? View.INVISIBLE : View.VISIBLE);
                    linearChartContainer.setVisibility(linearAlpha > 0f ? View.VISIBLE : View.INVISIBLE);
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
    protected String getFabLabel() { return "Thêm giao dịch"; }

    private int dpToPx(int dp) {
        return Math.round((float) dp * getResources().getDisplayMetrics().density);
    }
}