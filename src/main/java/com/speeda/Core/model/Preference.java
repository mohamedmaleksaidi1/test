package com.speeda.Core.model;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "preferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Preference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action;

    @Column(columnDefinition = "TEXT")
    private String resumer;

    private String toneOfVoice;

    @Column(columnDefinition = "TEXT")
    private String preferredPlatforms;

    @Column(columnDefinition = "TEXT")
    private String socialMediaGoals;

    private String languagePreference;

    @Column(columnDefinition = "TEXT")
    private String other;

    @Column(columnDefinition = "TEXT")
    private String text;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;
}

