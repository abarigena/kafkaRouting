package com.abarigena.matchconsents.store.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "matchRequests")
public class MatchRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Long routeId;
    private Long suggestionId;
    private Long userId;
    private Long driverId;

    private Boolean userConsent;
    private Boolean driverConsent;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private LocalDateTime userResponseTime;
    private LocalDateTime driverResponseTime;

    private String status;  // PENDING, COMPLETED, EXPIRED, CANCELLED

    private String requestType; // USER_INITIATED, DRIVER_INITIATED

    public MatchRequest() {
        this.createdAt = LocalDateTime.now();
        this.status = "PENDING";
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public Long getSuggestionId() {
        return suggestionId;
    }

    public void setSuggestionId(Long suggestionId) {
        this.suggestionId = suggestionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public Boolean getUserConsent() {
        return userConsent;
    }

    public void setUserConsent(Boolean userConsent) {
        if (userConsent != null && this.userResponseTime == null) {
            this.userResponseTime = LocalDateTime.now();
        }
        this.userConsent = userConsent;
        updateStatus();
    }

    public Boolean getDriverConsent() {
        return driverConsent;
    }

    public void setDriverConsent(Boolean driverConsent) {
        if (driverConsent != null && this.driverResponseTime == null) {
            this.driverResponseTime = LocalDateTime.now();
        }
        this.driverConsent = driverConsent;
        updateStatus();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getUserResponseTime() {
        return userResponseTime;
    }

    public void setUserResponseTime(LocalDateTime userResponseTime) {
        this.userResponseTime = userResponseTime;
    }

    public LocalDateTime getDriverResponseTime() {
        return driverResponseTime;
    }

    public void setDriverResponseTime(LocalDateTime driverResponseTime) {
        this.driverResponseTime = driverResponseTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    // Обновление статуса запроса на основе текущих согласий
    private void updateStatus() {
        if (userConsent != null && driverConsent != null) {
            this.status = "COMPLETED";
        }
    }

    @Override
    public String toString() {
        return "NotificationRequest{" +
                "id=" + id +
                ", routeId=" + routeId +
                ", suggestionId=" + suggestionId +
                ", userId=" + userId +
                ", driverId=" + driverId +
                ", userConsent=" + userConsent +
                ", driverConsent=" + driverConsent +
                ", status='" + status + '\'' +
                '}';
    }
}