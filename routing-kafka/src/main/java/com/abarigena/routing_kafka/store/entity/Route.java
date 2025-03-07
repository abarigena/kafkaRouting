package com.abarigena.routing_kafka.store.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "routes")
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Long driverId;

    private String cityStart;

    private String cityEnd;

    private Double price;

    public String getCityStart() {
        return cityStart;
    }

    public void setCityStart(String city_start) {
        this.cityStart = city_start;
    }

    public String getCityEnd() {
        return cityEnd;
    }

    public void setCityEnd(String city_end) {
        this.cityEnd = city_end;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Route{" +
                "id=" + id +
                ", driverId=" + driverId +
                ", cityStart='" + cityStart + '\'' +
                ", cityEnd='" + cityEnd + '\'' +
                ", price=" + price +
                '}';
    }
}
