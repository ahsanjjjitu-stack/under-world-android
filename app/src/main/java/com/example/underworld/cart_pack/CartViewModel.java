package com.example.underworld.cart_pack;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class CartViewModel extends ViewModel {

    private final CartRepository repository;

    public CartViewModel() {
        repository = new CartRepository();
    }

    public LiveData<CartResponse> fetchCart(String token, int page, int limit) {
        return repository.getCart(token, page, limit);
    }

}
