package com.betgether.betgether_server.domain.verification.entity;

import jakarta.persistence.*;

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
public class VerificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(name = "point_earned", nullable = false)
    private int pointEarned;

    @Column(name = "verified_at", nullable = false)
    private LocalDateTime verifiedAt;

    protected VerificationLog() {}

    public VerificationLog(Long userId, Long sessionId, int pointEarned) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.pointEarned = pointEarned;
        this.verifiedAt = LocalDateTime.now();
    }

    public int getPointEarned() { return pointEarned; }
}