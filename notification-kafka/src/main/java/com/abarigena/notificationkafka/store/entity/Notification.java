package com.abarigena.notificationkafka.store.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Long requestId;  // ID запроса из NotificationRequest
    private Long recipientId; // ID получателя уведомления
    private String recipientType; // USER, DRIVER
    private String content;  // Содержание уведомления
    private Boolean isRead;  // Прочитано ли уведомление
    private String status;   // PENDING, SENT, DELIVERED, READ

    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;

    // Конструкторы, геттеры, сеттеры

    public Notification() {
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
        this.status = "PENDING";
    }

    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
        this.status = "READ";
    }

    public void markAsSent() {
        this.sentAt = LocalDateTime.now();
        this.status = "SENT";
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public String getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(String recipientType) {
        this.recipientType = recipientType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
}
