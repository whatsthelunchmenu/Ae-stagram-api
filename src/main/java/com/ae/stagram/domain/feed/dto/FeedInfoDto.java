package com.ae.stagram.domain.feed.dto;

import com.ae.stagram.domain.feed.domain.Feed;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class FeedInfoDto {

    private Long id;

    private String display_name;

    private String content;

    private List<String> images;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public FeedInfoDto(Feed feed) {
        this.id = feed.getId();
        this.display_name = feed.getUser().getDisplayName();
        this.content = feed.getContent();
        this.images = feed.getImages().stream()
            .map(image -> image.getImageUrl())
            .collect(Collectors.toList());
        this.createdAt = feed.getCreatedAt();
        this.updatedAt = feed.getUpdatedAt();
    }

}
