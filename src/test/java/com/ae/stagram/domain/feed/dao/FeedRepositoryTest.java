package com.ae.stagram.domain.feed.dao;

import com.ae.stagram.domain.feed.domain.Feed;
import com.ae.stagram.domain.feed.domain.Image;
import com.ae.stagram.domain.feed.dto.FeedRequestDto;
import com.ae.stagram.domain.user.dao.UserRepository;
import com.ae.stagram.domain.user.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
public class FeedRepositoryTest {

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageRepository imageRepository;

    private MockMultipartFile mockMultipartFile;

    @BeforeEach
    void init() {
        createUser();

        mockMultipartFile = new MockMultipartFile("images", "imagefile.png",
            "image/png", "<<png data>>".getBytes());
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

        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        FeedRequestDto feedRequestDto = FeedRequestDto.builder()
            .content("컨텐츠 업데이트 내용")
            .images(Lists.newArrayList(mockMultipartFile))
            .build();

        Feed newFeed = Feed.builder()
            .content(feedRequestDto.getContent())
            .user(user)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .build();

        List<Image> images = Lists.newArrayList(
            Image.builder()
                .imagePath("static/test.png")
                .imageUrl("http://test.png")
                .feed(newFeed)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        newFeed.setImages(images);

        Feed savedFeed = feedRepository.save(newFeed);
        List<Image> savedImages = imageRepository.saveAll(images);

        Assertions.assertThat(savedFeed.getImages().stream().count())
            .isEqualTo(savedImages.stream().count());
    }
}