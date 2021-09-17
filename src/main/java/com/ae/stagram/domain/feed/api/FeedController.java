package com.ae.stagram.domain.feed.api;

import com.ae.stagram.domain.feed.dto.FeedRequest;
import com.ae.stagram.domain.feed.service.FeedService;
import com.ae.stagram.domain.user.dto.UserDto;
import com.ae.stagram.global.common.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    public ResponseEntity<ResponseMessage> createFeed(@RequestBody FeedRequest request,
        @RequestAttribute(value = "firebaseUser") UserDto userDto) {

        feedService.insertFeed(request, userDto);

        return ResponseEntity.ok().body(ResponseMessage.success());
    }

    @PatchMapping(value = "{id}")
    public ResponseEntity<ResponseMessage> putFeed(@PathVariable("id") Long id,
        @RequestBody FeedRequest request) {

        return ResponseEntity.ok()
            .body(ResponseMessage.success(feedService.updateFeed(id, request)));
    }

    @GetMapping
    public ResponseEntity<ResponseMessage> getFeeds() {
        return ResponseEntity.ok().body(ResponseMessage.success(feedService.getMainFeeds()));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ResponseMessage> deleteFeed(@PathVariable("id") Long id) {
        feedService.removeFeed(id);
        return ResponseEntity.ok().body(ResponseMessage.success());
    }
}
