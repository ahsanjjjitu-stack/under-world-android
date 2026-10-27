package com.example.underworld.home_pack;

import java.util.List;

public class ProductResponse {
    private boolean success;
    private int currentPage;
    private int totalPages;
    private int totalProducts;
    private int count;
    private List<ProductModel> data;

    public boolean isSuccess() { return success; }
    public int getCurrentPage() { return currentPage; }
    public int getTotalPages() { return totalPages; }
    public int getTotalProducts() { return totalProducts; }
    public int getCount() { return count; }
    public List<ProductModel> getData() { return data; }
}
