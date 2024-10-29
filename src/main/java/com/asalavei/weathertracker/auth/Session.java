package com.asalavei.weathertracker.auth;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "sessions", indexes = @Index(name = "idx_sessions_user_id", columnList = "user_id"))
public class Session {

    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @ToString.Exclude
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
}