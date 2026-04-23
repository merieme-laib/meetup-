package com.e11even.backend.dto;

import java.time.LocalDateTime;

public class EventRequest {
    private String title;
    private String description;
    private LocalDateTime date;
    private String location;
    private String city;
    private String imageUrl;
    private Double price;
    private Integer maxParticipants;
    private String category;
    private boolean online;

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDateTime getDate() { return date; }
    public String getLocation() { return location; }
    public String getCity() { return city; }
    public String getImageUrl() { return imageUrl; }
    public Double getPrice() { return price; }
    public Integer getMaxParticipants() { return maxParticipants; }
    public String getCategory() { return category; }
    public boolean isOnline() { return online; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setDate(LocalDateTime date) { this.date = date; }
    public void setLocation(String location) { this.location = location; }
    public void setCity(String city) { this.city = city; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setPrice(Double price) { this.price = price; }
    public void setMaxParticipants(Integer maxParticipants) { this.maxParticipants = maxParticipants; }
    public void setCategory(String category) { this.category = category; }
    public void setOnline(boolean online) { this.online = online; }
}