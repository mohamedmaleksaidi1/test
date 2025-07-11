package com.speeda.Core.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "weekly_plannings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyPlanning {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer weekNumber;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @OneToOne
    @JoinColumn(name = "strategy_id", unique = true)
    private Strategy strategy;
    @OneToMany(mappedBy = "weeklyPlanning", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;
}
