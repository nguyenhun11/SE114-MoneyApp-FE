package com.example.moneyapp.view.transaction;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.moneyapp.R;
import com.example.moneyapp.utils.ResourceMapper;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.viewmodel.TransactionViewModel;

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
                    R.drawable.ic_back, v -> Navigation.findNavController(v).navigateUp(),
                    R.drawable.ic_delete, v -> {
                        Toast.makeText(getContext(), "Xóa giao dịch", Toast.LENGTH_SHORT).show();
                        // Navigation.findNavController(view).navigate(...);
                    });

            observeViewModel(view);
            transactionViewModel.loadTransactionById(currentTransactionId);
        } else {
            Toast.makeText(getContext(), "Không tìm thấy mã giao dịch", Toast.LENGTH_SHORT).show();
        }
    }

    private void observeViewModel(View view) {
        transactionViewModel.getSelectedTransaction().observe(getViewLifecycleOwner(), t -> {
            if (t == null) return;

            // Ánh xạ UI
            FrameLayout flCategoryIcon  = view.findViewById(R.id.fl_category_icon);
            ImageView ivCategoryIcon    = view.findViewById(R.id.iv_category_icon);
            FrameLayout flAccountIcon   = view.findViewById(R.id.fl_account_icon);
            ImageView ivAccountIcon     = view.findViewById(R.id.iv_account_icon);

            TextView tvCategory         = view.findViewById(R.id.tvDetailCategoryLabel);
            TextView tvSource           = view.findViewById(R.id.tvDetailSource);
            TextView tvAmount           = view.findViewById(R.id.tvDetailAmount);
            TextView tvDate             = view.findViewById(R.id.tvDetailDate);
            TextView tvDescription      = view.findViewById(R.id.tvDetailDescription);
            TextView tvCreatedAt        = view.findViewById(R.id.tvCreatedAt);

            // Đổ dữ liệu text cơ bản
            tvCategory.setText(t.getCategoryName() != null ? t.getCategoryName() : "Hạng mục");
            tvSource.setText(t.getAccountName() != null ? t.getAccountName() : "Ví");
            tvDate.setText(t.getFormattedDate());

            // =====================================================================
            // XỬ LÝ ẨN/HIỆN DÒNG GHI CHÚ ĐỘNG
            // =====================================================================
            View noteRowContainer = (View) tvDescription.getParent(); // Lấy LinearLayout bọc bên ngoài
            if (t.getNote() != null && !t.getNote().trim().isEmpty()) {
                tvDescription.setText(t.getNote());
                noteRowContainer.setVisibility(View.VISIBLE); // Hiển thị cả hàng
            } else {
                noteRowContainer.setVisibility(View.GONE); // Ẩn hoàn toàn hàng này đi
            }

            // =====================================================================
            // FORMAT LẠI NGÀY TẠO (CreatedAt)
            // =====================================================================
            if (t.getCreatedAt() != null) {
                // Định dạng lại Date thành chuỗi đẹp mắt
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault());
                tvCreatedAt.setText("Tạo lúc: " + sdf.format(t.getCreatedAt()));
            } else {
                tvCreatedAt.setText("");
            }

            // Đổ dữ liệu tiền và màu sắc text
            if (t.getAmount() != null && t.getAmount() < 0) {
                tvAmount.setText(t.getFormattedAmount() + "đ");
                tvAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorDanger));
            } else {
                tvAmount.setText("+" + t.getFormattedAmount() + "đ");
                tvAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorSuccess));
            }

            // ĐẮP MÀU VÀ ICON CHO HẠNG MỤC
            int catIconRes = ResourceMapper.getIconResourceById(t.getCategoryIconId());
            int catColorRes = ResourceMapper.getColorResourceById(t.getCategoryColorId());
            int catActualColor = ContextCompat.getColor(requireContext(), catColorRes);

            ivCategoryIcon.setImageResource(catIconRes);
            flCategoryIcon.setBackgroundTintList(ColorStateList.valueOf(catActualColor));

            // ĐẮP MÀU VÀ ICON CHO NGUỒN TIỀN
            int accIconRes = ResourceMapper.getIconResourceById(t.getAccountIconId());
            int accColorRes = ResourceMapper.getColorResourceById(t.getAccountColorId());
            int accActualColor = ContextCompat.getColor(requireContext(), accColorRes);

            ivAccountIcon.setImageResource(accIconRes);
            flAccountIcon.setBackgroundTintList(ColorStateList.valueOf(accActualColor));
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
    protected int getFabIcon() {
        return R.drawable.ic_plus;
    }

    @Override
    protected void onFabClick() {
        if (currentTransactionId != null) {
            Toast.makeText(getContext(), "Mở màn hình Sửa giao dịch", Toast.LENGTH_SHORT).show();
            // Bundle args = new Bundle();
            // args.putString("transactionId", currentTransactionId);
            // Navigation.findNavController(requireView()).navigate(R.id.editTransactionFragment, args);
        }
    }
}