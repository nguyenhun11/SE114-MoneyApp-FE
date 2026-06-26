package com.example.moneyapp.view.information;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.moneyapp.R;
import com.example.moneyapp.view.BaseFragment;

public class InformationFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_information, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupHeader(view, R.string.info_screen_title, false);

        LinearLayout btnFeedback = view.findViewById(R.id.btn_send_feedback);
        LinearLayout btnShare = view.findViewById(R.id.btn_share_app);

        if (btnFeedback != null) {
            btnFeedback.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"giahungcantho11@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Hỗ trợ khách hàng - MoneyApp");
                intent.putExtra(Intent.EXTRA_TEXT, "Chào bạn,\n\nTôi cần hỗ trợ về ứng dụng MoneyApp:\n...");
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(requireContext(), "Không tìm thấy ứng dụng gửi Email trên thiết bị.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (btnShare != null) {
            btnShare.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Tải ứng dụng MoneyApp");
                    intent.putExtra(Intent.EXTRA_TEXT, "Tải ngay MoneyApp - Ứng dụng quản lý tài chính cá nhân tuyệt vời tại: https://github.com/quanghuy-newbie/SE114-MoneyApp-FE");
                    startActivity(Intent.createChooser(intent, "Chia sẻ ứng dụng qua"));
                } catch (Exception e) {
                    Toast.makeText(requireContext(), "Không thể thực hiện chia sẻ.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
