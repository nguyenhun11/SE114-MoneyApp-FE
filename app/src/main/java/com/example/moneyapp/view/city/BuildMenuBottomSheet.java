package com.example.moneyapp.view.city;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.utils.CityIconManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class BuildMenuBottomSheet extends BottomSheetDialogFragment {

    public interface OnBuildOptionSelectedListener {
        void onSelected(BuildOption option);
    }

    private OnBuildOptionSelectedListener listener;
    private final List<BuildOption> buildingOptions = new ArrayList<>();
    private final List<BuildOption> landscapeOptions = new ArrayList<>();
    private final List<BuildOption> currentOptions = new ArrayList<>();
    private RecyclerView.Adapter<OptionViewHolder> adapter;

    public void setOnBuildOptionSelectedListener(OnBuildOptionSelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_build_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TabLayout tabLayout = view.findViewById(R.id.tabLayoutBuild);
        RecyclerView rvOptions = view.findViewById(R.id.rvBuildOptions);
        rvOptions.setLayoutManager(new LinearLayoutManager(getContext()));

        setupData();

        currentOptions.addAll(buildingOptions);
        
        adapter = new RecyclerView.Adapter<OptionViewHolder>() {
            @NonNull
            @Override
            public OptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_build_option, parent, false);
                return new OptionViewHolder(v);
            }

            @Override
            public void onBindViewHolder(@NonNull OptionViewHolder holder, int position) {
                BuildOption opt = currentOptions.get(position);
                holder.tvName.setText(opt.getName());
                holder.tvDesc.setText(opt.getDescription());
                holder.tvCost.setText(String.valueOf(opt.getCost()));
                holder.tvCurrency.setText(opt.getCurrency());
                
                int color = opt.getCurrency().equals("PP") ? 
                        holder.itemView.getContext().getColor(R.color.colorSuccess) : 
                        holder.itemView.getContext().getColor(R.color.colorInfo);
                holder.tvCost.setTextColor(color);

                holder.ivIcon.setImageDrawable(CityIconManager.getBuildingDrawable(getContext(), 
                        opt.getType(), 1));
                
                holder.itemView.setOnClickListener(v -> {
                    if (listener != null) listener.onSelected(opt);
                    dismiss();
                });
            }

            @Override
            public int getItemCount() { return currentOptions.size(); }
        };

        rvOptions.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentOptions.clear();
                if (tab.getPosition() == 0) currentOptions.addAll(buildingOptions);
                else currentOptions.addAll(landscapeOptions);
                adapter.notifyDataSetChanged();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupData() {
        buildingOptions.clear();
        buildingOptions.add(new BuildOption("house", "Nhà ở", "+10 SP cho mỗi căn (Level 1) khi Điểm danh", 100, "PP"));
        buildingOptions.add(new BuildOption("shop", "Cửa hàng", "Thêm thưởng khi Nhận nhiệm vụ (+50 PP hoặc +10 SP mỗi căn Level 1)", 300, "PP"));
        buildingOptions.add(new BuildOption("factory", "Nhà máy", "+20 PP cho mỗi căn (Level 1) khi Ghi chép chi tiêu", 600, "PP"));

        landscapeOptions.clear();
        landscapeOptions.add(new BuildOption("road", "Đường phố", "Kết nối các khu vực", 10, "SP"));
        landscapeOptions.add(new BuildOption("tree", "Cây xanh", "Làm đẹp môi trường", 20, "SP"));
        landscapeOptions.add(new BuildOption("bench", "Ghế đá", "Tiện ích công cộng", 5, "SP"));
        landscapeOptions.add(new BuildOption("street_light", "Đèn đường", "Thắp sáng thành phố", 15, "SP"));
        landscapeOptions.add(new BuildOption("flower_bed", "Bồn hoa", "Thêm sắc màu", 10, "SP"));
        landscapeOptions.add(new BuildOption("fountain", "Đài phun nước", "Kiến trúc nghệ thuật", 80, "SP"));
        landscapeOptions.add(new BuildOption("park", "Công viên", "Không gian xanh", 50, "SP"));
        landscapeOptions.add(new BuildOption("statue", "Tượng đài", "Biểu tượng thành phố", 150, "SP"));
    }

    static class OptionViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDesc, tvCost, tvCurrency;
        ImageView ivIcon;
        public OptionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvBuildingName);
            tvDesc = itemView.findViewById(R.id.tvBuildingDesc);
            tvCost = itemView.findViewById(R.id.tvCost);
            tvCurrency = itemView.findViewById(R.id.tvCurrencyLabel);
            ivIcon = itemView.findViewById(R.id.ivBuildingIcon);
        }
    }
}
