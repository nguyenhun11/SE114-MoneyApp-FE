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
import androidx.navigation.Navigation;

import com.example.moneyapp.R;
import com.example.moneyapp.ui.BaseFragment;

import java.util.Calendar;

public class AddTransactionFragment extends BaseFragment {

    private boolean isExpense = true; // mặc định là Chi
    private String selectedDate = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupHeader(view, "Giao dịch mới", true);

        // Tab Chi/Thu
        setupIncomeExpenseTabs(view, expense -> {
            isExpense = expense;
        });

        // Spinner hạng mục
        Spinner spinnerCategory = view.findViewById(R.id.spinnerCategory);
        String[] categories = {"Ăn uống", "Sinh hoạt", "Di chuyển", "Mua sắm", "Giải trí", "Lương", "Khác"};
        spinnerCategory.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, categories));

        // Spinner nguồn tiền
        Spinner spinnerSource = view.findViewById(R.id.spinnerSource);
        String[] sources = {"Tiền mặt", "Momo", "Ngân hàng"};
        spinnerSource.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, sources));

        // Chọn ngày
        Button btnPickDate = view.findViewById(R.id.btnPickDate);
        Calendar calendar = Calendar.getInstance();
        // Mặc định là ngày hôm nay
        selectedDate = calendar.get(Calendar.YEAR) + "-"
                + String.format("%02d", calendar.get(Calendar.MONTH) + 1) + "-"
                + String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));
        btnPickDate.setText(selectedDate);

        btnPickDate.setOnClickListener(v -> {
            new DatePickerDialog(requireContext(), (datePicker, year, month, day) -> {
                selectedDate = year + "-"
                        + String.format("%02d", month + 1) + "-"
                        + String.format("%02d", day);
                btnPickDate.setText(selectedDate);
            },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Nút lưu
        view.findViewById(R.id.btnSave).setOnClickListener(v -> {
            EditText etAmount = view.findViewById(R.id.etAmount);
            EditText etDescription = view.findViewById(R.id.etDescription);

            String amount = etAmount.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String category = spinnerCategory.getSelectedItem().toString();
            String source = spinnerSource.getSelectedItem().toString();
            String type = isExpense ? "chi" : "thu";

            // Kiểm tra không bỏ trống số tiền
            if (amount.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
                return;
            }

            // TODO: Lưu vào database sau
            Toast.makeText(getContext(), "Đã lưu giao dịch!", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).navigateUp();
        });
    }

    @Override
    protected boolean shouldShowBottomNavigation() {
        return false;
    }

    @Override
    protected int getFabIcon() {
        return R.drawable.ic_add_white;
    }

    @Override
    protected void onFabClick() {}
}