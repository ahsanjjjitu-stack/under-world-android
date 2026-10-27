package com.example.underworld.details_pack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.underworld.R;

import java.util.ArrayList;
import java.util.List;

public class DetailsImageAdapter extends RecyclerView.Adapter<DetailsImageAdapter.ImageViewHolder>{

    private final Context context;
    private final List<String> imageList = new ArrayList<>();

    public DetailsImageAdapter(Context context) {
        this.context = context;
    }

    public void setImages(List<String> images) {
        imageList.clear();
        if (images != null) {
            imageList.addAll(images);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DetailsImageAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailsImageAdapter.ImageViewHolder holder, int position) {
        String imageUrl = imageList.get(position);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.bg_details_pager)
                    .centerCrop()
                    .into(holder.ivPhoto);
        } else {
            holder.ivPhoto.setImageResource(R.drawable.bg_details_pager);
        }

    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
        }
    }
}
