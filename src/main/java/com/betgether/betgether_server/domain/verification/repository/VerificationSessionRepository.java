package com.betgether.betgether_server.domain.verification.repository;

import com.betgether.betgether_server.domain.verification.entity.VerifySession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationSessionRepository extends JpaRepository<VerifySession, Long> {
    Optional<VerifySession> findByToken(String token);

    Optional<VerifySession> findFirstByGetherIdAndStatusOrderByCreatedAtDesc(Long getherId, String status);
}
