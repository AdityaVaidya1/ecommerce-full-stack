package com.aditya.ecommerce.dto;

import com.aditya.ecommerce.entity.Role;

public class LoginResponse {
    private String token;
    private Role role;
    private String username;

    public LoginResponse(String token, Role role, String username) {
        this.token = token;
        this.role = role;
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public Role getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }
}
