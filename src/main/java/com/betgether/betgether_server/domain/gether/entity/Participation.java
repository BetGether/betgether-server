package com.betgether.betgether_server.domain.gether.entity;

import com.betgether.betgether_server.domain.user.entity.User;
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
@EntityListeners(AuditingEntityListener.class) // 엔티티가 생성되거나 수정될 때마다 시간(생성일, 수정일)이나 사람(생성자, 수정자) 정보를 자동으로 기록해 주는 리스너 역할
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gether_id", nullable = false)
    private Gether gether;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime joinedAt;
}
