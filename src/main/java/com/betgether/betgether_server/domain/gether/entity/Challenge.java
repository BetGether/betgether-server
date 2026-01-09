package com.betgether.betgether_server.domain.gether.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners({AuditingEntityListener.class})
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gether_id", nullable = false, unique = true)
    private Gether gether;

    @Column(nullable = false, length = 20)
    private String title;

    @Column(name = "bet_point", nullable = false)
    private Integer betPoint;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 5)
    @Builder.Default
    private Status status = Status.CLOSED;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public enum Status {
        OPEN, CLOSED
    }

    public void update(String title, Integer betPoint, Status status) {
        if (title != null) this.title = title;
        if (betPoint != null) this.betPoint = betPoint;
        if (status != null) this.status = status;
    }
}