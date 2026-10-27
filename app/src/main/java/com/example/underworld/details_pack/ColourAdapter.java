package com.example.underworld.details_pack;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.underworld.R;
import com.example.underworld.home_pack.ColourModel;

import java.util.ArrayList;
import java.util.List;

public class ColourAdapter extends RecyclerView.Adapter<ColourAdapter.ColourViewHolder>{

    private final Context context;
    private final List<ColourModel> colourList = new ArrayList<>();
    private int selectedPosition = 0;
    private final OnColourClickListener listener;




    public interface OnColourClickListener {
        void onColourClick(ColourModel colour, int position);
    }

    public ColourAdapter(Context context, OnColourClickListener listener) {
        this.context = context;
        this.listener = listener;
    }




    public void setColours(List<ColourModel> colours) {
        this.colourList.clear();
        if (colours != null) {
            this.colourList.addAll(colours);
        }
        this.selectedPosition = 0;
        notifyDataSetChanged();
    }




    @NonNull
    @Override
    public ColourAdapter.ColourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_color, parent, false);
        return new ColourViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColourAdapter.ColourViewHolder holder, int position) {

        ColourModel colour = colourList.get(position);
        boolean isSelected = (position == selectedPosition);
        holder.colourRoot.setSelected(isSelected);



        try {
            GradientDrawable drawable = (GradientDrawable) holder.viewSwatch.getBackground();
            if (drawable != null) {
                drawable.setColor(Color.parseColor(colour.getHex()));
            }
        } catch (Exception e) {
            holder.viewSwatch.setBackgroundColor(Color.parseColor(colour.getHex()));
        }



        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onColourClick(colour, selectedPosition);
            }
        });







    }

    @Override
    public int getItemCount() {
        return colourList.size();
    }

    public class ColourViewHolder extends RecyclerView.ViewHolder {
        View colourRoot;
        View viewSwatch;
        public ColourViewHolder(@NonNull View itemView) {
            super(itemView);
            colourRoot = itemView.findViewById(R.id.colourRoot);
            viewSwatch = itemView.findViewById(R.id.viewSwatch);
        }
    }
}
