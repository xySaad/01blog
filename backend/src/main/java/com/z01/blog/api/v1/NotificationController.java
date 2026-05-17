package com.z01.blog.api.v1;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.z01.blog.annotation.Auth;
import com.z01.blog.exception.AppError;
import com.z01.blog.model.Notification.NotificationModel;
import com.z01.blog.model.Notification.NotificationRepo;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    @Autowired
    private NotificationRepo notificationRepo;

    @GetMapping
    List<NotificationModel> getAll(@Auth.User long userId) {
        return notificationRepo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @PostMapping("/{id}/read")
    void markRead(@PathVariable long id, @Auth.User long userId) {
        setRead(id, userId, true);
    }

    @PostMapping("/{id}/unread")
    void markUnread(@PathVariable long id, @Auth.User long userId) {
        setRead(id, userId, false);
    }

    @PostMapping("/read-all")
    void markAllRead(@Auth.User long userId) {
        notificationRepo.markAllReadByUserId(userId);
    }

    private void setRead(long id, long userId, boolean read) {
        var notif = notificationRepo.findById(id)
                .orElseThrow(AppError.ENTITY_NOT_FOUND::asException);
        if (notif.userId != userId)
            throw AppError.ACCESS_DENIED.asException();
        notif.read = read;
        notificationRepo.save(notif);
    }
}