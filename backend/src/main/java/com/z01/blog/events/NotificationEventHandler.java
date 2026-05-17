package com.z01.blog.events;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.z01.blog.infrastructure.APIEventHandler;
import com.z01.blog.infrastructure.PrincipalProvider;
import com.z01.blog.model.Follow;
import com.z01.blog.model.DTO.CreatePostResponse;
import com.z01.blog.model.Notification.NotificationModel;
import com.z01.blog.model.Notification.NotificationRepo;

import cn.hutool.core.util.IdUtil;

@Component
public class NotificationEventHandler implements APIEventHandler {

    @Autowired
    private NotificationRepo notificationRepo;

    @Autowired
    private Follow.repo followRepo;

    @Autowired
    PrincipalProvider<Long> principalProvider;

    @Override
    public void handle(Object returnValue, Object[] args) {
        CreatePostResponse response = (CreatePostResponse) returnValue;
        if (!response.isPublic())
            return;

        long postId = Long.parseLong(response.id());
        long authorId = this.principalProvider.getCurrentPrincipal();

        List<Long> followerIds = followRepo.findFollowerIdsByUserId(authorId);
        if (followerIds.isEmpty())
            return;

        Set<Long> alreadyNotifiedUserIds = notificationRepo.findAlreadyNotifiedUserIds(postId);

        LocalDateTime now = LocalDateTime.now();
        List<NotificationModel> notifications = followerIds.stream()
                .filter(followerId -> !alreadyNotifiedUserIds.contains(followerId))
                .map(followerId -> {
                    NotificationModel n = new NotificationModel();
                    n.id = IdUtil.getSnowflake().nextId();
                    n.userId = followerId;
                    n.type = "NEW_POST";
                    n.referenceId = postId;
                    n.createdAt = now;
                    n.read = false;
                    return n;
                })
                .toList();

        if (!notifications.isEmpty()) {
            notificationRepo.saveAll(notifications);
        }
    }
}