package com.ae.stagram.domain.feed.dto;

import com.ae.stagram.domain.feed.domain.Feed;
import com.ae.stagram.domain.feed.domain.Image;
import com.ae.stagram.domain.user.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
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
