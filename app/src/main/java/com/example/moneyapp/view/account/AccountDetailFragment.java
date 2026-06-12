package com.example.moneyapp.view.account;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.moneyapp.R;
import com.example.moneyapp.model.Account;
import com.example.moneyapp.utils.AppResourceManager;
import com.example.moneyapp.utils.PopupHelper;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.viewmodel.AccountViewModel;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AccountDetailFragment extends BaseFragment {

    private AccountViewModel viewModel;

    // Ánh xạ UI
    private EditText etName, etBalance, etDescription;
    private IconicsImageView ivSelectedIcon;
    private View vSelectedColor;
    private SwitchCompat switchExclude;
    private TextView tvCreatedAt;

    // State Variables
    private String currentAccountId = null;
    private int selectedIconId = 1;
    private int selectedColorId = 2;
    private boolean isDataPopulated = false;

    @Override
    protected String getFabIcon() {
        return "gmd_check";
    }

    @Override
    protected void onFabClick() {
        performSave();
    }

    @Override
    protected boolean shouldShowBottomNavigation() {
        return false;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        etName = view.findViewById(R.id.et_account_name);
        etBalance = view.findViewById(R.id.et_account_balance);
        etDescription = view.findViewById(R.id.et_account_description);
        ivSelectedIcon = view.findViewById(R.id.iv_selected_icon);
        vSelectedColor = view.findViewById(R.id.v_selected_color);
        switchExclude = view.findViewById(R.id.switch_exclude_total);
        tvCreatedAt = view.findViewById(R.id.tv_created_at);

        if (getArguments() != null) {
            currentAccountId = getArguments().getString("accountId", null);
        }

        String title = (currentAccountId == null) ? "Thêm tài khoản" : "";
        setupHeader(view, title, true);

        setupPickers(view);
        observeViewModel();

        if (currentAccountId != null) {
            tvCreatedAt.setVisibility(View.VISIBLE);
            viewModel.loadAccounts();
        } else {
            updateIconUI(selectedIconId);
            updateColorUI(selectedColorId);
            tvCreatedAt.setVisibility(View.GONE);
        }
    }

    private void setupPickers(View view) {
        view.findViewById(R.id.btn_pick_icon).setOnClickListener(v -> {
            PopupHelper.showIconPicker(requireContext(), id -> {
                selectedIconId = id;
                updateIconUI(id);
            });
        });

        view.findViewById(R.id.btn_pick_color).setOnClickListener(v -> {
            PopupHelper.showColorPicker(requireContext(), id -> {
                selectedColorId = id;
                updateColorUI(id);
            });
        });
    }

    private void updateIconUI(int iconId) {
        Context context = requireContext();
        ivSelectedIcon.setIcon(AppResourceManager.getWhiteIcon(context, iconId));
    }

    private void updateColorUI(int colorId) {
        int actualColor = AppResourceManager.getColor(colorId);
        vSelectedColor.getBackground().setTint(actualColor);
    }

    private void performSave() {
        String name = etName.getText().toString().trim();
        String balanceStr = etBalance.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etName.setError("Vui lòng nhập tên tài khoản");
            return;
        }

        double balance = 0.0;
        if (!TextUtils.isEmpty(balanceStr)) {
            balance = Double.parseDouble(balanceStr);
        }

        boolean includeInTotal = !switchExclude.isChecked();

        Account accountToSave = new Account(currentAccountId, name, balance, selectedColorId, selectedIconId, description, includeInTotal, 0, new Date(), new Date());

        if (currentAccountId == null) {
            viewModel.addAccount(accountToSave);
        } else {
            viewModel.updateAccount(accountToSave);
        }
    }

    private void observeViewModel() {
        viewModel.getAccountsLiveData().observe(getViewLifecycleOwner(), accounts -> {
            if (accounts == null || currentAccountId == null || isDataPopulated) return;

            for (Account acc : accounts) {
                if (currentAccountId.equals(acc.getAccountId())) {
                    setupHeader(requireView(), acc.getAccountName(), true);

                    etName.setText(acc.getAccountName());
                    etBalance.setText(String.format(Locale.US, "%.0f", acc.getBalance()));
                    etDescription.setText(acc.getDescription());

                    switchExclude.setChecked(!acc.isIncludeInTotal());

                    selectedIconId = acc.getIcon();
                    selectedColorId = acc.getColor();
                    updateIconUI(selectedIconId);
                    updateColorUI(selectedColorId);

                    if (acc.getCreatedAt() != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("'Tạo vào' HH:mm dd/M/yyyy", Locale.getDefault());
                        tvCreatedAt.setText(sdf.format(acc.getCreatedAt()));
                    }

                    isDataPopulated = true;
                    break;
                }
            }
        });

        viewModel.getSaveSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getContext(), "Lưu thông tin thành công!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp();
            }
        });

        viewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}