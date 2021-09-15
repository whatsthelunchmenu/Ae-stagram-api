package com.ae.stagram.controller;

import com.ae.stagram.dto.FeedDto;
import com.ae.stagram.dto.MainFeedDto;
import com.ae.stagram.dto.UserDto;
import com.ae.stagram.dto.response.ResponseMessage;
import com.ae.stagram.dto.response.ResponseMessageHeader;
import com.ae.stagram.service.FeedService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feeds")
public class FeedController {

    private final FeedService feedService;

    @PostMapping
    public ResponseEntity<ResponseMessage> createFeed(@RequestBody FeedDto feedDto,
        @RequestAttribute(value = "firebaseUser") UserDto userDto) {

        feedService.insertFeed(feedDto, userDto);

        return ResponseEntity.ok().body(ResponseMessage.builder()
            .header(ResponseMessageHeader.builder()
                .result(true)
                .message("")
                .status(HttpStatus.OK.value())
                .build())
            .body(null)
            .build());
    }

    @PutMapping(value = "{id}")
    public ResponseEntity<ResponseMessage> putFeed(@PathVariable("id") Long id,
        @RequestBody FeedDto feedDto) {

        return ResponseEntity.ok().body(ResponseMessage.builder()
            .header(ResponseMessageHeader.builder()
                .result(true)
                .message("")
                .status(HttpStatus.OK.value())
                .build())
            .body(feedService.updateFeed(id, feedDto))
            .build());
    }

    @GetMapping
    public ResponseEntity<ResponseMessage> getFeeds() {
        return ResponseEntity.ok().body(ResponseMessage.builder()
            .header(ResponseMessageHeader.builder()
                .result(true)
                .message("")
                .status(HttpStatus.OK.value())
                .build())
            .body(feedService.getMainFeeds())
            .build());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ResponseMessage> deleteFeed(@PathVariable("id") Long id){
        feedService.removeFeed(id);
        return ResponseEntity.ok().build();
    }
}
