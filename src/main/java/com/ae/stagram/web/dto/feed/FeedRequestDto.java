package com.ae.stagram.web.dto.feed;

import com.ae.stagram.domain.feed.Feed;
import com.ae.stagram.domain.feed.Image;
import com.ae.stagram.domain.user.User;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class FeedRequestDto {

    private String content;
    private List<MultipartFile> images;

    @Builder
    public FeedRequestDto(String content, List<MultipartFile> images) {
        this.content = content;
        this.images = images;
    }

    public Feed toEntity(User user, List<Image> images) {
        return Feed.builder()
            .content(content)
            .user(user)
            .images(images)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

}
