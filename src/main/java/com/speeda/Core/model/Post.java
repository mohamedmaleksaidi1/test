package com.speeda.Core.model;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "post")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String post;
    private String content;
    private LocalDate scheduledDate;
    private LocalTime scheduledTime;
    private Integer weekNumber;
    private String note;
    private Boolean status = false;
    private LocalDateTime publishAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wp_id", nullable = false) // <-- Ici le nom personnalisé
    private WeeklyPlanning weeklyPlanning;
}

