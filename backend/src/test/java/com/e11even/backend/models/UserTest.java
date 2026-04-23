package com.e11even.backend.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {

    @Test
    void gettersSettersAndDefaultRole_shouldWork() {
        User user = new User();
        LocalDateTime createdAt = LocalDateTime.now();

        user.setId(5L);
        user.setFirstName("Jane");
        user.setLastName("Doe");
        user.setEmail("jane@example.com");
        user.setPassword("pwd");
        user.setBio("bio");
        user.setAvatarUrl("avatar");
        user.setRole("admin");
        user.setCreatedAt(createdAt);

        assertEquals(5L, user.getId());
        assertEquals("Jane", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("jane@example.com", user.getEmail());
        assertEquals("pwd", user.getPassword());
        assertEquals("bio", user.getBio());
        assertEquals("avatar", user.getAvatarUrl());
        assertEquals("admin", user.getRole());
        assertEquals(createdAt, user.getCreatedAt());

        User defaultUser = new User();
        assertEquals("user", defaultUser.getRole());
    }
}