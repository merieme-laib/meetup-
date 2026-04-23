package com.e11even.backend.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventTest {

    @Test
    void gettersSetters_shouldWorkForAllFields() {
        Event event = new Event();
        LocalDateTime now = LocalDateTime.now();

        event.setId(1L);
        event.setTitle("Title");
        event.setDescription("Description");
        event.setDate(now);
        event.setLocation("Location");
        event.setCity("Lyon");
        event.setOnline(true);
        event.setImageUrl("https://image");
        event.setPrice(42.5);
        event.setMaxParticipants(300);
        event.setCategory("Tech");
        event.setCreatorId(9L);
        event.setParticipantsCount(10);
        event.setLikesCount(20);
        event.setIsRegistered(Boolean.TRUE);
        event.setIsLiked(Boolean.FALSE);

        assertEquals(1L, event.getId());
        assertEquals("Title", event.getTitle());
        assertEquals("Description", event.getDescription());
        assertEquals(now, event.getDate());
        assertEquals("Location", event.getLocation());
        assertEquals("Lyon", event.getCity());
        assertTrue(event.isOnline());
        assertEquals("https://image", event.getImageUrl());
        assertEquals(42.5, event.getPrice());
        assertEquals(300, event.getMaxParticipants());
        assertEquals("Tech", event.getCategory());
        assertEquals(9L, event.getCreatorId());
        assertEquals(10, event.getParticipantsCount());
        assertEquals(20, event.getLikesCount());
        assertTrue(event.getIsRegistered());
        assertFalse(event.getIsLiked());
    }
}