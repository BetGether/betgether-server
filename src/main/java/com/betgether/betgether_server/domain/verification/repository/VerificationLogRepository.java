package com.betgether.betgether_server.domain.verification.repository;

import com.betgether.betgether_server.domain.verification.entity.VerificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VerificationLogRepository extends JpaRepository<VerificationLog, Long> {
    boolean existsBySessionIdAndUserId(Long sessionId, Long userId);

    @Query("select v.userId from VerificationLog v where v.sessionId = :sessionId")
    List<Long> findUser_IdsBySession_Id(@Param("sessionId") Long sessionId);

    long countBySessionId(Long sessionId);

    @Query("""
        select count(distinct v.userId)
        from VerificationLog v
        where v.sessionId = :sessionId
    """)
    long countDistinctUserIdBySessionId(@Param("sessionId") Long sessionId);
}