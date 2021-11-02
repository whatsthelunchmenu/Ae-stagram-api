package com.ae.stagram.domain.feed;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedRepository extends JpaRepository<Feed, Long> {

    List<Feed> findAllByOrderByUpdatedAtDesc(Pageable pageable);

    @Query(value = "select fd from Feed fd "
        + "left outer join fetch fd.images join fetch fd.user "
        + "where (fd.updatedAt < :updatedAt or (fd.updatedAt = :updatedAt and fd.id <> :id)) "
        + "order by fd.updatedAt desc, fd.id asc")
    List<Feed> findPageList(@Param(value = "id") long id,
        @Param(value = "updatedAt") LocalDateTime updatedAt,
        Pageable pageable);
}
