package com.betgether.betgether_server.domain.gether.repository;

import com.betgether.betgether_server.domain.gether.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    Optional<Challenge> findByGether_Id(Long getherId);
    boolean existsByGether_Id(Long getherId);

    Optional<Challenge> findByGether_IdAndStatus(Long getherId, Challenge.Status status);
}
