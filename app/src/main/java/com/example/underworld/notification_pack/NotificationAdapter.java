package com.example.underworld.notification_pack;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.underworld.R;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>{

    private final Context context;
    private List<NotificationModel> notificationList;
    private final OnNotificationClickListener listener;


    public interface OnNotificationClickListener {
        void onItemClick(NotificationModel notification, int position);
        void onItemLongClick(NotificationModel notification, int position);
    }

    public NotificationAdapter(Context context, OnNotificationClickListener listener) {
        this.context = context;
        this.notificationList = new ArrayList<>();
        this.listener = listener;
    }

    public void setNotificationList(List<NotificationModel> notificationList) {
        this.notificationList = notificationList != null ? notificationList : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addNotifications(List<NotificationModel> newNotifications) {
        if (newNotifications != null && !newNotifications.isEmpty()) {
            int startPosition = this.notificationList.size();
            this.notificationList.addAll(newNotifications);
            notifyItemRangeInserted(startPosition, newNotifications.size());
        }
    }


    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {

        NotificationModel notification = notificationList.get(position);

        if (notification.isRead()) {
            holder.notifRoot.setBackgroundResource(R.drawable.bg_notif_read);
        } else {
            holder.notifRoot.setBackgroundResource(R.drawable.bg_notif_unread);
        }

        String orderId = notification.getOrderId();
        String shortOrderId = (orderId != null && orderId.length() >= 6)
                ? orderId.substring(orderId.length() - 6).toUpperCase()
                : orderId;

        holder.tvTitle.setText("Order #" + shortOrderId);
        holder.tvMessage.setText(notification.getBody());
        holder.tvTime.setText(getRelativeTime(notification.getCreatedAt()));


        if ("ORDER_CONFIRMED".equals(notification.getType())) {
            holder.tvStatus.setText("Order confirmed");
        } else {
            holder.tvStatus.setText("Order placed");
        }



        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(notification, holder.getAdapterPosition());
            }
        });


        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onItemLongClick(notification, holder.getAdapterPosition());
                return true;
            }
            return false;
        });

    }


    private String getRelativeTime(String createdAt) {
        if (createdAt == null || createdAt.trim().isEmpty()) {
            return "";
        }

        try {
            Instant instant = null;

            if (createdAt.endsWith("Z") || createdAt.contains("+")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    instant = Instant.parse(createdAt);
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    instant = LocalDateTime
                            .parse(createdAt)
                            .toInstant(ZoneOffset.UTC);
                }
            }

            long diffMillis = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                diffMillis = System.currentTimeMillis() - instant.toEpochMilli();
            }

            if (diffMillis < 0) {
                diffMillis = 0;
            }

            long seconds = diffMillis / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;
            long weeks = days / 7;

            if (seconds < 60) {
                return "Just now";
            } else if (minutes < 60) {
                return minutes + (minutes == 1 ? " min ago" : " mins ago");
            } else if (hours < 24) {
                return hours + (hours == 1 ? " hour ago" : " hours ago");
            } else if (days < 7) {
                return days + (days == 1 ? " day ago" : " days ago");
            } else if (weeks < 5) {
                return weeks + (weeks == 1 ? " week ago" : " weeks ago");
            } else if (days < 365) {
                long months = days / 30;
                return months + (months == 1 ? " month ago" : " months ago");
            } else {
                long years = days / 365;
                return years + (years == 1 ? " year ago" : " years ago");
            }

        } catch (Exception e) {
            Log.e("NotificationAdapter", "Invalid notification date: " + createdAt, e);
            return createdAt;
        }
    }


    @Override
    public int getItemCount() {
        return notificationList.size();
    }


    public class NotificationViewHolder extends RecyclerView.ViewHolder {

        LinearLayout notifRoot;
        TextView tvTitle, tvMessage, tvStatus, tvTime;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            notifRoot = itemView.findViewById(R.id.notifRoot);
            tvTitle = itemView.findViewById(R.id.tvNotifTitle);
            tvMessage = itemView.findViewById(R.id.tvNotifMessage);
            tvStatus = itemView.findViewById(R.id.tvNotifStatus);
            tvTime = itemView.findViewById(R.id.tvNotifTime);
        }
    }
}