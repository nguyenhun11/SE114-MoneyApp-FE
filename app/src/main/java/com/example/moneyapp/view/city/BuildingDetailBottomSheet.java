package com.example.moneyapp.view.city;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.moneyapp.R;
import com.example.moneyapp.data.remote.response.CityResponse;
import com.example.moneyapp.utils.CityIconManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

public class BuildingDetailBottomSheet extends BottomSheetDialogFragment {

    public interface OnUpgradeListener {
        void onUpgrade(int buildingId);
    }

    private OnUpgradeListener listener;
    private CityResponse.BuildingDto building;
    private int currentPP;

    public static BuildingDetailBottomSheet newInstance(CityResponse.BuildingDto building, int currentPP) {
        BuildingDetailBottomSheet fragment = new BuildingDetailBottomSheet();
        fragment.building = building;
        fragment.currentPP = currentPP;
        return fragment;
    }

    public void setOnUpgradeListener(OnUpgradeListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_building_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView ivIcon = view.findViewById(R.id.ivBuildingIconLarge);
        TextView tvName = view.findViewById(R.id.tvBuildingNameDetail);
        TextView tvLevel = view.findViewById(R.id.tvBuildingLevelDetail);
        TextView tvEffect = view.findViewById(R.id.tvBuildingEffect);
        MaterialButton btnUpgrade = view.findViewById(R.id.btnUpgradeBuilding);
        TextView tvHint = view.findViewById(R.id.tvUpgradeHint);

        String typeName = getBuildingDisplayName(building.getBuildingType());
        tvName.setText(typeName);
        tvLevel.setText("Cấp " + building.getLevel());
        ivIcon.setImageDrawable(CityIconManager.getBuildingDrawable(getContext(), 
                building.getBuildingType(), building.getLevel()));
        
        tvEffect.setText(getBuildingEffectDescription(building.getBuildingType(), building.getLevel()));
        
        if (building.getLevel() >= 3) {
            btnUpgrade.setText("Đã đạt cấp tối đa");
            btnUpgrade.setEnabled(false);
            btnUpgrade.setAlpha(0.5f);
            tvHint.setVisibility(View.GONE);
        } else {
            int upgradeCost = building.getLevel() * 200; // Mock cost formula
            btnUpgrade.setText("Nâng cấp (" + upgradeCost + " PP)");

            if (currentPP < upgradeCost) {
                btnUpgrade.setEnabled(false);
                btnUpgrade.setAlpha(0.5f);
                tvHint.setVisibility(View.VISIBLE);
                tvHint.setText("Bạn cần thêm " + (upgradeCost - currentPP) + " PP để nâng cấp");
            } else {
                btnUpgrade.setEnabled(true);
                btnUpgrade.setAlpha(1.0f);
                tvHint.setVisibility(View.GONE);
            }
        }

        btnUpgrade.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUpgrade(building.getId());
                dismiss();
            }
        });
    }

    private String getBuildingDisplayName(String type) {
        switch (type.toLowerCase()) {
            case "house": return "Nhà ở";
            case "shop": return "Cửa hàng";
            case "factory": return "Nhà máy";
            case "park": return "Công viên";
            case "road": return "Đường phố";
            case "tree": return "Cây xanh";
            case "bench": return "Ghế đá";
            case "street_light": return "Đèn đường";
            case "flower_bed": return "Bồn hoa";
            case "fountain": return "Đài phun nước";
            case "statue": return "Tượng đài";
            default: return "Công trình";
        }
    }

    private String getBuildingEffectDescription(String type, int level) {
        switch (type.toLowerCase()) {
            case "house":
                return "Cộng +" + (10 * level) + " SP mỗi khi bạn thực hiện Điểm danh hàng ngày.";
            case "shop":
                return "Cộng thêm phần thưởng mỗi khi bạn Nhận thưởng nhiệm vụ (+" + (50 * level) + " PP hoặc +" + (10 * level) + " SP).";
            case "factory":
                return "Cộng +" + (20 * level) + " PP mỗi khi bạn Ghi chép một giao dịch mới.";
            case "road":
                return "Giúp kết nối các khu vực trong thành phố.";
            case "tree":
                return "Cải thiện môi trường và cảnh quan đô thị.";
            case "park":
                return "Không gian thư giãn cho cư dân thành phố.";
            case "fountain":
                return "Kiến trúc nghệ thuật tăng vẻ đẹp cho quảng trường.";
            case "statue":
                return "Biểu tượng văn hóa của thành phố.";
            default:
                return "Công trình kiến trúc độc đáo của MoneyCity.";
        }
    }
}
