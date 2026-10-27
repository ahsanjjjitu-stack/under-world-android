package com.example.underworld.home_pack;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.underworld.R;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder>{

    private final Context context;
    private final List<ProductModel> productList = new ArrayList<>();
    private final OnProductClickListener listener;
    public interface OnProductClickListener {
        void onItemClick(ProductModel product, int position);
        void onAddClick(ProductModel product, int position);
    }
    public ProductAdapter(Context context, OnProductClickListener listener) {
        this.context = context;
        this.listener = listener;
    }





    public void setProducts(List<ProductModel> products) {
        this.productList.clear();
        if (products != null) {
            this.productList.addAll(products);
        }
        notifyDataSetChanged();
    }

    public void addProducts(List<ProductModel> newProducts) {
        if (newProducts != null && !newProducts.isEmpty()) {
            int startPosition = this.productList.size();
            this.productList.addAll(newProducts);
            notifyItemRangeInserted(startPosition, newProducts.size());
        }
    }




    @NonNull
    @Override
    public ProductAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ProductViewHolder holder, int position) {
        ProductModel product = productList.get(position);

        holder.tvName.setText(product.getName());
        holder.tvType.setText(product.getCategory());
        holder.tvPrice.setText(product.getCurrency() + " " + product.getPrice());



        if (product.getThumbnail() != null && !product.getThumbnail().isEmpty()) {
            Glide.with(context)
                    .load(product.getThumbnail())
                    .placeholder(R.drawable.bg_product_tile)
                    .centerCrop()
                    .into(holder.ivProduct);
        } else {
            holder.ivProduct.setImageResource(R.drawable.bg_product_tile);
        }



        holder.openArea.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(product, holder.getAdapterPosition());
            }
        });

        // 2. Add button (+) click listener
        holder.btnAdd.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddClick(product, holder.getAdapterPosition());
            }
        });





    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        View openArea;
        ImageView ivProduct;
        TextView tvName, tvType, tvPrice;
        ImageButton btnAdd;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            openArea = itemView.findViewById(R.id.openArea);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            tvName = itemView.findViewById(R.id.tvName);
            tvType = itemView.findViewById(R.id.tvType);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnAdd = itemView.findViewById(R.id.btnAdd);
        }
    }
}
