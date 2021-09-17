package com.ae.stagram.domain.feed.dao;

import com.ae.stagram.domain.feed.domain.Feed;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedRepository extends JpaRepository<Feed, Long> {

}
