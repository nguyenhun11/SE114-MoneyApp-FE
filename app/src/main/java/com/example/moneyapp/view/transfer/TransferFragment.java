package com.example.moneyapp.view.transfer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.model.Account;
import com.example.moneyapp.model.HistoryItem;
import com.example.moneyapp.utils.PopupHelper;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.view.components.TimeSelectorView;
import com.example.moneyapp.viewmodel.AccountViewModel;
import com.example.moneyapp.viewmodel.TransferViewModel;

import java.util.ArrayList;
import java.util.List;

public class TransferFragment extends BaseFragment {

    private TransferGroupAdapter adapter;
    private TransferViewModel transferViewModel;
    private AccountViewModel accountViewModel;

    private TimeSelectorView timeSelector;
    private TextView tvAccountFilter;
    private final List<Account> accountList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transfer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        transferViewModel = new ViewModelProvider(this).get(TransferViewModel.class);
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        setupHeader(view, "Lịch sử chuyển khoản", true);

        timeSelector = view.findViewById(R.id.time_selector);
        tvAccountFilter = view.findViewById(R.id.tv_selected_account);

        RecyclerView rvTransfers = view.findViewById(R.id.rvTransfers);
        rvTransfers.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new TransferGroupAdapter(new ArrayList<>(), accountList, (HistoryItem item) -> {
            if (item.getType() == HistoryItem.TYPE_TRANSFER) {
                Bundle args = new Bundle();
                args.putString("transferId", item.getTransfer().getId());
                Navigation.findNavController(view).navigate(R.id.transferDetailFragment, args);
            }
            else if (item.getType() == HistoryItem.TYPE_ADJUST_BALANCE) {
                Bundle args = new Bundle();
                args.putString("adjustId", item.getAdjustBalance().getId());
                args.putDouble("amount", item.getAdjustBalance().getAmount());
                args.putString("accountId", item.getAdjustBalance().getAccountId());
                args.putString("accountName", item.getAdjustBalance().getAccountName());
                if (item.getAdjustBalance().getCreatedAt() != null) {
                    args.putLong("createdAt", item.getAdjustBalance().getCreatedAt().getTime());
                }

                Navigation.findNavController(view).navigate(R.id.adjustBalanceDetailFragment, args);
            }

        });
        rvTransfers.setAdapter(adapter);

        timeSelector.setOnTimeRangeChangeListener((startDate, endDate) -> {
            transferViewModel.setTimeRangeAndReload(startDate, endDate);
        });

        view.findViewById(R.id.btn_account_filter).setOnClickListener(v -> showAccountFilterPopup());

        observeViewModels();
        accountViewModel.loadAccounts();
    }

    private void observeViewModels() {
        transferViewModel.getGroupedTransfers().observe(getViewLifecycleOwner(), groups -> {
            if (groups != null) {
                adapter.updateData(groups, accountList);
            }
        });

        transferViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        accountViewModel.getAccountsLiveData().observe(getViewLifecycleOwner(), accounts -> {
            if (accounts != null) {
                accountList.clear();
                accountList.addAll(accounts);

                // Khi danh sách ví cập nhật, ép adapter update lại
                if (transferViewModel.getGroupedTransfers().getValue() != null) {
                    adapter.updateData(transferViewModel.getGroupedTransfers().getValue(), accountList);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        transferViewModel.reloadTransfers();
    }

    private void showAccountFilterPopup() {
        if (accountList.isEmpty()) {
            Toast.makeText(getContext(), "Đang tải dữ liệu...", Toast.LENGTH_SHORT).show();
            return;
        }
        PopupHelper.showAccountFilterPopup(requireContext(), accountList,
                transferViewModel.getCurrentAccountId(),
                true,
                selectedAcc -> {
                    if (selectedAcc == null) {
                        tvAccountFilter.setText("Tất cả tài khoản");
                        transferViewModel.setAccountFilterAndReload(null);
                    } else {
                        tvAccountFilter.setText(selectedAcc.getAccountName());
                        transferViewModel.setAccountFilterAndReload(selectedAcc.getAccountId());
                    }
                });
    }

    @Override
    protected void onFabClick() {
        Navigation.findNavController(requireView()).navigate(R.id.action_transferFragment_to_transferAddFragment);
    }
}