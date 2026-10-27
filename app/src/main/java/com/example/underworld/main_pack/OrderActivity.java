package com.example.underworld.main_pack;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.underworld.R;
import com.example.underworld.order_pack.OrderHistoryAdapter;
import com.example.underworld.order_pack.OrderHistoryViewModel;
import com.example.underworld.order_pack.OrderListItemModel;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity {
    private ImageButton btnBack;
    private TextView tvOrderCount;
    private RecyclerView rvMyOrders;
    private ProgressBar progressPagination;
    private LinearLayout layoutEmptyOrders;

    private OrderHistoryViewModel viewModel;
    private OrderHistoryAdapter adapter;
    private String userToken;

    private List<OrderListItemModel> orderList = new ArrayList<>();
    private String lastCreatedAt = null;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order);


        userToken = "Bearer " + getSharedPreferences("UserDetails", MODE_PRIVATE).getString("JWT_TOKEN", "");



        btnBack = findViewById(R.id.btnBack);
        tvOrderCount = findViewById(R.id.tvOrderCount);
        rvMyOrders = findViewById(R.id.rvMyOrders);
        progressPagination = findViewById(R.id.progressPagination);
        layoutEmptyOrders = findViewById(R.id.layoutEmptyOrders);


        viewModel = new ViewModelProvider(this).get(OrderHistoryViewModel.class);



        rvMyOrders.setLayoutManager(new LinearLayoutManager(this));

        adapter = new OrderHistoryAdapter(this, orderId -> {
            Intent intent = new Intent(OrderActivity.this, OrderDetailsActivity.class);
            intent.putExtra("orderId", orderId);
            startActivity(intent);
        });

        rvMyOrders.setAdapter(adapter);




        rvMyOrders.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        int visibleItemCount = layoutManager.getChildCount();
                        int totalItemCount = layoutManager.getItemCount();
                        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                        if (!isLoading && !isLastPage) {
                            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                                    && firstVisibleItemPosition >= 0) {
                                loadNextPage();
                            }
                        }
                    }
                }
            }
        });






        btnBack.setOnClickListener(v -> finish());

        loadFirstPage();



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












    private void loadFirstPage() {
        isLoading = true;
        lastCreatedAt = null;
        isLastPage = false;


        viewModel.fetchCart(userToken, 10, null).observe(this, response -> {
            isLoading = false;
            if (response != null && response.isSuccess()) {
                List<OrderListItemModel> fetchedOrders = response.getData();
                orderList.clear();

                if (fetchedOrders != null && !fetchedOrders.isEmpty()) {
                    orderList.addAll(fetchedOrders);
                    adapter.setOrders(orderList);

                    lastCreatedAt = orderList.get(orderList.size() - 1).getCreatedAt();

                    rvMyOrders.setVisibility(View.VISIBLE);
                    layoutEmptyOrders.setVisibility(View.GONE);
                } else {
                    rvMyOrders.setVisibility(View.GONE);
                    layoutEmptyOrders.setVisibility(View.VISIBLE);
                    isLastPage = true;
                }

                tvOrderCount.setText(orderList.size() + " items");

                if (fetchedOrders == null || fetchedOrders.size() < 10) {
                    isLastPage = true;
                }
            } else {
                rvMyOrders.setVisibility(View.GONE);
                layoutEmptyOrders.setVisibility(View.VISIBLE);
            }
        });
    }














    private void loadNextPage() {
        if (isLastPage || lastCreatedAt == null) return;

        isLoading = true;
        progressPagination.setVisibility(View.VISIBLE);

        viewModel.fetchCart(userToken, 10, lastCreatedAt).observe(this, response -> {
            isLoading = false;
            progressPagination.setVisibility(View.GONE);

            if (response != null && response.isSuccess()) {
                List<OrderListItemModel> fetchedOrders = response.getData();

                if (fetchedOrders != null && !fetchedOrders.isEmpty()) {
                    int oldSize = orderList.size();
                    orderList.addAll(fetchedOrders);
                    adapter.notifyItemRangeInserted(oldSize, fetchedOrders.size());

                    lastCreatedAt = orderList.get(orderList.size() - 1).getCreatedAt();
                    tvOrderCount.setText(orderList.size() + " items");

                    if (fetchedOrders.size() < 10) {
                        isLastPage = true;
                    }
                } else {
                    isLastPage = true;
                }
            } else {
                Toast.makeText(this, "Failed to load more orders", Toast.LENGTH_SHORT).show();
            }
        });
    }




}