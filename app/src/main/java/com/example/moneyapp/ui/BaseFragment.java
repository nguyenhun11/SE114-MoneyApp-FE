package com.example.moneyapp.ui;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.moneyapp.R;

public abstract class BaseFragment extends Fragment {
    //Hàm để lấy kế thừa cho các fragment

    protected void setupHeader(View view, String titleText, boolean showBackBtn) {
        TextView tvTitle = view.findViewById(R.id.tv_header_title);
        ImageView btnBack = view.findViewById(R.id.btn_back);

        if (tvTitle != null) {
            tvTitle.setText(titleText);
        }

        if (btnBack != null) {
            if (showBackBtn) {
                btnBack.setVisibility(View.VISIBLE);
                btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
            } else {
                btnBack.setVisibility(View.INVISIBLE);
            }
        }
    }

    protected void setupHeader(View view, int titleResId, boolean showBackBtn) {
        TextView tvTitle = view.findViewById(R.id.tv_header_title);
        ImageView btnBack = view.findViewById(R.id.btn_back);

        if (tvTitle != null) {
            tvTitle.setText(titleResId);
        }

        if (btnBack != null) {
            if (showBackBtn) {
                btnBack.setVisibility(View.VISIBLE);
                btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
            } else {
                btnBack.setVisibility(View.INVISIBLE);
            }
        }
    }
}