package com.example.underworld.home_pack;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.underworld.R;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder>{

    private Context context;
    private List<BannerModel> bannerList;
    private OnBannerClickListener listener;





    public interface OnBannerClickListener {
        void onBannerClick(BannerModel banner);
    }

    public BannerAdapter(Context context, List<BannerModel> bannerList, OnBannerClickListener listener) {
        this.context = context;
        this.bannerList = bannerList;
        this.listener = listener;
    }

    public void updateData(List<BannerModel> newBannerList) {
        this.bannerList = newBannerList;
        notifyDataSetChanged();
    }







    @NonNull
    @Override
    public BannerAdapter.BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }






    @Override
    public void onBindViewHolder(@NonNull BannerAdapter.BannerViewHolder holder, int position) {

        BannerModel banner = bannerList.get(position);

        FrameLayout bannerCard =
                holder.itemView.findViewById(R.id.bannerCard);

        GradientDrawable roundedBackground = new GradientDrawable();
        roundedBackground.setColor(Color.TRANSPARENT);
        roundedBackground.setCornerRadius(
                holder.itemView.getResources()
                        .getDisplayMetrics().density * 30f
        );

        bannerCard.setBackground(roundedBackground);
        bannerCard.setClipToOutline(true);


        holder.tvTag.setText(banner.getHead());
        holder.tvTitle.setText(banner.getBody());
        holder.tvAction.setText(banner.getTitle());

        Glide.with(context)
                .load(banner.getImage())
                .centerCrop()
                .into(holder.ivBanner);


        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBannerClick(banner);
            }
        });



        holder.tvAction.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBannerClick(banner);
            }
        });


    }






    @Override
    public int getItemCount() {
        return bannerList == null ? 0 : bannerList.size();
    }





    public class BannerViewHolder extends RecyclerView.ViewHolder {

        ImageView ivBanner;
        TextView tvTag, tvTitle, tvAction;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBanner = itemView.findViewById(R.id.ivBanner);
            tvTag = itemView.findViewById(R.id.tvTag);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAction = itemView.findViewById(R.id.tvAction);
        }
    }
}
