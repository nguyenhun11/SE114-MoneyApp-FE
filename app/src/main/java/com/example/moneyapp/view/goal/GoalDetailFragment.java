package com.example.moneyapp.view.goal;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.data.local.PreferenceManager;
import com.example.moneyapp.data.remote.response.GoalRecordResponse;
import com.example.moneyapp.model.Account;
import com.example.moneyapp.model.DailyHistoryGroup;
import com.example.moneyapp.model.Goal;
import com.example.moneyapp.model.HistoryItem;
import com.example.moneyapp.utils.AppResourceManager;
import com.example.moneyapp.utils.CurrencyFormatter;
import com.example.moneyapp.utils.DateConverter;
import com.example.moneyapp.utils.DialogHelper;
import com.example.moneyapp.utils.RewardHelper;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.view.MainActivity;
import com.example.moneyapp.view.history.HistoryGroupAdapter;
import com.example.moneyapp.viewmodel.AccountViewModel;
import com.example.moneyapp.viewmodel.GoalViewModel;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GoalDetailFragment extends BaseFragment {

    private Goal goal;
    private GoalViewModel viewModel;
    private AccountViewModel accountViewModel;

    // View Expanded
    private TextView tvName, tvDeadline, tvPercent, tvCurrent, tvTarget;
    private FrameLayout flIconContainer;
    private IconicsImageView ivIcon;
    private CircularProgressIndicator cpProgress;

    // View Compact
    private TextView tvNameCompact, tvAmountCompact, tvPercentCompact;
    private FrameLayout flIconContainerCompact;
    private IconicsImageView ivIconCompact;
    private LinearProgressIndicator pbProgressCompact;

    // Layout Controls
    private MaterialButton btnDeposit, btnWithdraw;
    private RecyclerView rvGoalRecords;

    // Khai báo cho Animation giống Profile
    private View goalCardWrapper, topSlice, expandedContent, collapsedContent;

    private HistoryGroupAdapter historyAdapter;
    private List<Account> accountList = new ArrayList<>();
    private boolean isGoalJustCompleted = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(GoalViewModel.class);
        accountViewModel = new ViewModelProvider(requireActivity()).get(AccountViewModel.class);
        if (getArguments() != null) goal = (Goal) getArguments().getSerializable("goal");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_goal_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).getUiHandler().setBottomNavigationVisibility(false);
            ((MainActivity) getActivity()).getUiHandler().setFABVisibility(false);
        }

        // Ánh xạ Mở Rộng
        tvName = view.findViewById(R.id.tv_goal_name);
        tvDeadline = view.findViewById(R.id.tv_goal_deadline);
        flIconContainer = view.findViewById(R.id.fl_icon_container);
        ivIcon = view.findViewById(R.id.iv_goal_icon);
        tvPercent = view.findViewById(R.id.tv_percent);
        tvCurrent = view.findViewById(R.id.tv_current_amount);
        tvTarget = view.findViewById(R.id.tv_target_amount);
        cpProgress = view.findViewById(R.id.cp_progress);

        // Ánh xạ Thu Gọn
        tvNameCompact = view.findViewById(R.id.tv_goal_name_compact);
        tvAmountCompact = view.findViewById(R.id.tv_goal_amount_compact);
        tvPercentCompact = view.findViewById(R.id.tv_goal_percent_compact);
        pbProgressCompact = view.findViewById(R.id.pb_goal_progress_compact);
        flIconContainerCompact = view.findViewById(R.id.fl_icon_container_compact);
        ivIconCompact = view.findViewById(R.id.iv_goal_icon_compact);

        // Nút & Layout
        btnDeposit = view.findViewById(R.id.btn_deposit);
        btnWithdraw = view.findViewById(R.id.btn_withdraw);
        rvGoalRecords = view.findViewById(R.id.rv_goal_records);

        goalCardWrapper = view.findViewById(R.id.goal_card_wrapper);
        topSlice = view.findViewById(R.id.top_slice);
        expandedContent = view.findViewById(R.id.expanded_content);
        collapsedContent = view.findViewById(R.id.collapsed_content);

        setupHeader(view, "Chi tiết mục tiêu", "gmd_arrow_back", v -> Navigation.findNavController(v).navigateUp(), "gmd_edit", v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("goal", goal);
            Navigation.findNavController(v).navigate(R.id.goalAddFragment, bundle);
        });

        btnDeposit.setOnClickListener(v -> navigateToTransaction(GoalTransactionFragment.TYPE_DEPOSIT));
        btnWithdraw.setOnClickListener(v -> navigateToTransaction(GoalTransactionFragment.TYPE_WITHDRAW));

        setupScrollBehavior(view);
        setupRecyclerView();
        displayGoal();

        observeViewModel();
        accountViewModel.loadAccounts();
        viewModel.fetchGoals();
        if (goal != null) viewModel.fetchGoalRecords(goal.getId());
    }

    private void setupScrollBehavior(View view) {
        AppBarLayout appBarLayout = view.findViewById(R.id.app_bar_layout);
        com.google.android.material.appbar.CollapsingToolbarLayout collapsingToolbar = view.findViewById(R.id.collapsing_toolbar);

        goalCardWrapper.post(() -> {
            if (!isAdded()) return;
            int topHeight = topSlice.getHeight();

            // Đo chiều cao thật của khối Collapsed khi nó đang tàng hình
            collapsedContent.measure(
                    View.MeasureSpec.makeMeasureSpec(goalCardWrapper.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );
            int collapsedHeight = collapsedContent.getMeasuredHeight();

            // Lấy khoảng cách margin (khoảng 12dp)
            int wrapperMarginBottom = getResources().getDimensionPixelSize(R.dimen.card_horizontal_margin);

            // Set Margin để Card nằm lọt dưới Header Tím
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) goalCardWrapper.getLayoutParams();
            params.topMargin = topHeight + 32;
            goalCardWrapper.setLayoutParams(params);

            // CHỐT CHẶN CUỘN
            collapsingToolbar.setMinimumHeight(topHeight + collapsedHeight + wrapperMarginBottom);

            if (appBarLayout != null) {
                // Khởi tạo trạng thái ban đầu
                collapsedContent.setAlpha(0f);
                collapsedContent.setTranslationY(30f);
                collapsedContent.setVisibility(View.INVISIBLE);

                expandedContent.setAlpha(1f);
                expandedContent.setScaleX(1f);
                expandedContent.setScaleY(1f);
                expandedContent.setVisibility(View.VISIBLE);

                final boolean[] isCurrentlyCollapsed = {false};

                appBarLayout.addOnOffsetChangedListener((appBar, verticalOffset) -> {
                    int totalScrollRange = appBar.getTotalScrollRange();
                    if (totalScrollRange == 0) return;

                    float percentage = (float) Math.abs(verticalOffset) / totalScrollRange;
                    boolean shouldCollapse = percentage > 0.5f;

                    // Chỉ kích hoạt Animation khi qua ngưỡng 50%
                    if (shouldCollapse != isCurrentlyCollapsed[0]) {
                        isCurrentlyCollapsed[0] = shouldCollapse;

                        if (shouldCollapse) {
                            // THU GỌN: Ẩn Khối To, Hiện Khối Dạng Dòng
                            expandedContent.animate()
                                    .alpha(0f)
                                    .scaleX(0.8f).scaleY(0.8f)
                                    .setDuration(150)
                                    .withEndAction(() -> expandedContent.setVisibility(View.INVISIBLE))
                                    .start();

                            collapsedContent.setVisibility(View.VISIBLE);
                            collapsedContent.animate()
                                    .alpha(1f)
                                    .translationY(0f)
                                    .setDuration(150)
                                    .start();
                        } else {
                            // MỞ BUNG: Ẩn Khối Dạng Dòng, Hiện Khối To
                            collapsedContent.animate()
                                    .alpha(0f)
                                    .translationY(30f)
                                    .setDuration(150)
                                    .withEndAction(() -> collapsedContent.setVisibility(View.INVISIBLE))
                                    .start();

                            expandedContent.setVisibility(View.VISIBLE);
                            expandedContent.animate()
                                    .alpha(1f)
                                    .scaleX(1f).scaleY(1f)
                                    .setDuration(150)
                                    .start();
                        }
                    }
                });
            }
        });
    }

    private void displayGoal() {
        if (goal == null) return;

        String name = goal.getName();
        String displayDate = getString(R.string.goal_deadline_label, DateConverter.formatToDisplay(goal.getDeadline()));
        int percent = goal.getProgressPercent();
        String percentStr = String.format(Locale.getDefault(), "%d%%", percent);
        int color = AppResourceManager.getColor(goal.getColorId());
        String currentStr = CurrencyFormatter.formatVND(goal.getCurrentAmount());
        String targetStr = CurrencyFormatter.formatVND(goal.getTargetAmount());

        // Đổ data Expanded
        tvName.setText(name);
        tvDeadline.setText(displayDate);
        flIconContainer.setBackgroundTintList(ColorStateList.valueOf(color));
        ivIcon.setImageDrawable(AppResourceManager.getWhiteIcon(requireContext(), goal.getIconId()));
        tvPercent.setText(percentStr);
        tvPercent.setTextColor(color);
        cpProgress.setProgress(percent);
        cpProgress.setIndicatorColor(color);
        tvCurrent.setText(currentStr);
        tvTarget.setText(targetStr);

        // Đổ data Compact
        tvNameCompact.setText(name);
        tvAmountCompact.setText(currentStr + " / " + targetStr + " đ");
        tvPercentCompact.setText(percentStr);
        tvPercentCompact.setTextColor(color);
        pbProgressCompact.setProgress(percent);
        pbProgressCompact.setIndicatorColor(color);
        flIconContainerCompact.setBackgroundTintList(ColorStateList.valueOf(color));
        ivIconCompact.setImageDrawable(AppResourceManager.getWhiteIcon(requireContext(), goal.getIconId()));
    }

    private void setupRecyclerView() {
        String systemCurrency = PreferenceManager.getInstance(requireContext()).getDefaultCurrency();
        historyAdapter = new HistoryGroupAdapter(new ArrayList<>(), accountList, systemCurrency, item -> {
            if (item.getType() == HistoryItem.TYPE_GOAL_RECORD) {
                GoalRecordResponse record = item.getGoalRecord();
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Hủy giao dịch")
                        .setMessage("Bạn có chắc chắn muốn hủy giao dịch này? Số dư sẽ được hoàn trả về ví ban đầu.")
                        .setPositiveButton("Đồng ý", (dialog, which) -> viewModel.deleteGoalRecord(record.getId(), goal.getId()))
                        .setNegativeButton("Đóng", null).show();
            }
        });
        rvGoalRecords.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvGoalRecords.setAdapter(historyAdapter);
        rvGoalRecords.setNestedScrollingEnabled(false);
    }

    private List<DailyHistoryGroup> groupGoalRecordsByDate(List<GoalRecordResponse> records) {
        if (records == null || records.isEmpty()) return new ArrayList<>();
        Collections.sort(records, (r1, r2) -> {
            Date d1 = DateConverter.convertStringToDate(r1.getCreatedAt());
            Date d2 = DateConverter.convertStringToDate(r2.getCreatedAt());
            if (d1 == null || d2 == null) return 0;
            return d2.compareTo(d1);
        });
        Map<String, List<HistoryItem>> groupedMap = new LinkedHashMap<>();
        for (GoalRecordResponse r : records) {
            Date date = DateConverter.convertStringToDate(r.getCreatedAt());
            String dateKey = formatToDisplayDate(date);
            if (!groupedMap.containsKey(dateKey)) groupedMap.put(dateKey, new ArrayList<>());
            groupedMap.get(dateKey).add(new HistoryItem(r));
        }
        List<DailyHistoryGroup> resultList = new ArrayList<>();
        for (Map.Entry<String, List<HistoryItem>> entry : groupedMap.entrySet()) {
            double totalDayAmount = 0;
            for (HistoryItem item : entry.getValue()) {
                GoalRecordResponse r = item.getGoalRecord();
                if ("Deposit".equalsIgnoreCase(r.getType())) totalDayAmount += r.getAmount();
                else totalDayAmount -= r.getAmount();
            }
            String sign = totalDayAmount >= 0 ? "+" : "-";
            String dateSummary = String.format("%s %s", sign, CurrencyFormatter.formatVND(Math.abs(totalDayAmount)));
            resultList.add(new DailyHistoryGroup(entry.getKey(), dateSummary, entry.getValue()));
        }
        return resultList;
    }

    private String formatToDisplayDate(Date date) {
        if (date == null) return "Chưa xác định";
        java.text.DateFormat formatter = java.text.DateFormat.getDateInstance(java.text.DateFormat.LONG, Locale.getDefault());
        return formatter.format(date);
    }

    private void observeViewModel() {
        viewModel.getGoals().observe(getViewLifecycleOwner(), goals -> {
            for (Goal g : goals) {
                if (g.getId() == goal.getId()) {
                    goal = g;
                    displayGoal();
                    break;
                }
            }
        });
        viewModel.getGoalRecords().observe(getViewLifecycleOwner(), records -> {
            if (records != null)
                historyAdapter.updateData(groupGoalRecordsByDate(records), accountList);
        });
        accountViewModel.getAccountsLiveData().observe(getViewLifecycleOwner(), accounts -> {
            if (accounts != null) {
                accountList.clear();
                accountList.addAll(accounts);
                if (viewModel.getGoalRecords().getValue() != null) {
                    historyAdapter.updateData(groupGoalRecordsByDate(viewModel.getGoalRecords().getValue()), accountList);
                }
            }
        });
        viewModel.getIsOperationSuccess().observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess) {
                Toast.makeText(requireContext(), "Giao dịch thành công!", Toast.LENGTH_SHORT).show();
                if (isGoalJustCompleted) {
                    RewardHelper.showBigReward(requireContext(), "+100 PP", "Chúc mừng! Bạn đã hoàn thành mục tiêu '" + goal.getName() + "'.");
                    isGoalJustCompleted = false;
                }
                viewModel.resetOperationStatus();
            }
        });
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) DialogHelper.showSimpleDialog(requireContext(), "Lỗi", error);
        });
    }

    private void navigateToTransaction(String type) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("goal", goal);
        bundle.putString("type", type);
        Navigation.findNavController(requireView()).navigate(R.id.goalTransactionFragment, bundle);
    }
}