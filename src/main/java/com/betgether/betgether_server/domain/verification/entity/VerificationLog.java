package com.betgether.betgether_server.domain.verification.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification_log",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_verification_log_session_user", columnNames = {"session_id", "user_id"})
        },
        indexes = {
                @Index(name = "idx_verification_log_user", columnList = "user_id"),
                @Index(name = "idx_verification_log_session", columnList = "session_id")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class VerificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;


    @Column(name = "verified_at", nullable = false)
    private LocalDateTime verifiedAt;
}