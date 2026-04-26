package com.e11even.backend.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(columnDefinition = "varchar default 'user'")
    private String role = "user";

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    private boolean isDeleted = false;

    public boolean getIsDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }
        
    public User() {}
}