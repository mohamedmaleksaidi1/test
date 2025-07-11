package com.speeda.Core.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SessionDTO {
    private Long id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
}

