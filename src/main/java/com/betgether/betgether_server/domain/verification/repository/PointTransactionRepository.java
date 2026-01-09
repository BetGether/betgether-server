package com.betgether.betgether_server.domain.verification.repository;

import com.betgether.betgether_server.domain.verification.entity.PointTransaction;
import com.betgether.betgether_server.domain.verification.entity.PointTransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {
    boolean existsBySession_IdAndUser_IdAndType(Long sessionId, Long userId, PointTransactionType type);

    long countBySession_IdAndType(Long sessionId, PointTransactionType type);

    @Query("select coalesce(sum(p.amount), 0) from PointTransaction p where p.session.id = :sessionId")
    long sumAmountBySession_Id(@Param("sessionId") Long sessionId);

    @Query("select coalesce(sum(p.amount), 0) from PointTransaction p where p.session.id = :sessionId and p.type = :type")
    long sumAmountBySession_IdAndType(@Param("sessionId") Long sessionId,
                                     @Param("type") PointTransactionType type);

    // 유저의 최신 트렌젝션 1개
    Optional<PointTransaction> findTopByUser_IdOrderByCreatedAtDesc(Long userId);

    @Query("select count(distinct pt.user.id) from PointTransaction pt where pt.session.id = :sessionId")
    long countDistinctUsersBySessionId(@Param("sessionId") Long sessionId); // 세션 참여 유저 수
}
