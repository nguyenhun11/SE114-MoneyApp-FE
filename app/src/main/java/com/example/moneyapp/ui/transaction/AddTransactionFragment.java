package com.example.moneyapp.ui.transaction;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.moneyapp.R;
import com.example.moneyapp.model.Account;
import com.example.moneyapp.model.Category;
import com.example.moneyapp.model.CategoryType;
import com.example.moneyapp.model.Transaction;
import com.example.moneyapp.ui.BaseFragment;
import com.example.moneyapp.viewmodel.AccountViewModel;
import com.example.moneyapp.viewmodel.CategoryViewModel;
import com.example.moneyapp.viewmodel.TransactionViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddTransactionFragment extends BaseFragment {

    private CategoryType transactionType = CategoryType.EXPENSE;
    private Date selectedDate;

    private final List<Account> accountList = new ArrayList<>();
    private final List<Category> categoryList = new ArrayList<>();

    private Spinner spinnerCategory;
    private Spinner spinnerSource;

    private AccountViewModel accountViewModel;
    private CategoryViewModel categoryViewModel;
    private TransactionViewModel transactionViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        setupHeader(view, "Giao dịch mới", true);

        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        spinnerSource   = view.findViewById(R.id.spinnerSource);

        // Tab Chi / Thu
        setupIncomeExpenseTabs(view, isExpense -> {
            transactionType = isExpense ? CategoryType.EXPENSE : CategoryType.INCOME;
            categoryViewModel.loadCategories(transactionType);
        });

        observeViewModels();

        // Load dữ liệu ban đầu
        accountViewModel.loadAccounts();
        categoryViewModel.loadCategories(CategoryType.EXPENSE);

        // Chọn ngày
        selectedDate = new Date();
        Button btnPickDate = view.findViewById(R.id.btnPickDate);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        btnPickDate.setText(sdf.format(selectedDate));

        btnPickDate.setOnClickListener(v ->
                new DatePickerDialog(requireContext(), (dp, year, month, day) -> {
                    calendar.set(year, month, day);
                    selectedDate = calendar.getTime();
                    btnPickDate.setText(sdf.format(selectedDate));
                },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show()
        );

        // Nút lưu
        view.findViewById(R.id.btnSave).setOnClickListener(this::saveTransaction);
    }

    private void observeViewModels() {
        accountViewModel.getAccountsLiveData().observe(getViewLifecycleOwner(), accounts -> {
            accountList.clear();
            accountList.addAll(accounts);
            List<String> names = new ArrayList<>();
            for (Account a : accounts) names.add(a.getAccountName());
            spinnerSource.setAdapter(new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_dropdown_item, names));
        });

        categoryViewModel.getCategoriesLiveData().observe(getViewLifecycleOwner(), categories -> {
            categoryList.clear();
            categoryList.addAll(categories);
            List<String> names = new ArrayList<>();
            for (Category c : categories) names.add(c.getCategoryName());
            spinnerCategory.setAdapter(new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_dropdown_item, names));
        });

        transactionViewModel.getOperationSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getContext(), "Đã lưu giao dịch!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp();
            }
        });

        transactionViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveTransaction(View v) {
        EditText etAmount      = requireView().findViewById(R.id.etAmount);
        EditText etDescription = requireView().findViewById(R.id.etDescription);

        String amountStr  = etAmount.getText().toString().trim();
        String note       = etDescription.getText().toString().trim();

        if (amountStr.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
            return;
        }
        if (accountList.isEmpty()) {
            Toast.makeText(getContext(), "Không có tài khoản nào", Toast.LENGTH_SHORT).show();
            return;
        }
        if (categoryList.isEmpty()) {
            Toast.makeText(getContext(), "Không có hạng mục nào", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amountValue = Double.parseDouble(amountStr);
            String sourceAccountId = accountList.get(spinnerSource.getSelectedItemPosition()).getAccountId();
            String categoryId      = categoryList.get(spinnerCategory.getSelectedItemPosition()).getCategoryId();

            // Đảm bảo số tiền âm nếu là Chi (Expense)
            double finalAmount = (transactionType == CategoryType.EXPENSE) ? -Math.abs(amountValue) : Math.abs(amountValue);

            Transaction transaction = new Transaction(
                    null, // transactionId
                    sourceAccountId,
                    categoryId,
                    finalAmount,
                    selectedDate,
                    note,
                    new ArrayList<>() // imageUrls
            );
            transaction.setType(transactionType);

            transactionViewModel.addTransaction(transaction);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected boolean shouldShowBottomNavigation() { return false; }

    @Override
    protected int getFabIcon() { return R.drawable.ic_add_white; }

    @Override
    protected void onFabClick() {}
}
