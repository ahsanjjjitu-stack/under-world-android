package com.example.underworld.home_pack;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private HomeRepository repository;

    public HomeViewModel() {
        repository = new HomeRepository();
    }

    public LiveData<HomeDataResponse> getHomeData() {
        return repository.fetchHomeData();
    }

}
