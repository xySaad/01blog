package com.z01.blog.model.Notification;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;

public interface NotificationRepo extends JpaRepository<NotificationModel, Long> {

    List<NotificationModel> findByUserIdAndReadFalseOrderByCreatedAtDesc(long userId);

    List<NotificationModel> findByUserIdOrderByCreatedAtDesc(long userId);

    @Modifying
    @Transactional
    @Query("UPDATE NotificationModel n SET n.read = true WHERE n.userId = :userId AND n.read = false")
    void markAllReadByUserId(long userId);

    @Query("SELECT n.userId FROM NotificationModel n WHERE n.referenceId = :postId AND n.type = 'NEW_POST'")
    Set<Long> findAlreadyNotifiedUserIds(long postId);
}