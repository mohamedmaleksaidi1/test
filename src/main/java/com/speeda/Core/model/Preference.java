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
    private String toneOfVoice;
    private String socialMediaGoals;
    private String preferredPlatforms;
    private String postingFrequency;
    private String preferredPostTime;
    private String visualStyle;
    private String hashtagStrategy;
    private String competitorAccounts;
    private String contentTypes;
    private String languagePreference;
    private String additionalNotes;
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;
}
