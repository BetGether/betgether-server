package com.betgether.betgether_server.domain.gether.entity;

import com.betgether.betgether_server.domain.user.entity.User;
import com.betgether.betgether_server.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Gether extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private User host;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String imageUrl;

    @Column(nullable = false, unique = true, length = 10)
    private String inviteCode;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isPublic = true;

    @OneToMany(mappedBy = "gether", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Participation> participations = new ArrayList<>();

    @OneToOne(mappedBy = "gether", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Challenge challenge;

    public void update(String title, String description, String imageUrl, Boolean isPublic) {
        if (title != null) this.title = title;
        if (description != null) this.description = description;
        if (imageUrl != null) this.imageUrl = imageUrl;
        if (isPublic != null) this.isPublic = isPublic;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
        if(challenge != null) challenge.setGether(this);
    }

}
