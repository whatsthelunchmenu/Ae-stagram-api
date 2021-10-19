package com.ae.stagram.domain.feed.api;

import com.ae.stagram.domain.feed.dto.FeedRequestDto;
import com.ae.stagram.domain.feed.service.FeedService;
import com.ae.stagram.domain.user.dto.UserDto;
import com.ae.stagram.global.common.response.ResponseMessage;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feeds")
public class FeedController {

    private final FeedService feedService;

    @PostMapping
    public ResponseEntity<ResponseMessage> createFeed(@ModelAttribute FeedRequestDto request,
        @RequestAttribute(value = "firebaseUser") UserDto userDto) throws IOException {

        feedService.insertFeed(request, userDto);

        return ResponseEntity.ok().body(ResponseMessage.success());
    }

    @PatchMapping(value = "{id}")
    public ResponseEntity<ResponseMessage> putFeed(@PathVariable("id") Long id,
        @ModelAttribute FeedRequestDto request) throws IOException {

        return ResponseEntity.ok()
            .body(ResponseMessage.success(feedService.updateFeed(id, request)));
    }

    @GetMapping
    public ResponseEntity<ResponseMessage> getFeeds(
        @RequestParam(value = "nextToken", required = false) String nextToken) {
        return ResponseEntity.ok()
            .body(ResponseMessage.success(feedService.getMainFeeds(nextToken)));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ResponseMessage> deleteFeed(@PathVariable("id") Long id) {
        feedService.removeFeed(id);
        return ResponseEntity.ok().body(ResponseMessage.success());
    }
}
