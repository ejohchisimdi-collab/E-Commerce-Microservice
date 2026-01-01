package com.chisimdi.user.service.models;

public class UserDTO {
    private int id;
    private String name;
    private String roles;
    private String email;
    private Boolean approved;

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getRoles() {
        return roles;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public Boolean getApproved() {
        return approved;
    }
}
