package com.example.commondto.dto;

public class SuggestionResponseDTO {
    private Long id;
    private String cityStart;
    private String cityEnd;
    private Double price;
    private UserDTO user;

    public SuggestionResponseDTO() {}

    public SuggestionResponseDTO(Long id, String cityStart, String cityEnd, Double price, UserDTO user) {
        this.id = id;
        this.cityStart = cityStart;
        this.cityEnd = cityEnd;
        this.price = price;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCityStart() {
        return cityStart;
    }

    public void setCityStart(String cityStart) {
        this.cityStart = cityStart;
    }

    public String getCityEnd() {
        return cityEnd;
    }

    public void setCityEnd(String cityEnd) {
        this.cityEnd = cityEnd;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }
}
