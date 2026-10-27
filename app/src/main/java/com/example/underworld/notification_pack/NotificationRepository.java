package com.example.underworld.notification_pack;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.underworld.api_pack.ApiService;
import com.example.underworld.api_pack.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationRepository {

    private final ApiService apiService;

    public NotificationRepository() {
        apiService = RetrofitClient.getInstance().getApi();
    }


    public LiveData<NotificationResponse> getNotifications (String token, int limit, String lastCreatedAt) {
        MutableLiveData<NotificationResponse> data = new MutableLiveData<>();

        apiService.getNotifications(token, limit, lastCreatedAt).enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<NotificationResponse> call, Throwable t) {
                data.setValue(null);
            }
        });

        return data;
    }
}
