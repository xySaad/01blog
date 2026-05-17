package com.z01.blog.model.Notification;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "notifications")
public class NotificationModel {
    @Id
    public long id;

    /** The user who should receive this notification. */
    public long userId;

    /** Event type, e.g. "NEW_POST". */
    public String type;

    /** ID of the related entity (post, comment, …). */
    public Long referenceId;

    public LocalDateTime createdAt;

    public boolean read;
}