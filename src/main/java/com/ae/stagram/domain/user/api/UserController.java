package com.ae.stagram.domain.user.api;

import com.ae.stagram.domain.user.dto.UserDto;
import com.ae.stagram.domain.user.service.UserService;
import com.ae.stagram.global.common.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("login")
    public ResponseEntity<ResponseMessage> login(
        @RequestAttribute(value = "firebaseUser") UserDto userDto) {

        userService.addUser(userDto);
        return ResponseEntity.ok().body(ResponseMessage.success());
    }

}
