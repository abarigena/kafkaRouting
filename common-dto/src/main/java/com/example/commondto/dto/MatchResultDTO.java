package com.example.commondto.dto;

import java.io.Serializable;

public class MatchResultDTO implements Serializable {
    private Long requestId;
    private Long routeId;
    private Long suggestionId;
    private Long userId;
    private Long driverId;
    private Boolean userConsent;
    private Boolean driverConsent;
    private Boolean isMatched;  // Результат согласования (true если оба согласны)

    // Конструкторы, геттеры, сеттеры
    public MatchResultDTO() {}

    public MatchResultDTO(Long requestId, Long routeId, Long suggestionId,
                          Long userId, Long driverId,
                          Boolean userConsent, Boolean driverConsent) {
        this.requestId = requestId;
        this.routeId = routeId;
        this.suggestionId = suggestionId;
        this.userId = userId;
        this.driverId = driverId;
        this.userConsent = userConsent;
        this.driverConsent = driverConsent;
        this.isMatched = userConsent != null && driverConsent != null &&
                userConsent && driverConsent;
    }

    // Геттеры и сеттеры
    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
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
        this.userConsent = userConsent;
    }

    public Boolean getDriverConsent() {
        return driverConsent;
    }

    public void setDriverConsent(Boolean driverConsent) {
        this.driverConsent = driverConsent;
    }

    public Boolean getIsMatched() {
        return isMatched;
    }

    public void setIsMatched(Boolean isMatched) {
        this.isMatched = isMatched;
    }

    @Override
    public String toString() {
        return "MatchResultDTO{" +
                "requestId=" + requestId +
                ", routeId=" + routeId +
                ", suggestionId=" + suggestionId +
                ", userId=" + userId +
                ", driverId=" + driverId +
                ", userConsent=" + userConsent +
                ", driverConsent=" + driverConsent +
                ", isMatched=" + isMatched +
                '}';
    }
}
