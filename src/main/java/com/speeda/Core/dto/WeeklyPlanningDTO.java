package com.speeda.Core.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WeeklyPlanningDTO {
    private Long id;
    private Integer weekNumber;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
