package com.example.moneyapp.view.transaction;

import android.app.AlertDialog;
import android.content.Context;
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
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.moneyapp.R;
import com.example.moneyapp.utils.AppResourceManager;
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
            transactionViewModel.loadTransactionById(currentTransactionId);
        } else {
            Toast.makeText(getContext(), "Không tìm thấy mã giao dịch", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).navigateUp();
        }
    }

    private void showDeleteConfirmDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa giao dịch")
                .setMessage("Bạn có chắc chắn muốn xóa giao dịch này không? Hành động này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    transactionViewModel.deleteTransaction(currentTransactionId);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void observeViewModel(View view) {
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
                    TextView tvDate = view.findViewById(R.id.tvDetailDate);
                    TextView tvDescription = view.findViewById(R.id.tvDetailDescription);
                    TextView tvCreatedAt = view.findViewById(R.id.tvCreatedAt);

                    tvCategory.setText(t.getCategoryName() != null ? t.getCategoryName() : "Hạng mục");
                    tvSource.setText(t.getAccountName() != null ? t.getAccountName() : "Ví");
                    tvDate.setText(t.getFormattedDate());

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

                    if (t.getBaseAmount() != null && t.getBaseAmount() < 0) {
                        tvAmount.setText(String.format("%s đ", t.getFormattedAmount()));
                        tvAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorDanger));
                    } else {
                        tvAmount.setText(String.format("+%s đ", t.getFormattedAmount()));
                        tvAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorSuccess));
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
                Toast.makeText(getContext(), "Đã xóa giao dịch", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp();
            }
        });

        transactionViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
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

    // --- XỬ LÝ CHUYỂN SANG MÀN HÌNH SỬA ---
    @Override
    protected void onFabClick() {
        if (currentTransactionId != null) {
            Bundle args = new Bundle();
            args.putString("transactionId", currentTransactionId); // Truyền ID sang AddFragment
            Navigation.findNavController(requireView()).navigate(R.id.addTransactionFragment, args);
        }
    }
}