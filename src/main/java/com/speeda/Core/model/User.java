package com.speeda.Core.model;
import com.speeda.Core.model.Enum.UserStatus;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;
    private String password;
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private UserStatus status;
}
