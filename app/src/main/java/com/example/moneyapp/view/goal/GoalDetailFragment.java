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

    // View Mở rộng
    private TextView tvName, tvDeadline, tvPercent, tvCurrent, tvTarget;
    private FrameLayout flIconContainer;
    private IconicsImageView ivIcon;
    private CircularProgressIndicator cpProgress;

    // View Thu gọn
    private TextView tvNameCompact, tvAmountCompact, tvPercentCompact;
    private FrameLayout flIconContainerCompact;
    private IconicsImageView ivIconCompact;
    private LinearProgressIndicator pbProgressCompact;

    // Layout Controls
    private MaterialButton btnDeposit, btnWithdraw;
    private View cardExpanded, cardCompact;
    private RecyclerView rvGoalRecords;

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

        tvName = view.findViewById(R.id.tv_goal_name);
        tvDeadline = view.findViewById(R.id.tv_goal_deadline);
        flIconContainer = view.findViewById(R.id.fl_icon_container);
        ivIcon = view.findViewById(R.id.iv_goal_icon);
        tvPercent = view.findViewById(R.id.tv_percent);
        tvCurrent = view.findViewById(R.id.tv_current_amount);
        tvTarget = view.findViewById(R.id.tv_target_amount);
        cpProgress = view.findViewById(R.id.cp_progress);

        tvNameCompact = view.findViewById(R.id.tv_goal_name_compact);
        tvAmountCompact = view.findViewById(R.id.tv_goal_amount_compact);
        tvPercentCompact = view.findViewById(R.id.tv_goal_percent_compact);
        pbProgressCompact = view.findViewById(R.id.pb_goal_progress_compact);
        flIconContainerCompact = view.findViewById(R.id.fl_icon_container_compact);
        ivIconCompact = view.findViewById(R.id.iv_goal_icon_compact);

        btnDeposit = view.findViewById(R.id.btn_deposit);
        btnWithdraw = view.findViewById(R.id.btn_withdraw);

        cardExpanded = view.findViewById(R.id.card_expanded);
        cardCompact = view.findViewById(R.id.card_compact);
        rvGoalRecords = view.findViewById(R.id.rv_goal_records);

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
        com.google.android.material.appbar.CollapsingToolbarLayout collapsingToolbar = view.findViewById(R.id.collapsing_toolbar);
        AppBarLayout appBarLayout = view.findViewById(R.id.app_bar_layout);

        cardCompact.post(() -> {
            // LỖI 3 FIX: Tính chính xác Toán học điểm dừng!
            // AppBar chỉ được cuộn lên và dừng đúng tại điểm này:
            // Khoảng cách Lề trên (MarginTop = 70dp) + Chiều cao khối Compact + Lề dưới (MarginBottom = 16dp)
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) cardCompact.getLayoutParams();
            int minHeight = params.topMargin + cardCompact.getHeight() + params.bottomMargin;

            // Set chốt chặn cuộn
            collapsingToolbar.setMinimumHeight(minHeight);
        });

        appBarLayout.addOnOffsetChangedListener((appBar, verticalOffset) -> {
            int totalScrollRange = appBar.getTotalScrollRange();
            if (totalScrollRange == 0) return;

            float percentage = (float) Math.abs(verticalOffset) / totalScrollRange;

            // FADE OUT Thẻ Mở rộng
            float expAlpha = Math.max(0f, 1f - (percentage * 1.5f));
            cardExpanded.setAlpha(expAlpha);
            cardExpanded.setVisibility(expAlpha <= 0f ? View.INVISIBLE : View.VISIBLE);

            // FADE IN Thẻ Thu gọn
            float comAlpha = Math.max(0f, (percentage - 0.4f) * 2.5f);
            cardCompact.setAlpha(comAlpha);
            cardCompact.setVisibility(comAlpha <= 0f ? View.INVISIBLE : View.VISIBLE);
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
            if (records != null) historyAdapter.updateData(groupGoalRecordsByDate(records), accountList);
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