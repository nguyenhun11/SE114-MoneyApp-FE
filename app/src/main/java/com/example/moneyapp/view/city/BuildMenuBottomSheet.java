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

import java.util.ArrayList;
import java.util.List;

public class BuildMenuBottomSheet extends BottomSheetDialogFragment {

    public interface OnBuildOptionSelectedListener {
        void onSelected(BuildOption option);
    }

    private OnBuildOptionSelectedListener listener;

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

        RecyclerView rvOptions = view.findViewById(R.id.rvBuildOptions);
        rvOptions.setLayoutManager(new LinearLayoutManager(getContext()));

        List<BuildOption> options = new ArrayList<>();
        options.add(new BuildOption("house", "Nhà ở", "Tăng 5 điểm ổn định / ngày", 100));
        options.add(new BuildOption("shop", "Cửa hàng", "Tăng thu nhập thụ động", 300));
        options.add(new BuildOption("factory", "Nhà máy", "Tăng Prosperity nhanh hơn", 600));
        options.add(new BuildOption("park", "Công viên", "Tăng điểm hạnh phúc", 200));

        rvOptions.setAdapter(new RecyclerView.Adapter<OptionViewHolder>() {
            @NonNull
            @Override
            public OptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_build_option, parent, false);
                return new OptionViewHolder(v);
            }

            @Override
            public void onBindViewHolder(@NonNull OptionViewHolder holder, int position) {
                BuildOption opt = options.get(position);
                holder.tvName.setText(opt.getName());
                holder.tvDesc.setText(opt.getDescription());
                holder.tvCost.setText(opt.getCost() + " P");
                holder.ivIcon.setImageDrawable(CityIconManager.getBuildingDrawable(getContext(), 
                        opt.getType(), 1)); // Default level 1 for new build
                holder.itemView.setOnClickListener(v -> {
                    if (listener != null) listener.onSelected(opt);
                    dismiss();
                });
            }

            @Override
            public int getItemCount() { return options.size(); }
        });
    }

    static class OptionViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDesc, tvCost;
        ImageView ivIcon;
        public OptionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvBuildingName);
            tvDesc = itemView.findViewById(R.id.tvBuildingDesc);
            tvCost = itemView.findViewById(R.id.tvCost);
            ivIcon = itemView.findViewById(R.id.ivBuildingIcon);
        }
    }
}
