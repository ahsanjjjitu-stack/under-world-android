package com.example.underworld.main_pack;

import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.underworld.R;
import com.example.underworld.api_pack.ApiService;
import com.example.underworld.api_pack.RetrofitClient;
import com.example.underworld.notification_pack.NotificationAdapter;
import com.example.underworld.notification_pack.NotificationModel;
import com.example.underworld.notification_pack.NotificationViewModel;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {
    private ImageButton btnBack;
    private TextView btnReadAll;
    private RecyclerView rvNotifications;
    private ProgressBar progressPagination;
    private LinearLayout layoutEmptyNotif;

    private NotificationViewModel viewModel;
    private NotificationAdapter adapter;
    private List<NotificationModel> notificationList = new ArrayList<>();

    private String userToken = "";
    private String nextCursor = null;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private final int LIMIT = 10;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);


        btnBack = findViewById(R.id.btnBack);
        btnReadAll = findViewById(R.id.btnReadAll);
        rvNotifications = findViewById(R.id.rvNotifications);
        progressPagination = findViewById(R.id.progressPagination);
        layoutEmptyNotif = findViewById(R.id.layoutEmptyNotif);





        SharedPreferences prefs = getSharedPreferences("UserDetails", MODE_PRIVATE);
        userToken = "Bearer " + prefs.getString("JWT_TOKEN", "");





        viewModel = new ViewModelProvider(this).get(NotificationViewModel.class);




        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvNotifications.setLayoutManager(layoutManager);





        adapter = new NotificationAdapter(this, new NotificationAdapter.OnNotificationClickListener() {

            @Override
            public void onItemClick(NotificationModel notification, int position) {
                notification.setRead(true);
                adapter.notifyItemChanged(position);

                ApiService apiService = RetrofitClient.getInstance().getApi();
                apiService.markNotificationAsRead(userToken, notification.get_id()).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        android.util.Log.e("NotifApiDebug", "Network Failure: " + t.getMessage(), t);
                    }
                });

                if (notification.getOrderId() != null && !notification.getOrderId().isEmpty()) {
                    Intent intent = new Intent(NotificationActivity.this, OrderDetailsActivity.class);
                    intent.putExtra("orderId", notification.getOrderId());
                    startActivity(intent);
                }
            }





            @Override
            public void onItemLongClick(NotificationModel notification, int position) {
                showDeleteDialog(notification, position);
            }
        });




        rvNotifications.setAdapter(adapter);





        rvNotifications.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= LIMIT) {
                        loadNotifications(false);
                    }
                }
            }
        });







        btnBack.setOnClickListener(v -> finish());
        btnReadAll.setOnClickListener(v -> {
            Toast.makeText(NotificationActivity.this, "Marked all as read", Toast.LENGTH_SHORT).show();
        });

        loadNotifications(true);








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













    private void loadNotifications(boolean isFirstPage) {
        isLoading = true;

        if (isFirstPage) {
            nextCursor = null;
            isLastPage = false;
            progressPagination.setVisibility(View.GONE);
        } else {
            progressPagination.setVisibility(View.VISIBLE);
        }

        viewModel.fetchNotifications(userToken, LIMIT, nextCursor).observe(this, response -> {
            isLoading = false;
            progressPagination.setVisibility(View.GONE);

            if (response != null && response.isSuccess() && response.getData() != null) {
                List<NotificationModel> newNotifications = response.getData();

                if (isFirstPage) {
                    notificationList = new ArrayList<>(newNotifications);
                    adapter.setNotificationList(notificationList);
                } else {
                    notificationList.addAll(newNotifications);
                    adapter.addNotifications(newNotifications);
                }

                nextCursor = response.getNextCursor();

                if (nextCursor == null || newNotifications.size() < LIMIT) {
                    isLastPage = true;
                }

                updateUiState();

            } else {
                if (isFirstPage) {
                    updateUiState();
                }
            }
        });
    }







    private void updateUiState() {
        if (notificationList.isEmpty()) {
            rvNotifications.setVisibility(View.GONE);
            layoutEmptyNotif.setVisibility(View.VISIBLE);
        } else {
            rvNotifications.setVisibility(View.VISIBLE);
            layoutEmptyNotif.setVisibility(View.GONE);
        }
    }











    private void showDeleteDialog(NotificationModel notification, int position) {
        android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete_notification);

        android.view.Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));

            window.addFlags(android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setDimAmount(0.75f);

            android.view.WindowManager.LayoutParams params = window.getAttributes();
            params.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
            params.gravity = android.view.Gravity.CENTER;
            window.setAttributes(params);
        }

        TextView btnCancelDelete = dialog.findViewById(R.id.btnCancelDelete);
        TextView btnConfirmDelete = dialog.findViewById(R.id.btnConfirmDelete);

        btnCancelDelete.setOnClickListener(v -> dialog.dismiss());

        btnConfirmDelete.setOnClickListener(v -> {
            dialog.dismiss();

            ApiService apiService = RetrofitClient.getInstance().getApi();
            apiService.deleteNotification(userToken, notification.get_id()).enqueue(new Callback<okhttp3.ResponseBody>() {
                @Override
                public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                    if (response.isSuccessful()) {
                        notificationList.remove(position);
                        adapter.notifyItemRemoved(position);
                        adapter.notifyItemRangeChanged(position, notificationList.size());

                        updateUiState();

                        Toast.makeText(NotificationActivity.this, "Notification deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(NotificationActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                    android.widget.Toast.makeText(NotificationActivity.this, "Network error: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }




}