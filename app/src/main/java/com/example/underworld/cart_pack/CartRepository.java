package com.example.underworld.cart_pack;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.underworld.api_pack.ApiService;
import com.example.underworld.api_pack.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartRepository {

    private final ApiService apiService;

    public CartRepository() {
        apiService = RetrofitClient.getInstance().getApi();
    }


    public LiveData<CartResponse> getCart(String token, int page, int limit) {
        MutableLiveData<CartResponse> data = new MutableLiveData<>();

        apiService.getCart(token, page, limit).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                data.setValue(null);
            }
        });

        return data;
    }
}
