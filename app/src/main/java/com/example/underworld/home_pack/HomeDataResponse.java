package com.example.underworld.home_pack;

import java.util.List;

public class HomeDataResponse {

    private boolean success;
    private String message;
    private HomeData data;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public HomeData getData() { return data; }

    public static class HomeData {
        private List<BannerModel> banners;
        private List<CategoryModel> categories;

        public List<BannerModel> getBanners() { return banners; }
        public List<CategoryModel> getCategories() { return categories; }
    }

}
