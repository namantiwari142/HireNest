package com.hirenest.dto;

import com.hirenest.entity.NotificationType;

import java.time.Instant;

public class NotificationDto {
    private Long id;
    private NotificationType type;
    private String title;
    private String message;
    private boolean read;
    private Instant createdAt;
    private Long referenceId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Long getReferenceId() { return referenceId; }
    public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }
}
