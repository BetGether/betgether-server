package com.betgether.betgether_server.domain.verification.entity;

import com.betgether.betgether_server.domain.user.entity.User;
import com.betgether.betgether_server.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "point_transaction",
        indexes = {
                @Index(name = "idx_pt_user", columnList = "user_id"),
                @Index(name = "idx_pt_session", columnList = "session_id"),
                @Index(name = "idx_pt_created", columnList = "created_at")
        },
        uniqueConstraints = {
                // 같은 세션에서 같은 유저가 같은 타입의 트랜잭션을 중복 생성하는 것 방지
                @UniqueConstraint(name = "uk_pt_session_user_type", columnNames = {"session_id", "user_id", "type"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PointTransaction extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PointTransactionType type;

    @Column(nullable = false)
    private Integer amount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private VerificationSession session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}