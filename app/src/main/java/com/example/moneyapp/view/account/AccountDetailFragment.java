package com.example.moneyapp.view.account;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.moneyapp.R;
import com.example.moneyapp.model.Account;
import com.example.moneyapp.utils.AppResourceManager;
import com.example.moneyapp.utils.CurrencyFormatter;
import com.example.moneyapp.utils.PopupHelper;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.viewmodel.AccountViewModel;
import com.mikepenz.iconics.view.IconicsImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AccountDetailFragment extends BaseFragment {

    private AccountViewModel viewModel;

    // Ánh xạ UI
    private EditText etName, etBalance, etDescription;
    private IconicsImageView ivSelectedIcon;
    private View vSelectedColor;
    private SwitchCompat switchExclude;
    private TextView tvCreatedAt;

    // UI Tiền tệ
    private View btnSelectCurrency;
    private TextView tvCurrency;

    // State Variables
    private String currentAccountId = null;
    private int selectedIconId = 1;
    private int selectedColorId = 2;
    private boolean isDataPopulated = false;
    private String currentCurrencyCode = "VND";

    @Override
    protected String getFabIcon() {
        return "gmd-check";
    }

    @Override
    protected String getFabLabel() {
        return "Lưu tài khoản";
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

        btnSelectCurrency = view.findViewById(R.id.btnSelectCurrency);
        tvCurrency = view.findViewById(R.id.tvCurrency);

        NestedScrollView mainScrollView = view.findViewById(R.id.main_scroll_view);

        view.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            view.getWindowVisibleDisplayFrame(r);
            int screenHeight = view.getRootView().getHeight();
            int keypadHeight = screenHeight - r.bottom;

            if (keypadHeight > screenHeight * 0.15) {
                int[] svLocation = new int[2];
                mainScrollView.getLocationOnScreen(svLocation);
                int svBottom = svLocation[1] + mainScrollView.getHeight();
                int overlap = Math.max(0, svBottom - r.bottom);

                mainScrollView.setPadding(0, 0, 0, overlap);

                if (etDescription.hasFocus()) {
                    mainScrollView.postDelayed(() -> {
                        int dp24 = (int) (24 * getResources().getDisplayMetrics().density);
                        int visibleHeight = mainScrollView.getHeight() - overlap;
                        int targetY = etDescription.getBottom() + dp24 - visibleHeight;
                        mainScrollView.smoothScrollTo(0, Math.max(0, targetY));
                    }, 100);
                }
            } else {
                mainScrollView.setPadding(0, 0, 0, 0);
            }
        });

        if (getArguments() != null) {
            currentAccountId = getArguments().getString("accountId", null);
        }

        String title = (currentAccountId == null) ? "Thêm tài khoản" : "";
        setupHeader(view, title, true);

        setupPickers(view);
        setupCurrencyPicker();
        setupCurrencyFormatter();
        observeViewModel();

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                checkSaveConditions();
            }
        });

        if (currentAccountId != null) {
            tvCreatedAt.setVisibility(View.VISIBLE);
            viewModel.loadAccounts();

            if (btnSelectCurrency != null) {
                btnSelectCurrency.setClickable(false);
                btnSelectCurrency.setAlpha(0.6f);
            }
        } else {
            updateIconUI(selectedIconId);
            updateColorUI(selectedColorId);
            tvCreatedAt.setVisibility(View.GONE);

            if (tvCurrency != null) {
                tvCurrency.setText(currentCurrencyCode);
            }
        }

        view.post(this::checkSaveConditions);
    }

    private void checkSaveConditions() {
        boolean isValid = true;
        String nameStr = etName.getText().toString().trim();

        if (nameStr.isEmpty()) {
            isValid = false;
        }

        setFabEnabled(isValid);
    }

    private void setupCurrencyPicker() {
        if (btnSelectCurrency == null) return;

        btnSelectCurrency.setOnClickListener(v -> {
            hideKeyboard();

            if (currentAccountId != null) {
                Toast.makeText(requireContext(), "Không thể đổi đơn vị tiền tệ của ví đã tạo", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> allCurrencies = CurrencyFormatter.getSupportedCurrencies();
            if (allCurrencies == null || allCurrencies.isEmpty()) {
                allCurrencies = new java.util.ArrayList<>(java.util.Arrays.asList("VND", "USD", "EUR", "JPY"));
            }

            PopupHelper.showCurrencyFilterPopup(requireContext(), allCurrencies, selectedCurrency -> {
                currentCurrencyCode = selectedCurrency;
                tvCurrency.setText(currentCurrencyCode);
            });
        });
    }

    private void setupCurrencyFormatter() {
        etBalance.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    etBalance.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[.,]", "");

                    if (!cleanString.isEmpty()) {
                        try {
                            double parsed = Double.parseDouble(cleanString);
                            String formatted = CurrencyFormatter.formatVND(parsed);
                            current = formatted;
                            etBalance.setText(formatted);
                            etBalance.setSelection(formatted.length());
                        } catch (NumberFormatException e) { }
                    } else {
                        current = "";
                        etBalance.setText("");
                    }

                    etBalance.addTextChangedListener(this);
                }
            }
        });
    }

    private void setupPickers(View view) {
        view.findViewById(R.id.btn_pick_icon).setOnClickListener(v -> {
            hideKeyboard();
            PopupHelper.showIconPicker(requireContext(), id -> {
                selectedIconId = id;
                updateIconUI(id);
            });
        });

        view.findViewById(R.id.btn_pick_color).setOnClickListener(v -> {
            hideKeyboard();
            PopupHelper.showColorPicker(requireContext(), id -> {
                selectedColorId = id;
                updateColorUI(id);
            });
        });
    }

    private void updateIconUI(int iconId) {
        Context context = requireContext();
        ivSelectedIcon.setImageDrawable(AppResourceManager.getWhiteIcon(context, iconId));
    }

    private void updateColorUI(int colorId) {
        int actualColor = AppResourceManager.getColor(colorId);
        vSelectedColor.getBackground().setTint(actualColor);
    }

    private void performSave() {
        String name = etName.getText().toString().trim();
        String balanceStr = etBalance.getText().toString().trim().replaceAll("[.,]", "");
        String description = etDescription.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etName.setError("Vui lòng nhập tên tài khoản");
            return;
        }

        double balance = 0.0;
        if (!TextUtils.isEmpty(balanceStr)) {
            try {
                balance = Double.parseDouble(balanceStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Số dư không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        boolean includeInTotal = !switchExclude.isChecked();

        Account accountToSave = new Account(
                currentAccountId,
                name,
                balance,
                currentCurrencyCode,
                selectedColorId,
                selectedIconId,
                description,
                includeInTotal,
                0,
                new Date(),
                new Date()
        );

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

                    currentCurrencyCode = acc.getCurrencyCode();
                    if (tvCurrency != null && currentCurrencyCode != null) {
                        tvCurrency.setText(currentCurrencyCode);
                    }

                    if (acc.getCreatedAt() != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("'Tạo vào' HH:mm dd/M/yyyy", Locale.getDefault());
                        tvCreatedAt.setText(sdf.format(acc.getCreatedAt()));
                    }

                    isDataPopulated = true;

                    checkSaveConditions();

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