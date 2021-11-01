package com.ae.stagram.domain.feed.service;

import com.ae.stagram.domain.feed.dao.FeedRepository;
import com.ae.stagram.domain.feed.dao.ImageRepository;
import com.ae.stagram.domain.feed.domain.Feed;
import com.ae.stagram.domain.feed.domain.Image;
import com.ae.stagram.domain.feed.dto.FeedInfoDto;
import com.ae.stagram.domain.feed.dto.FeedRequestDto;
import com.ae.stagram.domain.feed.dto.FeedResponseDto;
import com.ae.stagram.domain.feed.exception.FeedNotFoundException;
import com.ae.stagram.domain.user.dao.UserRepository;
import com.ae.stagram.domain.user.domain.User;
import com.ae.stagram.domain.user.dto.UserDto;
import com.ae.stagram.domain.user.exception.UserNotFoundException;
import com.ae.stagram.global.util.pageable.PageNationUtils;
import com.ae.stagram.global.util.s3.S3UploaderUtils;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedRepository feedRepository;

    private final UserRepository userRepository;

    private final ImageRepository imageRepository;

    private final PageNationUtils pageNationUtil;

    private final S3UploaderUtils s3UploaderUtils;

    private final String imageDir = "static";

    @Transactional
    public void insertFeed(FeedRequestDto createFeedRequestDto, UserDto userDto)
        throws IOException {

        User user = userRepository.findByUuid(userDto.getUuid())
            .orElseThrow(() -> new UserNotFoundException("등록되지 않은 사용자입니다."));

        List<Image> feedImages = new ArrayList<>();

        Feed feed = createFeedRequestDto.toEntity(user, feedImages);
        feedImages = saveToImageStorage(createFeedRequestDto.getImages(), feed);
        feed.setImages(feedImages);

        feedRepository.save(feed);
        imageRepository.saveAll(feedImages);
    }

    @Transactional
    public FeedInfoDto updateFeed(Long feedId, FeedRequestDto feedRequestDto) throws IOException {
        Feed feed = feedRepository.findById(feedId)
            .orElseThrow(() -> new FeedNotFoundException("존재하지 않는 피드입니다."));

        for (Image image : feed.getImages()) {
            s3UploaderUtils.delete(image.getImagePath());
        }

        List<Long> imageIds = feed.getImages().stream()
            .map(it -> it.getId())
            .collect(Collectors.toList());
        imageRepository.deleteAllById(imageIds);

        List<Image> feedImages = saveToImageStorage(feedRequestDto.getImages(), feed);

        imageRepository.saveAll(feedImages);

        return feed.update(feedRequestDto.getContent(), feedImages);
    }

    public FeedResponseDto getMainFeeds(String nextToken) {
        Long cursorIndex = null;
        LocalDateTime updatedAt = null;

        if (StringUtils.hasText(nextToken)) {
            String[] values = nextToken.split(PageNationUtils.splitPageInfo);

            cursorIndex = Long.parseLong(values[0]);
            updatedAt = LocalDateTime.parse(values[1]);
        }

        List<FeedInfoDto> feedInfoDtos = pageNationUtil.getFeedPagenation(cursorIndex, updatedAt)
            .stream().map(FeedInfoDto::new)
            .collect(Collectors.toList());

        int feedCount = feedInfoDtos.size();
        String token = "";
        int pageMaxSize = pageNationUtil.getPageMaxSize();
        if (feedCount > pageMaxSize) {
            // 이후 데이터가 존재하는지 파악하기위해 pageSize보다 1개 더 읽은부분 제거
            feedInfoDtos.remove(feedCount - 1);

            FeedInfoDto feedInfoDto = feedInfoDtos.get(pageMaxSize - 1);
            token = String.format("%d%s%s", feedInfoDto.getId(), PageNationUtils.splitPageInfo,
                feedInfoDto.getUpdatedAt());
        }

        return FeedResponseDto.builder()
            .hasNextToken(token)
            .feedInfos(feedInfoDtos)
            .maxResults(pageNationUtil.getPageMaxSize())
            .build();
    }

    @Transactional
    public void removeFeed(Long feedId) {
        Feed feed = feedRepository.findById(feedId)
            .orElseThrow(
                () -> new IllegalArgumentException("해당 피드가 존재하지 않습니다. feedId = " + feedId));

        List<String> imagePaths = feed.getImages().stream().map(image -> image.getImagePath())
            .collect(Collectors.toList());
        for (String path : imagePaths) {
            s3UploaderUtils.delete(path);
        }

        feedRepository.deleteById(feedId);
    }

    private List<Image> saveToImageStorage(List<MultipartFile> multipartFiles, Feed feed)
        throws IOException {

        List<Image> images = new ArrayList<>();

        for (MultipartFile file : multipartFiles) {
            if (!file.isEmpty()){
                images.add(s3UploaderUtils.upload(file, imageDir).toEntity(feed));
            }
        }
        return images;
    }
}
