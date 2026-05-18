package com.example.moneyapp.ui.transaction;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import androidx.navigation.Navigation;

import com.example.moneyapp.R;
import com.example.moneyapp.data.local.entity.Account;
import com.example.moneyapp.data.local.entity.Category;
import com.example.moneyapp.data.local.entity.Transaction;
import com.example.moneyapp.data.repository.AccountRepository;
import com.example.moneyapp.data.repository.CategoryRepository;
import com.example.moneyapp.data.repository.TransactionRepository;
import com.example.moneyapp.ui.BaseFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddTransactionFragment extends BaseFragment {

    // 0=Transfer, 1=Expense(chi), 2=Income(thu) — theo Transaction entity
    private int transactionType = 1;
    private Date selectedDate;

    private final List<Account> accountList = new ArrayList<>();
    private final List<Category> categoryList = new ArrayList<>();

    private Spinner spinnerCategory;
    private Spinner spinnerSource;

    private AccountRepository accountRepository;
    private CategoryRepository categoryRepository;
    private TransactionRepository transactionRepository;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        accountRepository    = new AccountRepository(requireActivity().getApplication());
        categoryRepository   = new CategoryRepository(requireActivity().getApplication());
        transactionRepository = new TransactionRepository(requireActivity().getApplication());

        setupHeader(view, "Giao dịch mới", true);

        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        spinnerSource   = view.findViewById(R.id.spinnerSource);

        // Tab Chi / Thu
        setupIncomeExpenseTabs(view, isExpense -> {
            // Category.type: 1=income, 2=expense
            transactionType = isExpense ? 1 : 2;
            int categoryType = isExpense ? 2 : 1;
            loadCategories(categoryType);
        });

        // Load dữ liệu ban đầu (mặc định Chi → categoryType=2)
        loadAccounts();
        loadCategories(2);

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
        view.findViewById(R.id.btnSave).setOnClickListener(v -> saveTransaction(v));
    }

    private void loadAccounts() {
        accountRepository.getAccounts(new AccountRepository.AccountCallback() {
            @Override
            public void onSuccess(List<Account> accounts) {
                mainHandler.post(() -> {
                    accountList.clear();
                    accountList.addAll(accounts);
                    List<String> names = new ArrayList<>();
                    for (Account a : accounts) names.add(a.getName());
                    spinnerSource.setAdapter(new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_spinner_dropdown_item, names));
                });
            }

            @Override
            public void onError(String message) {
                mainHandler.post(() ->
                        Toast.makeText(getContext(), "Lỗi tải tài khoản: " + message,
                                Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void loadCategories(int categoryType) {
        categoryRepository.getCategoriesByType(categoryType, new CategoryRepository.CategoryCallback() {
            @Override
            public void onSuccess(List<Category> categories) {
                mainHandler.post(() -> {
                    categoryList.clear();
                    categoryList.addAll(categories);
                    List<String> names = new ArrayList<>();
                    for (Category c : categories) names.add(c.getName());
                    spinnerCategory.setAdapter(new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_spinner_dropdown_item, names));
                });
            }

            @Override
            public void onError(String message) {
                mainHandler.post(() ->
                        Toast.makeText(getContext(), "Lỗi tải hạng mục: " + message,
                                Toast.LENGTH_SHORT).show());
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

        double amount = Double.parseDouble(amountStr);
        String sourceAccountId = accountList.get(spinnerSource.getSelectedItemPosition()).getId();
        String categoryId      = categoryList.get(spinnerCategory.getSelectedItemPosition()).getId();

        Transaction transaction = new Transaction(
                transactionType,
                amount,
                sourceAccountId,
                null,           // destAccountId — null nếu không chuyển khoản
                categoryId,
                note,
                selectedDate
        );

        transactionRepository.addTransaction(transaction, new TransactionRepository.TransactionCallback() {
            @Override
            public void onSuccess(Transaction t) {
                mainHandler.post(() -> {
                    Toast.makeText(getContext(), "Đã lưu giao dịch!", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(v).navigateUp();
                });
            }

            @Override
            public void onSuccess(List<Transaction> transactionList) {}

            @Override
            public void onError(String message) {
                mainHandler.post(() ->
                        Toast.makeText(getContext(), "Lỗi: " + message, Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    protected boolean shouldShowBottomNavigation() { return false; }

    @Override
    protected int getFabIcon() { return R.drawable.ic_add_white; }

    @Override
    protected void onFabClick() {}
}