package com.speeda.Core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StrategyDTO {
    private Long id;
    private String rapport;
    private String resumer;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private Long sessionId; // <-- AJOUTER CECI !
}
