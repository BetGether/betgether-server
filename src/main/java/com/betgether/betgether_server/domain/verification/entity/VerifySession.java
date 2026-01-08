package com.betgether.betgether_server.domain.verification.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "verify_session",
        indexes = {
                @Index(name = "idx_verify_session_gether", columnList = "gether_id"),
                @Index(name = "idx_verify_session_host", columnList = "user_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_verify_session_token", columnNames = "token")
        })
@Getter
public class VerifySession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long hostUserId;

    @Column(name = "gether_id", nullable = false)
    private Long getherId;

    @Column(name = "token", nullable = false, length = 64)
    private String token;

    @Column(name = "bet_point", nullable = false)
    private int betPoint;

    @Column(name = "status", nullable = false, length = 20)
    private String status; // ACTIVE / EXPIRED / CLOSED

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    protected VerifySession() {}

    public VerifySession(Long hostUserId, Long getherId, String token, int betPoint,
                         LocalDateTime createdAt, LocalDateTime expiredAt) {
        this.hostUserId = hostUserId;
        this.getherId = getherId;
        this.token = token;
        this.betPoint = betPoint;
        this.status = "ACTIVE";
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
    }

    public boolean isExpired(LocalDateTime now) {
        return now.isAfter(expiredAt);
    }

    public void markExpired() { this.status = "EXPIRED"; }

    public boolean isActive() { return "ACTIVE".equals(this.status); }

}