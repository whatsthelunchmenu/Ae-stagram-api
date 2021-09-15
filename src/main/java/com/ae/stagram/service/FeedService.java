package com.ae.stagram.service;

import com.ae.stagram.dto.FeedDto;
import com.ae.stagram.dto.ImageDto;
import com.ae.stagram.dto.MainFeedDto;
import com.ae.stagram.dto.UserDto;
import com.ae.stagram.entity.Feed;
import com.ae.stagram.entity.Image;
import com.ae.stagram.entity.User;
import com.ae.stagram.exception.FeedNotFoundException;
import com.ae.stagram.exception.UserNotFoundException;
import com.ae.stagram.repository.FeedRepository;
import com.ae.stagram.repository.ImageRepository;
import com.ae.stagram.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedRepository feedRepository;

    private final UserRepository userRepository;

    private final ImageRepository imageRepository;

    @Transactional
    public void insertFeed(FeedDto feedDto, UserDto userDto) {

        User user = userRepository.findByUuid(userDto.getUuid())
            .orElseThrow(() -> new UserNotFoundException("등록되지 않은 사용자입니다."));

        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        Feed newFeed = Feed.builder()
            .content(feedDto.getContent())
            .user(user)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .build();

        List<Image> feedImages = getFeedImages(feedDto.getImages(), newFeed);
        newFeed.setImages(feedImages);

        feedRepository.save(newFeed);
        imageRepository.saveAll(feedImages);
    }

    @Transactional
    public MainFeedDto updateFeed(Long feedId, FeedDto feedDto) {
        Feed feed = feedRepository.findById(feedId)
            .orElseThrow(() -> new FeedNotFoundException("존재하지 않는 피드입니다."));

        Feed updateFeed = Feed.builder()
            .id(feed.getId())
            .content(feedDto.getContent())
            .user(feed.getUser())
            .createdAt(feed.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .build();

        List<ImageDto> updateImages = feedDto.getImages();

        List<Image> feedImages = getFeedImages(updateImages, updateFeed);
        updateFeed.setImages(feedImages);

        Feed savedFeed = feedRepository.save(updateFeed);
        imageRepository.saveAll(feedImages);

        return MainFeedDto.builder()
            .id(savedFeed.getId())
            .content(savedFeed.getContent())
            .display_name(savedFeed.getContent())
            .images(savedFeed.getImages())
            .build();
    }

    public List<MainFeedDto> getMainFeeds() {

        List<Feed> feeds = feedRepository.findAll(Sort.by(Direction.DESC, "updatedAt"));

        List<MainFeedDto> mainFeedDtos = new ArrayList<>();
        for (Feed feed : feeds) {
            mainFeedDtos.add(MainFeedDto.builder()
                .id(feed.getId())
                .display_name(feed.getUser().getDisplayName())
                .content(feed.getContent())
                .images(feed.getImages())
                .build());
        }
        return mainFeedDtos;
    }

    public void removeFeed(Long feedId) {
        feedRepository.deleteById(feedId);
    }

    private List<Image> getFeedImages(List<ImageDto> imagePaths, Feed feed) {
        List<Image> paths = new ArrayList<>();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        for (ImageDto imageDto : imagePaths) {
            paths.add(Image.builder()
                .id(imageDto.getId())
                .imagePath(imageDto.getPath())
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .feed(feed)
                .build());
        }
        return paths;
    }
}
