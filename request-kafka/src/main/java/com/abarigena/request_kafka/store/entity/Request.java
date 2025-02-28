package com.abarigena.request_kafka.store.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long routeId;
    private Long suggestionId;
    private boolean userConsent;
    private boolean driverConsent;

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

    public boolean isUserConsent() {
        return userConsent;
    }

    public void setUserConsent(boolean userConsent) {
        this.userConsent = userConsent;
    }

    public boolean isDriverConsent() {
        return driverConsent;
    }

    public void setDriverConsent(boolean driverConsent) {
        this.driverConsent = driverConsent;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Request{" +
                "id=" + id +
                ", routeId=" + routeId +
                ", suggestionId=" + suggestionId +
                ", userConsent=" + userConsent +
                ", driverConsent=" + driverConsent +
                '}';
    }
}
