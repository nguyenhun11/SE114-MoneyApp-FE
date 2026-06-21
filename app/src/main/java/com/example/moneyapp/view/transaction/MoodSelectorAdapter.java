package com.example.moneyapp.view.transaction;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyapp.R;
import com.example.moneyapp.model.Mood;

import java.util.List;

public class MoodSelectorAdapter extends RecyclerView.Adapter<MoodSelectorAdapter.ViewHolder> {

    private final List<Mood> moods;
    private int selectedMoodId = 0;
    private final OnMoodClickListener listener;

    public interface OnMoodClickListener {
        void onMoodClick(Mood mood);
    }

    public MoodSelectorAdapter(List<Mood> moods, int initialMoodId, OnMoodClickListener listener) {
        this.moods = moods;
        this.selectedMoodId = initialMoodId;
        this.listener = listener;
    }

    public int getSelectedMoodId() {
        return selectedMoodId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mood_selector, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Mood mood = moods.get(position);
        holder.tvEmoji.setText(mood.getEmoji());
        holder.tvName.setText(mood.getName());

        boolean isSelected = mood.getId() == selectedMoodId;
        holder.itemView.setBackgroundResource(isSelected ? R.drawable.bg_date_selected : R.drawable.bg_input_border);
        holder.tvName.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), 
                isSelected ? R.color.colorOnSurface : R.color.colorOnSurfaceVariant));
        holder.tvName.setTypeface(null, isSelected ? Typeface.BOLD : Typeface.NORMAL);

        holder.itemView.setOnClickListener(v -> {
            int oldSelected = selectedMoodId;
            selectedMoodId = mood.getId();
            notifyItemChanged(oldSelected);
            notifyItemChanged(position);
            listener.onMoodClick(mood);
        });
    }

    @Override
    public int getItemCount() {
        return moods.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmoji, tvName;

        ViewHolder(View itemView) {
            super(itemView);
            tvEmoji = itemView.findViewById(R.id.tvMoodEmoji);
            tvName = itemView.findViewById(R.id.tvMoodName);
        }
    }
}
