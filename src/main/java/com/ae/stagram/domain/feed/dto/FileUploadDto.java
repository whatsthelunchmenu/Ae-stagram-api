package com.ae.stagram.domain.feed.dto;


import com.ae.stagram.domain.feed.domain.Feed;
import com.ae.stagram.domain.feed.domain.Image;
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
