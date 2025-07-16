package com.speeda.Core.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "activities")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String businessName;
    private String industry;
    @Column(length = 1000)
    private String businessDescription;
    private String location;
    private String openingHours;
    private String audienceTarget;
    private String businessSize;
    private String uniqueSellingPoint;
    private String yearFounded;
    private String certifications;
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;
}
