package com.betgether.betgether_server.domain.gether.repository;

import com.betgether.betgether_server.domain.gether.dto.response.MyGetherResponse;
import com.betgether.betgether_server.domain.gether.entity.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    @Query("""
        select new com.betgether.betgether_server.domain.gether.dto.response.MyGetherResponse(
            g.id,
            g.title,
            g.imageUrl,
            cast(count(p2) as integer),
            p.joinedAt,
            case when c is null then null else concat('', c.status) end
        )
        from Participation p
        join p.gether g
        join g.participations p2
        left join g.challenge c
        where p.user.id = :userId
        group by g.id, g.title, g.imageUrl, p.joinedAt, c.status
        order by p.joinedAt desc
    """)
    List<MyGetherResponse> findMyGethers(@Param("userId") Long userId);

    Optional<Participation> findByUser_IdAndGether_Id(Long userId, Long getherId);
    boolean existsByUser_IdAndGether_Id(Long userId, Long getherId);

    long countByGether_Id(Long getherId);
}
