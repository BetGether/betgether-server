package com.betgether.betgether_server.domain.verification.repository;

import com.betgether.betgether_server.domain.verification.entity.VerifySession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VerificationSessionRepository extends JpaRepository<VerifySession, Long> {
    Optional<VerifySession> findByToken(String token);

    Optional<VerifySession> findFirstByGetherIdAndStatusOrderByCreatedAtDesc(Long getherId, String status);

    @Query("""
    select s
    from VerifySession s
    where s.status = 'ACTIVE'
      and s.expiredAt <= :now
""")
    List<VerifySession> findExpiredActiveSessions(@Param("now") LocalDateTime now);
}
