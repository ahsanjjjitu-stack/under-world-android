package com.example.underworld.details_pack;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.underworld.R;

import java.util.ArrayList;
import java.util.List;

public class SizeAdapter extends RecyclerView.Adapter<SizeAdapter.SizeViewHolder>{

    private final Context context;
    private final List<String> sizeList = new ArrayList<>();
    private int selectedPosition = 0;
    private final OnSizeClickListener listener;

    public interface OnSizeClickListener {
        void onSizeClick(String size, int position);
    }

    public SizeAdapter(Context context, OnSizeClickListener listener) {
        this.context = context;
        this.listener = listener;
    }




    public void setSizes(List<String> sizes) {
        this.sizeList.clear();
        if (sizes != null) {
            this.sizeList.addAll(sizes);
        }
        this.selectedPosition = 0;
        notifyDataSetChanged();
    }




    @NonNull
    @Override
    public SizeAdapter.SizeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_size, parent, false);
        return new SizeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SizeAdapter.SizeViewHolder holder, int position) {
        String size = sizeList.get(position);
        boolean isSelected = (position == selectedPosition);

        holder.tvSize.setText(size);

        holder.tvSize.setSelected(isSelected);

        if (isSelected) {
            holder.tvSize.setTextColor(Color.parseColor("#0E1116"));
        } else {
            holder.tvSize.setTextColor(Color.WHITE);
        }

        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onSizeClick(size, selectedPosition);
            }
        });

    }

    @Override
    public int getItemCount() {
        return sizeList.size();
    }

    public class SizeViewHolder extends RecyclerView.ViewHolder {
        TextView tvSize;
        public SizeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSize = itemView.findViewById(R.id.tvCategory);
        }
    }
}
