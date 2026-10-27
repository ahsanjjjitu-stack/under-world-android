package com.example.underworld.home_pack;

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

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>{

    private final Context context;
    private final List<CategoryModel> categoryList = new ArrayList<>();
    private int selectedPosition = 0;
    private OnCategoryClickListener listener;




    public interface OnCategoryClickListener {
        void onCategoryClick(CategoryModel category, int position);
    }

    public CategoryAdapter(Context context, OnCategoryClickListener listener) {
        this.context = context;
        this.listener = listener;
    }




    public void setCategories(List<CategoryModel> categories) {
        this.categoryList.clear();

        CategoryModel allCategory = new CategoryModel();
        allCategory.set_id("ALL");
        allCategory.setTitle("All");
        this.categoryList.add(allCategory);

        if (categories != null) {
            this.categoryList.addAll(categories);
        }

        this.selectedPosition = 0;
        notifyDataSetChanged();
    }






    @NonNull
    @Override
    public CategoryAdapter.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.CategoryViewHolder holder, int position) {

        CategoryModel category = categoryList.get(position);
        holder.tvCategoryName.setText(category.getTitle());


        boolean isSelected = (position == selectedPosition);

        // Selected vs Unselected UI State Change
        if (isSelected) {
            holder.tvCategoryName.setBackgroundResource(R.drawable.bg_category_selected);
            holder.tvCategoryName.setTextColor(Color.parseColor("#0E1116"));
        } else {
            holder.tvCategoryName.setBackgroundResource(R.drawable.bg_category_unselected);
            holder.tvCategoryName.setTextColor(Color.parseColor("#FFFFFF"));
        }


        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onCategoryClick(category, selectedPosition);
            }
        });


    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName;
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategory);
        }
    }
}
