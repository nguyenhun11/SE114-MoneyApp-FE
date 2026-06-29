package com.example.moneyapp.view.transaction;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.moneyapp.R;
import com.example.moneyapp.data.local.PreferenceManager;
import com.example.moneyapp.model.CategoryType;
import com.example.moneyapp.model.Mood;
import com.example.moneyapp.utils.AppResourceManager;
import com.example.moneyapp.utils.CurrencyFormatter;
import com.example.moneyapp.utils.DialogHelper;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.viewmodel.TransactionViewModel;
import com.mikepenz.iconics.view.IconicsImageView;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TransactionDetailFragment extends BaseFragment {

    private TransactionViewModel transactionViewModel;
    private String currentTransactionId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        Bundle args = getArguments();
        if (args != null && args.containsKey("transactionId")) {
            currentTransactionId = args.getString("transactionId");
            setupHeader(view,
                    "Chi tiết giao dịch",
                    "gmd_navigate_before", v -> Navigation.findNavController(v).navigateUp(),
                    "gmd_delete_outline", v -> showDeleteConfirmDialog());

            observeViewModel(view);
        } else {
            DialogHelper.showSimpleDialog(requireContext(), "Lỗi", "Không tìm thấy mã giao dịch", () -> {
                Navigation.findNavController(view).navigateUp();
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (currentTransactionId != null) {
            transactionViewModel.loadTransactionById(currentTransactionId);
        }
    }

    private void showDeleteConfirmDialog() {
        DialogHelper.showConfirmDialog(requireContext(),
                "Xóa giao dịch",
                "Bạn có chắc chắn muốn xóa giao dịch này không? Hành động này không thể hoàn tác.",
                () -> transactionViewModel.deleteTransaction(currentTransactionId),
                null);
    }

    private void observeViewModel(View view) {
        com.facebook.shimmer.ShimmerFrameLayout shimmerDetail = view.findViewById(R.id.shimmer_detail);
        View layoutContent = view.findViewById(R.id.layout_content);

        transactionViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                if (shimmerDetail != null) {
                    shimmerDetail.setVisibility(View.VISIBLE);
                    shimmerDetail.startShimmer();
                }
                if (layoutContent != null) layoutContent.setVisibility(View.GONE);
            } else {
                if (shimmerDetail != null) {
                    shimmerDetail.stopShimmer();
                    shimmerDetail.setVisibility(View.GONE);
                }
                if (layoutContent != null) layoutContent.setVisibility(View.VISIBLE);
            }
        });

        transactionViewModel.getSelectedTransaction().observe(
                getViewLifecycleOwner(),
                t -> {
                    if (t == null) return;

                    FrameLayout flCategoryIcon = view.findViewById(R.id.fl_category_icon);
                    IconicsImageView ivCategoryIcon = view.findViewById(R.id.iv_category_icon);
                    FrameLayout flAccountIcon = view.findViewById(R.id.fl_account_icon);
                    IconicsImageView ivAccountIcon = view.findViewById(R.id.iv_account_icon);

                    TextView tvCategory = view.findViewById(R.id.tvDetailCategoryLabel);
                    TextView tvSource = view.findViewById(R.id.tvDetailSource);
                    TextView tvAmount = view.findViewById(R.id.tvDetailAmount);

                    TextView tvBaseAmountDetail = view.findViewById(R.id.tvBaseAmountDetail);

                    TextView tvDate = view.findViewById(R.id.tvDetailDate);
                    TextView tvDescription = view.findViewById(R.id.tvDetailDescription);
                    TextView tvCreatedAt = view.findViewById(R.id.tvCreatedAt);
                    TextView tvMood = view.findViewById(R.id.tvDetailMood);
                    View moodContainer = view.findViewById(R.id.ll_mood_container);

                    tvCategory.setText(t.getCategoryName() != null ? t.getCategoryName() : "Hạng mục");
                    tvSource.setText(t.getAccountName() != null ? t.getAccountName() : "Ví");
                    tvDate.setText(t.getFormattedDate());

                    moodContainer.setVisibility(View.VISIBLE);
                    tvMood.setText(String.format("%s %s", Mood.getEmojiById(t.getMoodId()), Mood.getNameById(t.getMoodId())));

                    View noteRowContainer = (View) tvDescription.getParent();
                    if (t.getNote() != null && !t.getNote().trim().isEmpty()) {
                        tvDescription.setText(t.getNote());
                        noteRowContainer.setVisibility(View.VISIBLE);
                    } else {
                        noteRowContainer.setVisibility(View.GONE);
                    }

                    if (t.getCreatedAt() != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault());
                        tvCreatedAt.setText(String.format("Tạo lúc: %s", sdf.format(t.getCreatedAt())));
                    } else {
                        tvCreatedAt.setText("");
                    }

                    String transactionCurrency = t.getCurrencyCode() != null ? t.getCurrencyCode() : "VND";
                    String systemCurrency = PreferenceManager.getInstance(requireContext()).getDefaultCurrency();

                    String sign = (t.getType() == CategoryType.EXPENSE) ? "-" : "+";
                    int colorRes = (t.getType() == CategoryType.EXPENSE) ? R.color.colorDanger : R.color.colorSuccess;

                    String formattedOriginal = CurrencyFormatter.formatVND(t.getOriginalAmount());
                    tvAmount.setText(String.format("%s %s %s", sign, formattedOriginal, transactionCurrency));
                    tvAmount.setTextColor(ContextCompat.getColor(requireContext(), colorRes));

                    if (tvBaseAmountDetail != null) {
                        if (!transactionCurrency.equalsIgnoreCase(systemCurrency)) {
                            tvBaseAmountDetail.setVisibility(View.VISIBLE);
                            String formattedBase = CurrencyFormatter.formatVND(t.getBaseAmount());
                            tvBaseAmountDetail.setText(String.format("≈ %s %s %s", sign, formattedBase, systemCurrency));
                        } else {
                            tvBaseAmountDetail.setVisibility(View.GONE);
                        }
                    }

                    Context context = view.getContext();
                    int accountColor = AppResourceManager.getColor(t.getAccountColorId());
                    int categoryColor = AppResourceManager.getColor(t.getCategoryColorId());

                    ivCategoryIcon.setImageDrawable(AppResourceManager.getWhiteIcon(context, t.getCategoryIconId()));
                    ivAccountIcon.setImageDrawable(AppResourceManager.getWhiteIcon(context, t.getAccountIconId()));

                    flCategoryIcon.setBackgroundTintList(ColorStateList.valueOf(categoryColor));
                    flAccountIcon.setBackgroundTintList(ColorStateList.valueOf(accountColor));
                });

        transactionViewModel.getOperationSuccess().observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                DialogHelper.showSimpleDialog(requireContext(), "Thành công", "Đã xóa giao dịch thành công", () -> {
                    Navigation.findNavController(requireView()).navigateUp();
                });
            }
        });

        transactionViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                DialogHelper.showSimpleDialog(requireContext(), "Lỗi", error);
            }
        });
    }

    @Override
    protected boolean shouldShowBottomNavigation() {
        return false;
    }

    @Override
    protected String getFabIcon() {
        return "gmd_edit";
    }
    @Override
    protected String getFabLabel() {
        return "Chỉnh sửa giao dịch";
    }

    @Override
    protected void onFabClick() {
        if (currentTransactionId != null) {
            Bundle args = new Bundle();
            args.putString("transactionId", currentTransactionId);
            Navigation.findNavController(requireView()).navigate(R.id.transactionEntryFragment, args);
        }
    }
}
