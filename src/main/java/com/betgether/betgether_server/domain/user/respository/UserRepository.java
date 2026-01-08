package com.betgether.betgether_server.domain.user.respository;

import com.betgether.betgether_server.domain.ranking.RankingItemView;
import com.betgether.betgether_server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByNickname(String nickname);

    boolean existsByNickname(String nickname);

    @Query(value = """
        SELECT t.rank AS rank, t.nickname AS nickname, t.point AS point
        FROM (
            SELECT
                RANK() OVER (ORDER BY u.point DESC, u.id ASC) AS rank,
                u.nickname AS nickname,
                u.point AS point,
                u.id AS user_id
            FROM users u
        ) t
        ORDER BY t.rank ASC
        LIMIT :limit
        """, nativeQuery = true)
    List<RankingItemView> findTopRanking(@Param("limit") int limit);

    @Query(value = """
        SELECT t.rank AS rank, t.nickname AS nickname, t.point AS point
        FROM (
            SELECT
                RANK() OVER (ORDER BY u.point DESC, u.id ASC) AS rank,
                u.nickname AS nickname,
                u.point AS point,
                u.id AS user_id
            FROM users u
        ) t
        WHERE t.user_id = :userId
        """, nativeQuery = true)
    RankingItemView findMyRanking(@Param("userId") Long userId);

    @Modifying
    @Query("update User u set u.point = u.point + :delta where u.id in :userIds")
    int bulkAddPoint(@Param("userIds") List<Long> userIds, @Param("delta") int delta);

    @Modifying
    @Query("update User u set u.point = u.point - :delta where u.id in :userIds")
    int bulkSubPoint(@Param("userIds") List<Long> userIds, @Param("delta") int delta);
}
