package com.ae.stagram.controller;

import com.ae.stagram.dto.UserDto;
import com.ae.stagram.entity.User;
import com.ae.stagram.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("login")
    public ResponseEntity<?> joinUser(@RequestAttribute(value = "firebaseUser") UserDto userDto) {

        User user = User.builder()
            .uuid(userDto.getUuid())
            .email(userDto.getEmail())
            .displayName(userDto.getDisplayName())
            .build();
        userService.addUser(user);

        return ResponseEntity.ok().build();
    }

}
