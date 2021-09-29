package com.ae.stagram.domain.feed.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class FeedResponse {

    private boolean hasNext;

    private List<FeedInfo> feedInfos;

}
