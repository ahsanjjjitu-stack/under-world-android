package com.example.underworld.order_pack;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;


public class OrderHistoryViewModel extends ViewModel {

    private final OrderHistoryRepository repository;

    public OrderHistoryViewModel() {
        repository = new OrderHistoryRepository();
    }

    public LiveData<OrderListResponse> fetchCart(String token, int limit, String lastCreatedAt) {
        return repository.getUserOrders(token, limit, lastCreatedAt);
    }

}
