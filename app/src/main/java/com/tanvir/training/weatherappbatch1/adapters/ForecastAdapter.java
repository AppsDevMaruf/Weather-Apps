package com.tanvir.training.weatherappbatch1.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tanvir.training.weatherappbatch1.databinding.ForecastRowBinding;
import com.tanvir.training.weatherappbatch1.models.forecast.ListItem;

import java.util.ArrayList;
import java.util.List;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {
    private List<ListItem> items = new ArrayList<>();

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final ForecastRowBinding binding = ForecastRowBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent, false);
        return new ForecastViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        final ListItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ForecastViewHolder extends RecyclerView.ViewHolder {
        private ForecastRowBinding binding;
        public ForecastViewHolder(ForecastRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public void bind(ListItem item) {
            binding.setItem(item);
        }
    }

    public void submitList(List<ListItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }
}
