package com.example.underworld.home_pack;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class ProductViewModel extends ViewModel {
    private final ProductRepository repository;

    public ProductViewModel() {
        repository = new ProductRepository();
    }

    public LiveData<ProductResponse> fetchProducts(int page, int limit, String category) {
        return repository.getProducts(page, limit, category);
    }
}
