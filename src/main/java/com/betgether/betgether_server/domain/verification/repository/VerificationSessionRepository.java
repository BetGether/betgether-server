package com.betgether.betgether_server.domain.verification.repository;

import com.betgether.betgether_server.domain.verification.entity.VerificationSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VerificationSessionRepository extends JpaRepository<VerificationSession, Long> {
    Optional<VerificationSession> findByToken(String token);

    Optional<VerificationSession> findFirstByGetherIdAndStatusOrderByCreatedAtDesc(Long getherId, String status);

    @Query("""
    select s
    from VerificationSession s
    where s.status = 'ACTIVE'
      and s.expiredAt <= :now
""")
    List<VerificationSession> findExpiredActiveSessions(@Param("now") LocalDateTime now);

    @Modifying
    @Query("update VerificationSession s set s.status = 'CLOSED' where s.id = :id and s.status = 'ACTIVE'")
    int closeIfActive(@Param("id") Long id);
}
