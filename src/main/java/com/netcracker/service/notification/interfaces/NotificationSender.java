package com.netcracker.service.notification.interfaces;

import com.netcracker.model.entity.Notification;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Request;

import java.util.List;
import java.util.Map;

public interface NotificationSender {
    void sendPasswordReminder(Person person, String link);

    void sendInformationNotification(Person person);

    void sendCustomInformationNotification(Person person);

    void sendRegistrationCompletedNotification(Person person, String link);

    void sendPasswordForNewManager(Person person);

    void sendChangeStatusEvent(Person person);

    void resendNotification();

    void saveFailedNotification(Notification notification);

    void sendRequestExpiryReminder(List<Request> expiringRequests);
}
