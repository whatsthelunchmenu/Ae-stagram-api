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
import com.ae.stagram.global.util.s3.dto.FileUploadDto;
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

        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        Feed newFeed = Feed.builder()
            .content(createFeedRequestDto.getContent())
            .user(user)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .build();

        List<Image> feedImages = getFeedImages(createFeedRequestDto.getImages(), newFeed);
        newFeed.setImages(feedImages);

        feedRepository.save(newFeed);
        imageRepository.saveAll(feedImages);
    }

    @Transactional
    public FeedInfoDto updateFeed(Long feedId, FeedRequestDto feedRequestDto) throws IOException {
        Feed feed = feedRepository.findById(feedId)
            .orElseThrow(() -> new FeedNotFoundException("존재하지 않는 피드입니다."));

        if (!feedRequestDto.getImages().isEmpty()) {
            for (Image image : feed.getImages()) {
                s3UploaderUtils.delete(image.getImagePath());
            }
        }

        List<Long> imageIds = feed.getImages().stream()
            .map(it -> it.getId())
            .collect(Collectors.toList());
        imageRepository.deleteAllById(imageIds);

        List<FileUploadDto> imageConvertInfo = getImageConvertInfo(feedRequestDto.getImages());
        List<Image> images = new ArrayList<>();
        for (FileUploadDto fileInfo : imageConvertInfo) {
            images.add(Image.builder()
                .imagePath(fileInfo.getFileFullPath())
                .imageUrl(fileInfo.getFileUrl())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .feed(feed)
                .build());
        }
        imageRepository.saveAll(images);

        return feed.update(feedRequestDto.getContent(), images);
    }

    public FeedResponseDto getMainFeeds(String nextToken) {
        Long cursorIndex = null;
        LocalDateTime updatedAt = null;

        if (StringUtils.hasText(nextToken)) {
            String[] values = nextToken.split(PageNationUtils.splitPageInfo);

            cursorIndex = Long.parseLong(values[0]);
            updatedAt = LocalDateTime.parse(values[1]);
        }

        List<Feed> pageFeeds = pageNationUtil.getFeedPagenation(cursorIndex, updatedAt);

        List<FeedInfoDto> feedInfoDtos = new ArrayList<>();
        for (Feed feed : pageFeeds) {
            List<String> imagePaths = feed.getImages().stream()
                .map(image -> image.getImagePath())
                .collect(Collectors.toList());

            feedInfoDtos.add(FeedInfoDto.builder()
                .id(feed.getId())
                .display_name(feed.getUser().getDisplayName())
                .content(feed.getContent())
                .images(imagePaths)
                .createdAt(feed.getCreatedAt())
                .updatedAt(feed.getUpdatedAt())
                .build());
        }

        int feedCount = feedInfoDtos.size();
        String token = "";
        int pageMaxSize = pageNationUtil.getPageMaxSize();
        if (feedCount > pageMaxSize) {
            // 읽지않은 데이터가 존재하는지 파악하기위해 pageSize보다 1개 더 읽은부분 제거
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

    public void removeFeed(Long feedId) {
        feedRepository.deleteById(feedId);
    }

    private List<FileUploadDto> getImageConvertInfo(List<MultipartFile> multipartFiles)
        throws IOException {
        List<FileUploadDto> fileUploadDtos = new ArrayList<>();

        for (MultipartFile file : multipartFiles) {
            fileUploadDtos.add(s3UploaderUtils.upload(file, imageDir));
        }
        return fileUploadDtos;
    }

    private List<Image> getFeedImages(List<MultipartFile> multipartFiles, Feed feed)
        throws IOException {
        List<Image> paths = new ArrayList<>();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        for (MultipartFile file : multipartFiles) {
            FileUploadDto uploadDto = s3UploaderUtils.upload(file, imageDir);
            paths.add(Image.builder()
                .imagePath(uploadDto.getFileFullPath())
                .imageUrl(uploadDto.getFileUrl())
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .feed(feed)
                .build());
        }
        return paths;
    }
}
