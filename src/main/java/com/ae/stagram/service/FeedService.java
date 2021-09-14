package com.ae.stagram.service;

import com.ae.stagram.dto.FeedDto;
import com.ae.stagram.dto.MainFeedDto;
import com.ae.stagram.dto.UserDto;
import com.ae.stagram.entity.Feed;
import com.ae.stagram.entity.Image;
import com.ae.stagram.entity.User;
import com.ae.stagram.exception.UserNotFoundException;
import com.ae.stagram.repository.FeedRepository;
import com.ae.stagram.repository.ImageRepository;
import com.ae.stagram.repository.UserRepository;
import com.google.common.collect.Lists;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
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

        List<Image> images = new ArrayList<>();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        Feed newFeed = Feed.builder()
            .content(feedDto.getContent())
            .user(user)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .build();

        for (String path : feedDto.getImages()) {
            images.add(Image.builder()
                .imagePath(path)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .feed(newFeed)
                .build());
        }
        newFeed.setImages(images);

        feedRepository.save(newFeed);
        imageRepository.saveAll(images);
    }

    public List<MainFeedDto> getMainFeeds() {

        List<Feed> feeds = feedRepository.findAll(Sort.by(Direction.DESC, "updatedAt"));

        List<MainFeedDto> mainFeedDtos = new ArrayList<>();
        for (Feed feed : feeds) {
            mainFeedDtos.add(MainFeedDto.builder()
                .uuid(feed.getUser().getUuid())
                .display_name(feed.getUser().getDisplayName())
                .content(feed.getContent())
                .images(feed.getImages())
                .build());
        }
        return mainFeedDtos;
    }
}
