package com.betgether.betgether_server.domain.user.entity;

import com.betgether.betgether_server.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String nickname;

    @Column(nullable = false)
    @Builder.Default
    private Integer point = 500;

    @Column
    private LocalDateTime lastLogin;

    public void addPoint(int amount) {
        this.point += amount;
    }

    public boolean isFirstLoginToday(LocalDateTime now) {
        return this.lastLogin == null || !this.lastLogin.toLocalDate().isEqual(now.toLocalDate());
    }

    public void updateLastLogin(LocalDateTime now) {
        this.lastLogin = now;
    }
}
