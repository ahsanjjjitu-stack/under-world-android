package com.example.underworld.notification_pack;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class NotificationViewModel extends ViewModel {

    private final NotificationRepository repository;

    public NotificationViewModel() {
        repository = new NotificationRepository();
    }

    public LiveData<NotificationResponse> fetchNotifications(String token, int limit, String lastCreatedAt) {
        return repository.getNotifications(token, limit, lastCreatedAt);
    }
}
