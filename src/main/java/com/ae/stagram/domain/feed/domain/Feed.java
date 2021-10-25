package com.ae.stagram.domain.feed.domain;

import com.ae.stagram.domain.feed.dto.FeedInfoDto;
import com.ae.stagram.domain.user.domain.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Feed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne
    @ToString.Exclude // toString의 순환참조를 막기위해 사용
    private User user;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "feed", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    private List<Image> images = new ArrayList<>();

    public FeedInfoDto update(String updatedContent, List<Image> updatedImage){

        content = updatedContent;

        images.clear();
        images = updatedImage;

        updatedAt = LocalDateTime.now();

        List<String> imagePaths = images.stream().map(it -> it.getImagePath())
            .collect(Collectors.toList());

        return FeedInfoDto.builder()
            .id(id)
            .content(content)
            .display_name(user.getDisplayName())
            .images(imagePaths)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .build();
    }
}
