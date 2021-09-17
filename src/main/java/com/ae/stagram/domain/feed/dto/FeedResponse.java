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

    private Long id;

    private String display_name;

    private String content;

    private List<String> images;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
