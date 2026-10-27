package com.example.underworld.home_pack;

import android.util.Log; // Log import kora

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.underworld.api_pack.ApiService;
import com.example.underworld.api_pack.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeRepository {

    private ApiService apiService;

    public HomeRepository() {
        apiService = RetrofitClient.getInstance().getApi();
    }

    public LiveData<HomeDataResponse> fetchHomeData() {
        MutableLiveData<HomeDataResponse> data = new MutableLiveData<>();

        apiService.getHomeData().enqueue(new Callback<HomeDataResponse>() {
            @Override
            public void onResponse(Call<HomeDataResponse> call, Response<HomeDataResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<HomeDataResponse> call, Throwable t) {
                data.setValue(null);
            }
        });

        return data;
    }
}