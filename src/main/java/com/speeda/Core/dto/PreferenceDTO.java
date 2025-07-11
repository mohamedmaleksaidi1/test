package com.speeda.Core.dto;

import lombok.Data;

@Data
public class PreferenceDTO {
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
}
