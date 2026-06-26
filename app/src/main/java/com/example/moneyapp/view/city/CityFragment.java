package com.example.moneyapp.view.city;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.moneyapp.R;
import com.example.moneyapp.data.remote.response.CityResponse;
import com.example.moneyapp.utils.CityIconManager;
import com.example.moneyapp.utils.DialogHelper;
import com.example.moneyapp.view.BaseFragment;
import com.example.moneyapp.viewmodel.CityViewModel;

public class CityFragment extends BaseFragment {

    private CityViewModel viewModel;
    private TextView tvLevel, tvProsperity, tvStability;
    private GridLayout cityGrid;
    private static final int GRID_SIZE = 10;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_city, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupHeader(view, "MoneyCity", null, null, "gmd_info_outline", v -> 
            new CityGuideBottomSheet().show(getChildFragmentManager(), "CityGuide")
        );

        tvLevel = view.findViewById(R.id.tvLevel);
        tvProsperity = view.findViewById(R.id.tvProsperity);
        tvStability = view.findViewById(R.id.tvStability);
        cityGrid = view.findViewById(R.id.cityGrid);

        viewModel = new ViewModelProvider(this).get(CityViewModel.class);

        viewModel.getCityData().observe(getViewLifecycleOwner(), this::updateUI);

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) DialogHelper.showSimpleDialog(getContext(), "Lỗi", error);
        });

        viewModel.fetchCityData();
    }

    private void updateUI(CityResponse city) {
        if (city == null) return;
        tvLevel.setText("Cấp " + city.getLevel());
        tvProsperity.setText(String.valueOf(city.getProsperityPoints()));
        tvStability.setText(String.valueOf(city.getStabilityPoints()));
        renderGrid(city);
    }

    private void renderGrid(CityResponse city) {
        cityGrid.removeAllViews();
        cityGrid.setRowCount(GRID_SIZE);
        cityGrid.setColumnCount(GRID_SIZE);

        int cellSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getResources().getDisplayMetrics());

        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                FrameLayout cell = new FrameLayout(getContext());
                GridLayout.LayoutParams params = new GridLayout.LayoutParams(
                        GridLayout.spec(r), GridLayout.spec(c));
                params.width = cellSize;
                params.height = cellSize;
                params.setMargins(4, 4, 4, 4);
                cell.setLayoutParams(params);
                
                // Ô lưới đẹp hơn: Bo góc và màu xanh cỏ
                cell.setBackgroundResource(R.drawable.bg_city_grid);
                
                final int currentRow = r;
                final int currentCol = c;

                CityResponse.BuildingDto building = findBuildingAt(city, r, c);
                if (building != null) {
                    ImageView ivBuilding = new ImageView(getContext());
                    ivBuilding.setImageDrawable(CityIconManager.getBuildingDrawable(getContext(), 
                            building.getBuildingType(), building.getLevel()));
                    ivBuilding.setPadding(12, 12, 12, 12);
                    cell.addView(ivBuilding);
                    
                    cell.setOnClickListener(v -> showBuildingDetail(building));
                } else {
                    cell.setOnClickListener(v -> startBuildingAt(currentRow, currentCol));
                }

                cityGrid.addView(cell);
            }
        }
    }

    private CityResponse.BuildingDto findBuildingAt(CityResponse city, int r, int c) {
        if (city.getBuildings() == null) return null;
        for (CityResponse.BuildingDto b : city.getBuildings()) {
            if (b.getPositionX() == r && b.getPositionY() == c) return b;
        }
        return null;
    }

    private void showBuildingDetail(CityResponse.BuildingDto building) {
        // Kiểm tra nếu là vật phẩm trang trí thì không cho mở Menu Nâng cấp
        String type = building.getBuildingType().toLowerCase();
        if (type.equals("road") || type.equals("tree") || type.equals("bench") ||
            type.equals("street_light") || type.equals("flower_bed") ||
            type.equals("fountain") || type.equals("statue")) {
            Toast.makeText(getContext(), getBuildingDisplayName(type) + " (Vật phẩm trang trí)", Toast.LENGTH_SHORT).show();
            return;
        }

        CityResponse currentCity = viewModel.getCityData().getValue();
        int prosperity = (currentCity != null) ? currentCity.getProsperityPoints() : 0;

        BuildingDetailBottomSheet bottomSheet = BuildingDetailBottomSheet.newInstance(building, prosperity);
        bottomSheet.setOnUpgradeListener(buildingId -> viewModel.upgradeStructure(buildingId));
        bottomSheet.show(getChildFragmentManager(), "BuildingDetail");
    }

    private String getBuildingDisplayName(String type) {
        switch (type.toLowerCase()) {
            case "road": return "Đường phố";
            case "tree": return "Cây xanh";
            case "bench": return "Ghế đá";
            case "street_light": return "Đèn đường";
            case "flower_bed": return "Bồn hoa";
            case "fountain": return "Đài phun nước";
            case "statue": return "Tượng đài";
            default: return "Cảnh quan";
        }
    }

    private void startBuildingAt(int r, int c) {
        CityResponse currentCity = viewModel.getCityData().getValue();
        if (currentCity == null) return;

        BuildMenuBottomSheet bottomSheet = new BuildMenuBottomSheet();
        bottomSheet.setOnBuildOptionSelectedListener(option -> {
            int currentPoints = option.getCurrency().equals("PP") ?
                    currentCity.getProsperityPoints() : currentCity.getStabilityPoints();

            if (currentPoints < option.getCost()) {
                String msg = "Không đủ điểm " + (option.getCurrency().equals("PP") ? "Prosperity" : "Stability") + "!";
                DialogHelper.showSimpleDialog(getContext(), "Thông báo", "Không đủ điểm Prosperity!");
                return;
            }
            
            viewModel.buildStructure(new com.example.moneyapp.data.remote.request.BuildRequest(
                    option.getType(), r, c
            ));
        });
        bottomSheet.show(getChildFragmentManager(), "BuildMenu");
    }

    @Override
    protected boolean shouldShowBottomNavigation() {
        return false;
    }
    @Override
    protected String getFabLabel(){
        return "Xây dựng";
    }
    @Override
    protected void onFabClick(){
        DialogHelper.showSimpleDialog(getContext(), "Hướng dẫn", "Chọn một ô trống để bắt đầu xây dựng!");
    }
    @Override
    protected int getFabBackgroundColorRes(){
        return R.color.colorChartOrange;
    }
}
