package com.speeda.api;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_sessions")
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true, length = 512)
    private String token;

    @Column
    private String ipAddress;

    @Column
    private String userAgent;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private LocalDateTime expiredAt;

    @Column(nullable = false)
    private Boolean isActive = true;

    // Getters & Setters
}
