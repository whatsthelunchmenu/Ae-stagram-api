package com.ae.stagram.global.util.pageable;

import com.ae.stagram.domain.feed.dao.FeedRepository;
import com.ae.stagram.domain.feed.domain.Feed;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PageNationUtils {

    private final FeedRepository feedRepository;

    public static final String splitPageInfo = "#";

    @Value("${app.page-size}")
    private int pageSize;

    public List<Feed> getFeedPagenation(Long cursorIndex, LocalDateTime updatedTime) {
        PageRequest pageRequest = PageRequest.of(0, pageSize);
        List<Feed> feeds = cursorIndex == null
            ? feedRepository.findAllByOrderByUpdatedAtDesc(pageRequest)
            : feedRepository.findPageList(cursorIndex, updatedTime, pageRequest);

        return feeds;
    }

    public int getPageSize(){
        return pageSize;
    }
}
