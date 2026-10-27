package com.example.underworld.order_details_pack;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.underworld.api_pack.ApiService;
import com.example.underworld.api_pack.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRepository {
    private final ApiService apiService;

    public OrderRepository() {
        this.apiService = RetrofitClient.getInstance().getApi();
    }


    public LiveData<OrderDetailsResponse> getOrderDetails(String token, String orderId) {
        MutableLiveData<OrderDetailsResponse> orderData = new MutableLiveData<>();

        apiService.getOrderDetails(token, orderId).enqueue(new Callback<OrderDetailsResponse>() {
            @Override
            public void onResponse(Call<OrderDetailsResponse> call, Response<OrderDetailsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orderData.setValue(response.body());
                } else {
                    OrderDetailsResponse errorResponse = new OrderDetailsResponse();
                    orderData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<OrderDetailsResponse> call, Throwable t) {
                orderData.setValue(null);
            }
        });

        return orderData;
    }


}
