package com.example.underworld.cart_pack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.underworld.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>{

    private final Context context;
    private List<CartItemModel> cartItems;
    private final OnCartActionListener listener;

    // Interface for button clicks (Stepper & Remove)
    public interface OnCartActionListener {
        void onIncreaseClick(CartItemModel item, int position);
        void onDecreaseClick(CartItemModel item, int position);
        void onRemoveClick(CartItemModel item, int position);
    }



    public CartAdapter(Context context, OnCartActionListener listener) {
        this.context = context;
        this.cartItems = new ArrayList<>();
        this.listener = listener;
    }


    public void setCartItems(List<CartItemModel> cartItems) {
        this.cartItems = cartItems != null ? cartItems : new ArrayList<>();
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public CartAdapter.CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull CartAdapter.CartViewHolder holder, int position) {

        CartItemModel item = cartItems.get(position);

        holder.tvName.setText(item.getName());

        String colourName = (item.getSelectedColour() != null) ? item.getSelectedColour().getName() : "";
        String size = item.getSelectedSize() != null ? item.getSelectedSize() : "";
        String metaText = colourName + (!colourName.isEmpty() && !size.isEmpty() ? " · Size " : "Size ") + size;
        holder.tvMeta.setText(metaText);


        String currency = item.getCurrency() != null ? item.getCurrency() : "BDT";
        holder.tvEach.setText(currency + " " + item.getPrice() + " each");

        holder.tvQty.setText(String.valueOf(item.getQuantity()));

        double lineTotal = item.getPrice() * item.getQuantity();
        holder.tvLineTotal.setText(currency + " " + (int) lineTotal);



        String imageUrl = "";
        if (item.getSelectedColour() != null && item.getSelectedColour().getImage() != null) {
            imageUrl = item.getSelectedColour().getImage();
        } else {
            imageUrl = item.getThumbnail();
        }

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .centerCrop()
                    .into(holder.ivProduct);
        } else {
            holder.ivProduct.setImageResource(R.drawable.bg_product_tile);
        }



        holder.btnPlus.setOnClickListener(v -> {
            if (listener != null) {
                listener.onIncreaseClick(item, holder.getAdapterPosition());
            }
        });

        holder.btnMinus.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDecreaseClick(item, holder.getAdapterPosition());
            }
        });

        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveClick(item, holder.getAdapterPosition());
            }
        });




    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView ivProduct;
        TextView tvName, tvMeta, tvEach, tvQty, tvLineTotal;
        ImageButton btnPlus, btnMinus, btnRemove;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            tvName = itemView.findViewById(R.id.tvName);
            tvMeta = itemView.findViewById(R.id.tvMeta);
            tvEach = itemView.findViewById(R.id.tvEach);
            tvQty = itemView.findViewById(R.id.tvQty);
            tvLineTotal = itemView.findViewById(R.id.tvLineTotal);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}
