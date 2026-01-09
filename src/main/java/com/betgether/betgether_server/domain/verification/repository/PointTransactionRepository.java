package com.betgether.betgether_server.domain.verification.repository;

import com.betgether.betgether_server.domain.verification.entity.PointTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {
    boolean existsBySession_IdAndUser_IdAndType(Long sessionId, Long userId, PointTransaction.Type type);

    long countBySession_IdAndType(Long sessionId, PointTransaction.Type type);

    @Query("select coalesce(sum(p.amount), 0) from PointTransaction p where p.session.id = :sessionId")
    long sumAmountBySession_Id(@Param("sessionId") Long sessionId);

    @Query("select coalesce(sum(p.amount), 0) from PointTransaction p where p.session.id = :sessionId and p.type = :type")
    long sumAmountBySession_IdAndType(@Param("sessionId") Long sessionId,
                                     @Param("type") PointTransaction.Type type);
}
