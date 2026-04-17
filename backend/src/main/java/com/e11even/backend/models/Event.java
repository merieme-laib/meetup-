package com.e11even.backend.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Spring Boot transformera automatiquement la date String du Front en LocalDateTime
    private LocalDateTime date;

    private String location;
    private String city;
    private boolean isOnline;
    
    private String imageUrl;
    private double price;
    
    private Integer maxParticipants;
    private String category;

    // Pour l'instant, on stocke juste l'ID du créateur pour simplifier
    private Long creatorId;

    private int participantsCount = 0;
    private int likesCount = 0;

    // @Transient = "Ne crée pas de colonne dans la BDD pour ça, c'est juste pour le Front"
    @Transient
    private Boolean isRegistered;

    @Transient
    private Boolean isLiked;

    // ==========================================
    // CONSTRUCTEURS
    // ==========================================
    public Event() {}

    // ==========================================
    // GETTERS ET SETTERS
    // (Génère-les tous ici avec ton IDE : Clic droit > Generate > Getters and Setters)
    // ==========================================
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public boolean isOnline() { return isOnline; }
    public void setOnline(boolean online) { isOnline = online; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public Integer getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(Integer maxParticipants) { this.maxParticipants = maxParticipants; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }

    public int getParticipantsCount() { return participantsCount; }
    public void setParticipantsCount(int participantsCount) { this.participantsCount = participantsCount; }

    public int getLikesCount() { return likesCount; }
    public void setLikesCount(int likesCount) { this.likesCount = likesCount; }

    public Boolean getIsRegistered() { return isRegistered; }
    public void setIsRegistered(Boolean isRegistered) { this.isRegistered = isRegistered; }

    public Boolean getIsLiked() { return isLiked; }
    public void setIsLiked(Boolean isLiked) { this.isLiked = isLiked; }
}