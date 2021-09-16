package com.ae.stagram.domain.feed.service;

import com.ae.stagram.domain.feed.dao.FeedRepository;
import com.ae.stagram.domain.feed.dao.ImageRepository;
import com.ae.stagram.domain.feed.domain.Feed;
import com.ae.stagram.domain.feed.domain.Image;
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
    public FeedResponse updateFeed(Long feedId, FeedRequest feedRequest) {
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

        Feed savedFeed = feedRepository.save(feed);
        List<String> imagePaths = savedFeed.getImages().stream()
            .map(image -> image.getImagePath())
            .collect(Collectors.toList());

        return FeedResponse.builder()
            .id(savedFeed.getId())
            .content(savedFeed.getContent())
            .display_name(savedFeed.getContent())
            .images(imagePaths)
            .build();
    }

    public List<FeedResponse> getMainFeeds() {

        List<Feed> feeds = feedRepository.findAll(Sort.by(Direction.DESC, "updatedAt"));

        List<FeedResponse> mainFeedDtos = new ArrayList<>();
        for (Feed feed : feeds) {
            List<String> imagePaths = feed.getImages().stream()
                .map(image -> image.getImagePath())
                .collect(Collectors.toList());

            mainFeedDtos.add(FeedResponse.builder()
                .id(feed.getId())
                .display_name(feed.getUser().getDisplayName())
                .content(feed.getContent())
                .images(imagePaths)
                .build());
        }
        return mainFeedDtos;
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
