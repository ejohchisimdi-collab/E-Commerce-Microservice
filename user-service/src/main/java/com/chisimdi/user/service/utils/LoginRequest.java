package com.chisimdi.user.service.utils;

import jakarta.validation.constraints.NotNull;

public class LoginRequest {
    @NotNull
    private String username;
    @NotNull
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

}
