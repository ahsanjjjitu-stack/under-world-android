package com.example.underworld.checkout_pack;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.underworld.R;
import com.example.underworld.cart_pack.CartItemModel;

import java.util.List;
import java.util.Locale;

public class OrderLineAdapter extends RecyclerView.Adapter<OrderLineAdapter.Holder> {

    private final List<CartItemModel> items;

    public OrderLineAdapter(List<CartItemModel> items) {
        this.items = items;
    }


    @NonNull
    @Override
    public OrderLineAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_line, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderLineAdapter.Holder holder, int position) {

        CartItemModel item = items.get(position);

        holder.tvName.setText(item.getName() + " × " + item.getQuantity());

        String colour = item.getSelectedColour() != null ? item.getSelectedColour().getName() : "";
        holder.tvMeta.setText(colour + " · Size " + item.getSelectedSize());

        holder.tvPrice.setText(money(item.getPrice() * item.getQuantity()));


    }

    @Override
    public int getItemCount() {
        return items.size();
    }



    public static String money(double amount) {
        if (amount == Math.floor(amount)) {
            return "৳" + String.format(Locale.US, "%,.0f", amount);
        }
        return "৳" + String.format(Locale.US, "%,.2f", amount);
    }


    public class Holder extends RecyclerView.ViewHolder {
        TextView tvName, tvMeta, tvPrice;
        public Holder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvLineName);
            tvMeta = itemView.findViewById(R.id.tvLineMeta);
            tvPrice = itemView.findViewById(R.id.tvLinePrice);
        }
    }
}
