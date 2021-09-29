package com.ae.stagram.domain.feed.service;

import com.ae.stagram.domain.feed.dao.FeedRepository;
import com.ae.stagram.domain.feed.dao.ImageRepository;
import com.ae.stagram.domain.feed.domain.Feed;
import com.ae.stagram.domain.feed.domain.Image;
import com.ae.stagram.domain.feed.dto.FeedInfo;
import com.ae.stagram.domain.feed.dto.FeedRequest;
import com.ae.stagram.domain.feed.dto.FeedResponse;
import com.ae.stagram.domain.feed.exception.FeedNotFoundException;
import com.ae.stagram.domain.user.dao.UserRepository;
import com.ae.stagram.domain.user.domain.User;
import com.ae.stagram.domain.user.dto.UserDto;
import com.ae.stagram.domain.user.exception.UserNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedRepository feedRepository;

    private final UserRepository userRepository;

    private final ImageRepository imageRepository;

    @Value("${app.page-size}")
    private int pageSize;

    @Transactional
    public void insertFeed(FeedRequest createFeedRequest, UserDto userDto) {

        User user = userRepository.findByUuid(userDto.getUuid())
            .orElseThrow(() -> new UserNotFoundException("등록되지 않은 사용자입니다."));

        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        Feed newFeed = Feed.builder()
            .content(createFeedRequest.getContent())
            .user(user)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .build();

        List<Image> feedImages = getFeedImages(createFeedRequest.getImages(), newFeed);
        newFeed.setImages(feedImages);

        feedRepository.save(newFeed);
        imageRepository.saveAll(feedImages);
    }

    @Transactional
    public FeedInfo updateFeed(Long feedId, FeedRequest feedRequest) {
        Feed feed = feedRepository.findById(feedId)
            .orElseThrow(() -> new FeedNotFoundException("존재하지 않는 피드입니다."));

        if (feedRequest.getContent() != null && feedRequest.getContent().isEmpty() == false) {
            feed.setContent(feedRequest.getContent());
        }

        if (feedRequest.getImages() != null && feedRequest.getImages().isEmpty() == false) {
            List<Long> imageIds = feed.getImages().stream().map(image -> image.getId())
                .collect(Collectors.toList());
            imageRepository.deleteAllById(imageIds);
            List<Image> feedImages = getFeedImages(feedRequest.getImages(), feed);
            feed.setImages(feedImages);
            imageRepository.saveAll(feedImages);
        }

        feed.setUpdatedAt(LocalDateTime.now());
        Feed savedFeed = feedRepository.save(feed);
        List<String> imagePaths = savedFeed.getImages().stream()
            .map(image -> image.getImagePath())
            .collect(Collectors.toList());

        return FeedInfo.builder()
            .id(savedFeed.getId())
            .content(savedFeed.getContent())
            .display_name(savedFeed.getContent())
            .images(imagePaths)
            .createdAt(savedFeed.getCreatedAt())
            .updatedAt(savedFeed.getUpdatedAt())
            .build();
    }

    public FeedResponse getMainFeeds(Long cursorIndex, LocalDateTime updatedAt) {

        List<Feed> pageFeeds = getFeedPagenation(cursorIndex, updatedAt);

        List<FeedInfo> feedInfos = new ArrayList<>();
        for (Feed feed : pageFeeds) {
            List<String> imagePaths = feed.getImages().stream()
                .map(image -> image.getImagePath())
                .collect(Collectors.toList());

            feedInfos.add(FeedInfo.builder()
                .id(feed.getId())
                .display_name(feed.getUser().getDisplayName())
                .content(feed.getContent())
                .images(imagePaths)
                .createdAt(feed.getCreatedAt())
                .updatedAt(feed.getUpdatedAt())
                .build());
        }

        return FeedResponse.builder()
            .hasNext(pageFeeds.size() == pageSize)
            .feedInfos(feedInfos)
            .build();
    }

    private List<Feed> getFeedPagenation(Long cursorIndex, LocalDateTime updatedTime) {
        PageRequest pageRequest = PageRequest.of(0, pageSize);
        List<Feed> feeds = cursorIndex == null
                ? feedRepository.findAllByOrderByUpdatedAtDesc(pageRequest)
                : feedRepository.findPageList(cursorIndex, updatedTime,
                    pageRequest);

        return feeds;
    }

    public void removeFeed(Long feedId) {
        feedRepository.deleteById(feedId);
    }

    private List<Image> getFeedImages(List<String> imagePaths, Feed feed) {
        List<Image> paths = new ArrayList<>();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        for (String path : imagePaths) {
            paths.add(Image.builder()
                .imagePath(path)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .feed(feed)
                .build());
        }
        return paths;
    }
}
