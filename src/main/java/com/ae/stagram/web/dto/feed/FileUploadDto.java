package com.ae.stagram.web.dto.feed;


import com.ae.stagram.domain.feed.Feed;
import com.ae.stagram.domain.feed.Image;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FileUploadDto {

    private String fileFullPath;
    private String fileUrl;

    @Builder
    public FileUploadDto(String fileFullPath, String fileUrl) {
        this.fileFullPath = fileFullPath;
        this.fileUrl = fileUrl;
    }

    public Image toEntity(Feed feed) {
        return Image.builder()
            .imagePath(fileFullPath)
            .imageUrl(fileUrl)
            .feed(feed)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }
}
