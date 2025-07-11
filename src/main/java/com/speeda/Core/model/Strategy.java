package com.speeda.Core.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "strategies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Strategy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rapport;
    private String resumer;
    private String name;
    private String description;
    private LocalDateTime createdAt;

    @OneToOne
    @JoinColumn(name = "session_id", unique = true)
    private Session session;
}
