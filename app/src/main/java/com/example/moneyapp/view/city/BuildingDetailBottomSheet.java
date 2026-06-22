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
        
        int upgradeCost = building.getLevel() * 200; // Mock cost formula
        btnUpgrade.setText("Nâng cấp (" + upgradeCost + " PP)");

        if (currentPP < upgradeCost) {
            btnUpgrade.setEnabled(false);
            btnUpgrade.setAlpha(0.5f);
            tvHint.setVisibility(View.VISIBLE);
            tvHint.setText("Bạn cần thêm " + (upgradeCost - currentPP) + " PP để nâng cấp");
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
            default: return "Công trình";
        }
    }
}
