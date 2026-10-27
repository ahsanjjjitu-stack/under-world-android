package com.example.underworld.order_pack;

import static com.google.firebase.messaging.Constants.MessageNotificationKeys.TAG;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.underworld.api_pack.ApiService;
import com.example.underworld.api_pack.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryRepository {

    private final ApiService apiService;

    public OrderHistoryRepository() {
        this.apiService = RetrofitClient.getInstance().getApi();
    }

    public LiveData<OrderListResponse> getUserOrders(String token, int limit, String lastCreatedAt) {
        MutableLiveData<OrderListResponse> responseData = new MutableLiveData<>();

        apiService.getUserOrders(token, limit, lastCreatedAt).enqueue(new Callback<OrderListResponse>() {
            @Override
            public void onResponse(Call<OrderListResponse> call, Response<OrderListResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    responseData.setValue(response.body());
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e(TAG, "❌ API Unsuccessful! Code: " + response.code() + " | Error: " + errorBody);
                    } catch (Exception e) {
                        Log.e(TAG, "❌ Error parsing error body: " + e.getMessage());
                    }
                    responseData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<OrderListResponse> call, Throwable t) {
                responseData.setValue(null);
            }
        });

        return responseData;
    }

}
