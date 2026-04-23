package com.e11even.backend.dto;

import com.e11even.backend.models.User;

public class UserProfileResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String bio;
    private String avatarUrl;
    private String role;

    public UserProfileResponse(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.bio = user.getBio();
        this.avatarUrl = user.getAvatarUrl();
        this.role = user.getRole();
    }

    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getBio() { return bio; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getRole() { return role; }
}