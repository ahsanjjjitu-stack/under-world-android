package com.example.underworld.main_pack;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.underworld.R;
import com.example.underworld.order_details_pack.OrderDetailsViewModel;
import com.example.underworld.order_details_pack.OrderItemAdapter;
import com.example.underworld.order_details_pack.OrderItemModel;
import com.example.underworld.order_details_pack.OrderModel;
import com.example.underworld.order_details_pack.PaymentModel;
import com.example.underworld.order_details_pack.ShippingAddressModel;

public class OrderDetailsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvOrderId, tvStatusChip, tvPlaced;
    private View viewTrkLine, viewTrkConfirmed, ivTrkConfirmedTick;
    private TextView tvTrkPlacedTime, tvTrkConfirmedTitle, tvTrkConfirmedTime;
    private TextView tvAddrName, tvAddrPhone, tvAddrLine;
    private TextView tvPayMethod, tvPayLabel2, tvPayValue2, tvTrxId;
    private LinearLayout layoutTrx;
    private TextView tvSubtotal, tvDelivery, tvTotal;

    private OrderDetailsViewModel viewModel;
    private String orderId, userToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_details);


        orderId = getIntent().getStringExtra("orderId");
        userToken = "Bearer " + getSharedPreferences("UserDetails", MODE_PRIVATE).getString("JWT_TOKEN", "");


        btnBack = findViewById(R.id.btnBack);
        tvOrderId = findViewById(R.id.tvOrderId);
        tvStatusChip = findViewById(R.id.tvStatusChip);
        tvPlaced = findViewById(R.id.tvPlaced);

        viewTrkLine = findViewById(R.id.viewTrkLine);
        viewTrkConfirmed = findViewById(R.id.viewTrkConfirmed);
        ivTrkConfirmedTick = findViewById(R.id.ivTrkConfirmedTick);
        tvTrkPlacedTime = findViewById(R.id.tvTrkPlacedTime);
        tvTrkConfirmedTitle = findViewById(R.id.tvTrkConfirmedTitle);
        tvTrkConfirmedTime = findViewById(R.id.tvTrkConfirmedTime);

        tvAddrName = findViewById(R.id.tvAddrName);
        tvAddrPhone = findViewById(R.id.tvAddrPhone);
        tvAddrLine = findViewById(R.id.tvAddrLine);

        tvPayMethod = findViewById(R.id.tvPayMethod);
        tvPayLabel2 = findViewById(R.id.tvPayLabel2);
        tvPayValue2 = findViewById(R.id.tvPayValue2);
        tvTrxId = findViewById(R.id.tvTrxId);
        layoutTrx = findViewById(R.id.layoutTrx);

        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvDelivery = findViewById(R.id.tvDelivery);
        tvTotal = findViewById(R.id.tvTotal);




        viewModel = new ViewModelProvider(this).get(OrderDetailsViewModel.class);




        btnBack.setOnClickListener(v -> finish());

        if (orderId != null && !orderId.isEmpty()) {
            fetchOrderDetails();
        } else {
            Toast.makeText(this, "Invalid Order ID", Toast.LENGTH_SHORT).show();
            finish();
        }







    }





    // ******************************************* status bar code *************************************
    private void setSystemBarColors() {
        getWindow().setStatusBarColor(Color.parseColor("#0D1017"));
        getWindow().setNavigationBarColor(Color.parseColor("#0D1017"));

        getWindow().getDecorView().setSystemUiVisibility(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setSystemBarColors();
    }








    private void fetchOrderDetails() {
        viewModel.fetchOrderDetails(userToken, orderId).observe(this, response -> {
            if (response != null && response.getData() != null) {
                bindOrderData(response.getData());
            } else {
                Toast.makeText(this, "Failed to load order details", Toast.LENGTH_SHORT).show();
            }
        });
    }








    private void bindOrderData(OrderModel order) {
        String fullId = order.getId();
        String shortId = (fullId != null && fullId.length() >= 6)
                ? fullId.substring(fullId.length() - 6).toUpperCase()
                : fullId;
        tvOrderId.setText("Order #" + shortId);



        // ৩. Order Status & Chip
        String status = order.getOrderStatus();
        if ("confirmed".equalsIgnoreCase(status)) {
            tvStatusChip.setText("Confirmed");
            tvStatusChip.setBackgroundResource(R.drawable.bg_notif_badge);
            tvStatusChip.setTextColor(getResources().getColor(R.color.uw_accent));

            tvTrkConfirmedTitle.setText("Order confirmed");
            tvTrkConfirmedTitle.setTextColor(getResources().getColor(R.color.uw_text));
            tvTrkConfirmedTime.setText("Seller has confirmed your order");
            viewTrkConfirmed.setBackgroundResource(R.drawable.bg_badge);
            viewTrkLine.setBackgroundResource(R.drawable.bg_confrim_trk_line);
            ivTrkConfirmedTick.setVisibility(View.VISIBLE);
        } else {
            tvStatusChip.setText("Pending");
            tvStatusChip.setBackgroundResource(R.drawable.bg_chip_pending);
            tvStatusChip.setTextColor(getResources().getColor(R.color.uw_amber));
            viewTrkLine.setBackgroundResource(R.drawable.bg_trk_line);
            tvTrkConfirmedTitle.setText("Waiting for confirmation");
            tvTrkConfirmedTime.setText("We will notify you");
            viewTrkConfirmed.setBackgroundResource(R.drawable.bg_trk_dot);
            ivTrkConfirmedTick.setVisibility(View.GONE);
        }




        tvPlaced.setText("Placed on: " + formatOrderDate(order.getCreatedAt()));
        tvTrkPlacedTime.setText("" + formatOrderDate(order.getCreatedAt()));



        RecyclerView rvOrderItems = findViewById(R.id.rvOrderItems);
        rvOrderItems.setLayoutManager(new LinearLayoutManager(this));
        OrderItemAdapter orderItemAdapter = new OrderItemAdapter(this);

        if (order.getItems() != null) {
            orderItemAdapter.setItems(order.getItems());
        }

        rvOrderItems.setAdapter(orderItemAdapter);



        // ৪. Delivery Address
        if (order.getShippingAddress() != null) {
            ShippingAddressModel addr = order.getShippingAddress();
            tvAddrName.setText(addr.getFullName());
            tvAddrPhone.setText(addr.getPhone());
            tvAddrLine.setText(addr.getAddressLine() + ", " + addr.getArea() + ", " + addr.getDistrict());
        }

        // ৫. Payment Info
        if (order.getPayment() != null) {
            PaymentModel payment = order.getPayment();
            String method = payment.getMethod().toUpperCase();
            tvPayMethod.setText(method);

            if ("COD".equalsIgnoreCase(payment.getMethod())) {
                tvPayLabel2.setVisibility(View.GONE);
                tvPayValue2.setVisibility(View.GONE);
                layoutTrx.setVisibility(View.GONE);
            } else {
                tvPayLabel2.setVisibility(View.VISIBLE);
                tvPayValue2.setVisibility(View.VISIBLE);
                layoutTrx.setVisibility(View.VISIBLE);

                tvPayValue2.setText(payment.getSenderNumber());
                tvTrxId.setText(payment.getTransactionId());
            }
        }

        // ৬. Summary Calculation & Display
        double subtotal = order.getTotalAmount();
        double deliveryFee = 60;

        double total = subtotal + deliveryFee;

        tvSubtotal.setText("BDT " + subtotal);
        tvDelivery.setText( "BDT " + deliveryFee);
        tvTotal.setText("BDT " + total);
    }








    private String formatOrderDate(String isoDateStr) {
        if (isoDateStr == null || isoDateStr.isEmpty()) return "";

        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                java.time.Instant instant = java.time.Instant.parse(isoDateStr);
                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter
                        .ofPattern("dd MMM yyyy, hh:mm a")
                        .withZone(java.time.ZoneId.systemDefault());
                return formatter.format(instant);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isoDateStr;
    }



}