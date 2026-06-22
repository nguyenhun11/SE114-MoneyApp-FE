package com.example.moneyapp.view.transfer;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.moneyapp.R;
import com.example.moneyapp.data.local.PreferenceManager;
import com.example.moneyapp.model.Account;
import com.example.moneyapp.model.Transfer;
import com.example.moneyapp.utils.AppResourceManager;
import com.example.moneyapp.utils.CurrencyFormatter;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.viewmodel.AccountViewModel;
import com.example.moneyapp.viewmodel.TransferViewModel;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransferDetailFragment extends BaseFragment {

    private TransferViewModel transferViewModel;
    private AccountViewModel accountViewModel;
    private String currentTransferId;
    private final List<Account> accountList = new ArrayList<>();
    private Transfer currentTransfer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transfer_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        transferViewModel = new ViewModelProvider(this).get(TransferViewModel.class);
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        Bundle args = getArguments();
        if (args != null && args.containsKey("transferId")) {
            currentTransferId = args.getString("transferId");
            setupHeader(view,
                    "Chi tiết chuyển khoản",
                    "gmd_navigate_before", v -> Navigation.findNavController(v).navigateUp(),
                    "gmd_delete_outline", v -> showDeleteConfirmDialog());

            observeViewModel(view);
            accountViewModel.loadAccounts();
        } else {
            Toast.makeText(getContext(), "Không tìm thấy mã chuyển khoản", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).navigateUp();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (currentTransferId != null) {
            transferViewModel.loadTransferById(currentTransferId);
        }
    }

    private void showDeleteConfirmDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa chuyển khoản")
                .setMessage("Bạn có chắc chắn muốn xóa chuyển khoản này không? Hành động này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    transferViewModel.deleteTransfer(currentTransferId);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void observeViewModel(View view) {
        transferViewModel.getSelectedTransfer().observe(getViewLifecycleOwner(), t -> {
            if (t == null) return;
            currentTransfer = t;
            updateUI(view);
        });

        accountViewModel.getAccountsLiveData().observe(getViewLifecycleOwner(), accounts -> {
            if (accounts != null) {
                accountList.clear();
                accountList.addAll(accounts);
                updateUI(view);
            }
        });

        transferViewModel.getOperationSuccess().observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(getContext(), "Đã xóa chuyển khoản", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp();
            }
        });

        transferViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(View view) {
        if (currentTransfer == null) return;

        TextView tvAmount = view.findViewById(R.id.tvDetailAmount);
        TextView tvBaseAmountDetail = view.findViewById(R.id.tvBaseAmountDetail);

        TextView tvSource = view.findViewById(R.id.tvDetailSource);
        TextView tvDest = view.findViewById(R.id.tvDetailDest);
        TextView tvDate = view.findViewById(R.id.tvDetailDate);
        TextView tvDescription = view.findViewById(R.id.tvDetailDescription);
        TextView tvCreatedAt = view.findViewById(R.id.tvCreatedAt);

        FrameLayout flSourceIcon = view.findViewById(R.id.fl_source_icon);
        IconicsImageView ivSourceIcon = view.findViewById(R.id.iv_source_icon);
        FrameLayout flDestIcon = view.findViewById(R.id.fl_dest_icon);
        IconicsImageView ivDestIcon = view.findViewById(R.id.iv_dest_icon);

        tvSource.setText(currentTransfer.getSourceAccountName() != null ? currentTransfer.getSourceAccountName() : "N/A");
        tvDest.setText(currentTransfer.getDestinationAccountName() != null ? currentTransfer.getDestinationAccountName() : "N/A");

        if (currentTransfer.getDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            tvDate.setText(sdf.format(currentTransfer.getDate()));
        } else {
            tvDate.setText("");
        }

        View descriptionContainer = view.findViewById(R.id.ll_description_container);
        if (currentTransfer.getDescription() != null && !currentTransfer.getDescription().trim().isEmpty()) {
            tvDescription.setText(currentTransfer.getDescription());
            descriptionContainer.setVisibility(View.VISIBLE);
        } else {
            descriptionContainer.setVisibility(View.GONE);
        }

        if (currentTransfer.getCreatedAt() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault());
            tvCreatedAt.setText(String.format("Tạo lúc: %s", sdf.format(currentTransfer.getCreatedAt())));
        } else {
            tvCreatedAt.setText("");
        }

        Context context = requireContext();
        Account srcAcc = findAccountById(currentTransfer.getSourceAccountId());
        Account destAcc = findAccountById(currentTransfer.getDestinationAccountId());

        String systemCurrency = PreferenceManager.getInstance(requireContext()).getDefaultCurrency();
        String srcCurrency = (srcAcc != null && srcAcc.getCurrencyCode() != null) ? srcAcc.getCurrencyCode() : "VND";

        String formattedSourceAmount = CurrencyFormatter.formatVND(currentTransfer.getSourceAmount());
        tvAmount.setText(String.format("%s %s", formattedSourceAmount, srcCurrency));

        if (tvBaseAmountDetail != null) {
            if (!srcCurrency.equalsIgnoreCase(systemCurrency)) {
                tvBaseAmountDetail.setVisibility(View.VISIBLE);

                double baseAmt = (currentTransfer.getBaseAmount() != null && currentTransfer.getBaseAmount() > 0)
                        ? currentTransfer.getBaseAmount()
                        : CurrencyFormatter.previewConversion(currentTransfer.getSourceAmount(), srcCurrency, systemCurrency);

                String formattedBaseAmount = CurrencyFormatter.formatVND(baseAmt);
                tvBaseAmountDetail.setText(String.format("%s %s", formattedBaseAmount, systemCurrency));
            } else {
                tvBaseAmountDetail.setVisibility(View.GONE);
            }
        }

        if (srcAcc != null) {
            int actualColor = AppResourceManager.getColor(srcAcc.getColor());
            flSourceIcon.setBackgroundTintList(ColorStateList.valueOf(actualColor));
            ivSourceIcon.setImageDrawable(AppResourceManager.getWhiteIcon(context, srcAcc.getIcon()));
        }

        if (destAcc != null) {
            int actualColor = AppResourceManager.getColor(destAcc.getColor());
            flDestIcon.setBackgroundTintList(ColorStateList.valueOf(actualColor));
            ivDestIcon.setImageDrawable(AppResourceManager.getWhiteIcon(context, destAcc.getIcon()));
        }
    }

    private Account findAccountById(String accountId) {
        if (accountId == null || accountList == null || accountList.isEmpty()) return null;
        for (Account a : accountList) {
            if (a.getAccountId() != null && accountId.equalsIgnoreCase(a.getAccountId())) {
                return a;
            }
        }
        return null;
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
    protected void onFabClick() {
        if (currentTransferId != null) {
            Bundle args = new Bundle();
            args.putString("transferId", currentTransferId);
            Navigation.findNavController(requireView()).navigate(R.id.transferAddFragment, args);
        }
    }
}