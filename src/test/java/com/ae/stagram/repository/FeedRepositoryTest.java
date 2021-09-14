package com.ae.stagram.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.ae.stagram.dto.FeedDto;
import com.ae.stagram.entity.Feed;
import com.ae.stagram.entity.Image;
import com.ae.stagram.entity.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class FeedRepositoryTest {

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageRepository imageRepository;

    @BeforeEach
    void init() {
        createUser();
    }

    private void createUser() {
        User user = User.builder()
            .id(1L)
            .uuid("123456")
            .email("test@naver.com")
            .displayName("test")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        userRepository.save(user);
    }

    @Test
    public void 피드_추가() {
        User user = userRepository.findByUuid("123456")
            .orElseThrow(RuntimeException::new);

        List<Image> images = new ArrayList<>();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        FeedDto feedDto = FeedDto.builder()
            .content("컨텐츠 내용입니다.")
            .images(Lists.newArrayList("http://image1.jpg", "http://image2.jpg"))
            .build();

        Feed newFeed = Feed.builder()
            .content(feedDto.getContent())
            .user(user)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .build();

        for (String path : feedDto.getImages()) {
            images.add(Image.builder()
                .imagePath(path)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .feed(newFeed)
                .build());
        }

        newFeed.setImages(images);

        Feed save = feedRepository.save(newFeed);
        imageRepository.saveAll(images);

        System.out.println(save);
        System.out.println(save.getUser());
    }
}