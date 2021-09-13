package com.ae.stagram.controller;

import com.ae.stagram.dto.FeedDto;
import com.ae.stagram.dto.response.ResponseMessage;
import com.ae.stagram.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @PostMapping
    public ResponseEntity<ResponseMessage> createFeed(@RequestBody FeedDto feedDto){

        feedService.insertFeed(feedDto);

        return ResponseEntity.ok().body(ResponseMessage.builder().build());
    }
}
