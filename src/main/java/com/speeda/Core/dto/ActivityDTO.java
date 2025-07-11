package com.speeda.Core.dto;

import lombok.Data;

@Data
public class ActivityDTO {
    private Long id;
    private String businessName;
    private String industry;
    private String businessDescription;
    private String location;
    private String openingHours;
    private String audienceTarget;
    private String businessSize;
    private String uniqueSellingPoint;
    private Integer yearFounded;
    private String certifications;
}
