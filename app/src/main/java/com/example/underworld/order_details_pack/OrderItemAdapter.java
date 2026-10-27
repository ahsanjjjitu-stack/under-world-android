package com.example.underworld.order_details_pack;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.underworld.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {

    private final Context context;
    private List<OrderItemModel> itemList;

    public OrderItemAdapter(Context context) {
        this.context = context;
        this.itemList = new ArrayList<>();
    }

    public void setItems(List<OrderItemModel> itemList) {
        this.itemList = itemList != null ? itemList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail_item, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        OrderItemModel item = itemList.get(position);

        if (position == 0) {
            holder.viewItemDivider.setVisibility(View.GONE);
        } else {
            holder.viewItemDivider.setVisibility(View.VISIBLE);
        }

        holder.tvItemName.setText(item.getName());

        String imageUrl = (item.getSelectedColour() != null) ? item.getSelectedColour().getImage() : "";
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .into(holder.ivGarment);
        }

        String colorName = (item.getSelectedColour() != null) ? item.getSelectedColour().getName() : "";
        String size = item.getSelectedSize();
        holder.tvItemMeta.setText(colorName + " • " + size);

        try {
            if (item.getSelectedColour() != null && item.getSelectedColour().getHex() != null) {
                String hex = item.getSelectedColour().getHex();
                GradientDrawable drawable = (GradientDrawable) holder.viewColourDot.getBackground();
                drawable.setColor(Color.parseColor(hex));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        double price = item.getPrice();
        int qty = item.getQuantity();
        double totalItemPrice = price * qty;

        holder.tvItemTotal.setText("৳" + totalItemPrice);
        holder.tvItemQtyPrice.setText(qty + " x ৳" + price);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class OrderItemViewHolder extends RecyclerView.ViewHolder {

        View viewItemDivider, viewColourDot;
        ShapeableImageView ivGarment;
        TextView tvItemName, tvItemMeta, tvItemTotal, tvItemQtyPrice;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            viewItemDivider = itemView.findViewById(R.id.viewItemDivider);
            viewColourDot = itemView.findViewById(R.id.viewColourDot);
            ivGarment = itemView.findViewById(R.id.ivGarment);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemMeta = itemView.findViewById(R.id.tvItemMeta);
            tvItemTotal = itemView.findViewById(R.id.tvItemTotal);
            tvItemQtyPrice = itemView.findViewById(R.id.tvItemQtyPrice);
        }
    }
}
