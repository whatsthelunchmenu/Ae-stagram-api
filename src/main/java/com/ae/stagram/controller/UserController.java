package com.ae.stagram.controller;

import com.ae.stagram.dto.UserDto;
import com.ae.stagram.entity.User;
import com.ae.stagram.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestAttribute(value = "firebaseUser") UserDto userDto) {

        userService.addUser(userDto);

        return ResponseEntity.ok().build();
    }

}
