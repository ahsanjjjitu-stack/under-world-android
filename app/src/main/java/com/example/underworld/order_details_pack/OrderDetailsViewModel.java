package com.example.underworld.order_details_pack;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OrderDetailsViewModel extends ViewModel {

    private final OrderRepository repository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public OrderDetailsViewModel() {
        this.repository = new OrderRepository();
    }

    public LiveData<OrderDetailsResponse> fetchOrderDetails(String token, String orderId) {
        isLoading.setValue(true);
        MutableLiveData<OrderDetailsResponse> result = new MutableLiveData<>();

        repository.getOrderDetails(token, orderId).observeForever(response -> {
            isLoading.setValue(false);
            if (response != null && response.isSuccess()) {
                result.setValue(response);
            } else {
                errorMessage.setValue("Failed to fetch order details!");
                result.setValue(null);
            }
        });

        return result;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
}
