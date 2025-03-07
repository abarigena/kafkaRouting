package com.example.commondto.dto;

import java.io.Serializable;

public class UserDTO implements Serializable {
    private Long id;
    private String name;

    // Добавляем дополнительные поля для будущего использования
    private String email;
    private String phone;
    private String address;

    // Конструкторы
    public UserDTO() {}

    public UserDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public UserDTO(Long id, String name, String email, String phone, String address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    // Геттеры и сеттеры для всех полей
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
