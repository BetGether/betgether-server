package com.betgether.betgether_server.domain.gether.repository;

import com.betgether.betgether_server.domain.gether.dto.response.MyGetherResponse;
import com.betgether.betgether_server.domain.gether.entity.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    @Query("""
        select new com.betgether.betgether_server.domain.gether.dto.response.MyGetherResponse(
            g.id,
            g.title,
            g.imageUrl,
            (select cast(count(p3) as integer) from Participation p3 where p3.gether = g),
            p.joinedAt,
            case when c is null then null else concat('', c.status) end
        )
        from Participation p
        join p.gether g
        left join g.challenge c
        where p.user.id = :userId
        order by p.joinedAt desc
    """)
    List<MyGetherResponse> findMyGethers(@Param("userId") Long userId);

    @Query("""
        select p.user.id
        from Participation p 
        where p.gether.id = :getherId
    """)
    List<Long> findUserIdsByGetherId(@Param("getherId") Long getherId);
    boolean existsByUser_IdAndGether_Id(Long userId, Long getherId);

    long countByGether_Id(Long getherId);
}
