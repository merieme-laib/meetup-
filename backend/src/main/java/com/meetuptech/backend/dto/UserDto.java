package com.meetuptech.backend.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String bio;
    private String avatarUrl;
    private LocalDateTime createdAt;
}
