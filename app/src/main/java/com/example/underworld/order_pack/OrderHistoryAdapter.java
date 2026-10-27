package com.example.underworld.order_pack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.underworld.R;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private final Context context;
    private List<OrderListItemModel> orderList;
    private final OnOrderClickListener listener;


    public interface OnOrderClickListener {
        void onOrderClick(String orderId);
    }

    public OrderHistoryAdapter(Context context, OnOrderClickListener listener) {
        this.context = context;
        this.orderList = new ArrayList<>();
        this.listener = listener;
    }

    public void setOrders(List<OrderListItemModel> orderList) {
        this.orderList = orderList != null ? orderList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderListItemModel order = orderList.get(position);

        String fullId = order.getId();
        String shortId = (fullId != null && fullId.length() >= 6)
                ? fullId.substring(fullId.length() - 6).toUpperCase()
                : fullId;
        holder.tvOrderId.setText("Order #" + shortId);




        String status = order.getOrderStatus();
        if ("confirmed".equalsIgnoreCase(status)) {
            holder.tvStatusChip.setText("Confirmed");
            holder.tvStatusChip.setBackgroundResource(R.drawable.bg_notif_badge);
            holder.tvStatusChip.setTextColor(context.getResources().getColor(R.color.uw_accent));
        } else {
            holder.tvStatusChip.setText("Pending");
            holder.tvStatusChip.setBackgroundResource(R.drawable.bg_chip_pending);
            holder.tvStatusChip.setTextColor(context.getResources().getColor(R.color.uw_amber));
        }




        String formattedDate = formatOrderDate(order.getCreatedAt());
        holder.tvPlaced.setText("Placed on: " + formattedDate + " • ৳" + order.getTotalAmount() + " (" + order.getTotalItemCount() + " qty)");




        holder.itemView.setOnClickListener(v -> {
            if (listener != null && fullId != null) {
                listener.onOrderClick(fullId);
            }
        });


    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    private String formatOrderDate(String isoDateStr) {
        if (isoDateStr == null || isoDateStr.isEmpty()) return "";
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Instant instant = Instant.parse(isoDateStr);
                DateTimeFormatter formatter = DateTimeFormatter
                        .ofPattern("dd MMM yyyy, hh:mm a")
                        .withZone(java.time.ZoneId.systemDefault());
                return formatter.format(instant);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isoDateStr;
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvStatusChip, tvPlaced;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvStatusChip = itemView.findViewById(R.id.tvStatusChip);
            tvPlaced = itemView.findViewById(R.id.tvPlaced);
        }
    }
}
