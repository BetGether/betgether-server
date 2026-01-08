package com.betgether.betgether_server.domain.gether.repository;

import com.betgether.betgether_server.domain.gether.dto.response.GetherDetailResponse;
import com.betgether.betgether_server.domain.gether.dto.response.GetherSearchResponse;
import com.betgether.betgether_server.domain.gether.entity.Gether;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GetherRepository extends JpaRepository<Gether, Long> {

    @Query("""
        select new com.betgether.betgether_server.domain.gether.dto.response.GetherSearchResponse(
            g.id,
            g.title,
            g.description,
            g.imageUrl,
            cast(count(p) as integer),
            g.createdAt,
            c.title
        )
        from Gether g
        left join g.participations p
        left join g.challenge c
        where g.isPublic = true and (:keyword is null or :keyword = '' 
               or lower(g.title) like lower(concat('%', :keyword, '%'))
               or lower(g.description) like lower(concat('%', :keyword, '%')))
        group by g.id, g.title, g.description, g.imageUrl, g.createdAt, c.title
        order by g.createdAt desc
    """)
    List<GetherSearchResponse> search(@Param("keyword") String keyword);

    @Query("""
        select new com.betgether.betgether_server.domain.gether.dto.response.GetherDetailResponse(
            g.id,
            g.title,
            g.description,
            g.imageUrl,
            case when g.host.id = :userId then true else false end,
            case when g.host.id = :userId then g.inviteCode else null end,
            cast(count(p) as integer),
            c.title,
            c.betPoint
        )
        from Gether g
        left join g.participations p
        left join g.challenge c
        where g.id = :getherId
        group by g.id, g.title, g.description, g.imageUrl, g.host.id, g.inviteCode, c.title, c.betPoint
    """)
    Optional<GetherDetailResponse> findDetail(@Param("getherId") Long getherId,
                                              @Param("userId") Long userId);

    Optional<Gether> findByInviteCode(String inviteCode);
}
