package com.betgether.betgether_server.domain.verification.entity;

import com.betgether.betgether_server.domain.gether.entity.Challenge;
import jakarta.persistence.*;
import lombok.*;

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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    public boolean isExpired(LocalDateTime now) {
        return now.isAfter(expiredAt);
    }

    public void markExpired() { this.status = "EXPIRED"; }

    public boolean isActive() { return "ACTIVE".equals(this.status); }

    public void markClosed() { this.status = "CLOSED"; }
}