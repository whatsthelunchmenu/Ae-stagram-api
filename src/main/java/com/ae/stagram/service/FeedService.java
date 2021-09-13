package com.ae.stagram.service;

import com.ae.stagram.dto.FeedDto;
import com.ae.stagram.entity.Feed;
import com.ae.stagram.entity.Image;
import com.ae.stagram.repository.FeedRepository;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedRepository feedRepository;

    public void insertFeed(FeedDto feedDto){

//
//        Feed.builder()
//            .content(feedDto.getContent())
//            .images(Lists.newArrayList())
    }
}
