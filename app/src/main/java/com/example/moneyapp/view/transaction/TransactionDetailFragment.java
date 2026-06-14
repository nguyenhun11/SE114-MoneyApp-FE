package com.example.moneyapp.view.transaction;

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
import com.mikepenz.iconics.IconicsDrawable;
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
                    "gmd-arrow-back", v -> Navigation.findNavController(v).navigateUp(),
                    "gmd-delete-outline", v -> {
                        Toast.makeText(getContext(), "Xóa giao dịch", Toast.LENGTH_SHORT).show();
                        // TODO: Gọi ViewModel để xóa dữ liệu, sau đó back về trang trước
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
            IconicsImageView ivCategoryIcon    = view.findViewById(R.id.iv_category_icon);
            FrameLayout flAccountIcon   = view.findViewById(R.id.fl_account_icon);
            IconicsImageView ivAccountIcon     = view.findViewById(R.id.iv_account_icon);

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

            // Đổ dữ liệu tiền và màu sắc text
            if (t.getAmount() != null && t.getAmount() < 0) {
                tvAmount.setText(String.format("%sđ", t.getFormattedAmount()));
                tvAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorDanger));
            } else {
                tvAmount.setText(String.format("+%sđ", t.getFormattedAmount()));
                tvAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorSuccess));
            }

            Context context = view.getContext();
            ivCategoryIcon.setImageDrawable(AppResourceManager.getWhiteIcon(context, t.getCategoryIconId()));
            flCategoryIcon.setBackgroundTintList(ColorStateList.valueOf(AppResourceManager.getColor(t.getCategoryColorId())));
            ivAccountIcon.setImageDrawable(AppResourceManager.getWhiteIcon(context, t.getAccountIconId()));
            flAccountIcon.setBackgroundTintList(ColorStateList.valueOf(AppResourceManager.getColor(t.getAccountIconId())));
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

    @Override
    protected void onFabClick() {
        if (currentTransactionId != null) {
            Toast.makeText(getContext(), "Mở màn hình Sửa giao dịch", Toast.LENGTH_SHORT).show();
        }
    }
}
