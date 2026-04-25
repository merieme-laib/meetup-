package com.e11even.backend.dto;

import com.e11even.backend.models.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserProfileResponseTest {

    @Test
    void constructor_shouldMapAllFields_andGettersShouldReturnValues() {
        User user = new User();
        user.setId(42L);
        user.setFirstName("Jane");
        user.setLastName("Doe");
        user.setEmail("jane@example.com");
        user.setBio("Bio");
        user.setAvatarUrl("avatar.png");
        user.setRole("admin");

        UserProfileResponse response = new UserProfileResponse(user);

        assertEquals(42L, response.getId());
        assertEquals("Jane", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("jane@example.com", response.getEmail());
        assertEquals("Bio", response.getBio());
        assertEquals("avatar.png", response.getAvatarUrl());
        assertEquals("admin", response.getRole());
    }
}
