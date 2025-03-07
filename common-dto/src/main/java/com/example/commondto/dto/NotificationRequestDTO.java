package com.example.commondto.dto;

import java.io.Serializable;

public class NotificationRequestDTO implements Serializable {
    private Long requestId;    // Уникальный ID запроса в системе
    private Long routeId;      // ID маршрута
    private Long suggestionId; // ID предложения
    private Long userId;       // ID пользователя
    private Long driverId;     // ID водителя
    private RequestType requestType; // Тип запроса

    // Enum для типа запроса
    public enum RequestType {
        USER_INITIATED,    // Запрос инициирован пользователем
        DRIVER_INITIATED   // Запрос инициирован водителем
    }

    public NotificationRequestDTO() {}

    public NotificationRequestDTO(Long requestId, Long routeId, Long suggestionId,
                                  Long userId, Long driverId, RequestType requestType) {
        this.requestId = requestId;
        this.routeId = routeId;
        this.suggestionId = suggestionId;
        this.userId = userId;
        this.driverId = driverId;
        this.requestType = requestType;
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

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    @Override
    public String toString() {
        return "NotificationRequestDTO{" +
                "requestId=" + requestId +
                ", routeId=" + routeId +
                ", suggestionId=" + suggestionId +
                ", userId=" + userId +
                ", driverId=" + driverId +
                ", requestType=" + requestType +
                '}';
    }
}
