package com.abarigena.orderskafka.store.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "orders",
 uniqueConstraints = @UniqueConstraint(columnNames = "idempotency_key"))
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Long routeId;
    private Long suggestionId;
    private Long userId;
    private Long driverId;

    @Column(name = "idempotency_key", unique = true, nullable = false)
    private String idempotencyKey;

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

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", routeId=" + routeId +
                ", suggestionId=" + suggestionId +
                ", userId=" + userId +
                ", driverId=" + driverId +
                ", idempotencyKey='" + idempotencyKey + '\'' +
                '}';
    }
}
