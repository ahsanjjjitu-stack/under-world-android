package com.example.underworld.home_pack;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.underworld.api_pack.ApiService;
import com.example.underworld.api_pack.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductRepository {

    private final ApiService apiService;

    public ProductRepository() {
        apiService = RetrofitClient.getInstance().getApi();
    }


    public LiveData<ProductResponse> getProducts(int page, int limit, String category) {
        MutableLiveData<ProductResponse> data = new MutableLiveData<>();

        apiService.getProducts(page, limit, category).enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                data.setValue(null);
            }
        });

        return data;
    }




}
