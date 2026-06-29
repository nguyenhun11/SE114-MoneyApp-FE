package com.example.moneyapp.view.city;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.moneyapp.R;
import com.example.moneyapp.data.remote.response.RankItemDto;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CityRankAdapter extends RecyclerView.Adapter<CityRankAdapter.RankViewHolder> {

    private List<RankItemDto> rankList = new ArrayList<>();
    private final DecimalFormat df = new DecimalFormat("#,###");
    private int rankType = 1; // 1: Prosperity, 2: Stability

    public void setData(List<RankItemDto> data, int rankType) {
        this.rankList = data != null ? data : new ArrayList<>();
        this.rankType = rankType;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city_rank, parent, false);
        return new RankViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankViewHolder holder, int position) {
        RankItemDto item = rankList.get(position);
        holder.bind(item, position + 1);
    }

    @Override
    public int getItemCount() {
        return rankList.size();
    }

    class RankViewHolder extends RecyclerView.ViewHolder {
        TextView tvRankNumber, tvUserName, tvUserCityLevel, tvProsperityPoints, tvPointLabel;
        ImageView ivAvatar;

        public RankViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRankNumber = itemView.findViewById(R.id.tvRankNumber);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserCityLevel = itemView.findViewById(R.id.tvUserCityLevel);
            tvProsperityPoints = itemView.findViewById(R.id.tvProsperityPoints);
            tvPointLabel = itemView.findViewById(R.id.tvPointLabel);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
        }

        public void bind(RankItemDto item, int rank) {
            tvRankNumber.setText(String.valueOf(rank));
            tvUserName.setText(item.getName());
            tvUserCityLevel.setText("Thành phố Cấp " + item.getCityLevel());
            
            if (rankType == 1) {
                tvProsperityPoints.setText(df.format(item.getProsperityPoints()));
                tvPointLabel.setText("PROSPERITY");
                tvProsperityPoints.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.colorChartOrange));
            } else {
                tvProsperityPoints.setText(df.format(item.getStabilityPoints()));
                tvPointLabel.setText("STABILITY");
                tvProsperityPoints.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.colorInfo));
            }

            // Highlight top 3
            if (rank == 1) {
                tvRankNumber.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.colorWarning));
            } else if (rank == 2) {
                tvRankNumber.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.colorInfo));
            } else if (rank == 3) {
                tvRankNumber.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.colorChartOrange));
            } else {
                tvRankNumber.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.colorOnSurface));
            }

            if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(item.getImageUrl())
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .circleCrop()
                        .into(ivAvatar);
            } else {
                ivAvatar.setImageResource(R.drawable.ic_launcher_foreground);
            }
        }
    }
}
