package com.example.commondto.dto;

import java.util.List;

public class OrderDTO {

    private Long id;

    private List<UserDTO> users;
    private DriverDTO driver;

    private RouteResponseDTO route;
    private SuggestionResponseDTO suggestion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<UserDTO> getUsers() {
        return users;
    }

    public void setUsers(List<UserDTO> users) {
        this.users = users;
    }

    public DriverDTO getDriver() {
        return driver;
    }

    public void setDriver(DriverDTO driver) {
        this.driver = driver;
    }

    public RouteResponseDTO getRoute() {
        return route;
    }

    public void setRoute(RouteResponseDTO route) {
        this.route = route;
    }

    public SuggestionResponseDTO getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(SuggestionResponseDTO suggestion) {
        this.suggestion = suggestion;
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
                "id=" + id +
                ", users=" + users +
                ", driver=" + driver +
                ", route=" + route +
                ", suggestion=" + suggestion +
                '}';
    }
}
