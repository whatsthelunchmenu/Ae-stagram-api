package com.ae.stagram.domain.user.api;

import com.ae.stagram.domain.user.dto.UserDto;
import com.ae.stagram.global.common.ResponseMessage;
import com.ae.stagram.global.common.ResponseMessageHeader;
import com.ae.stagram.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

        return ResponseEntity.ok().body(ResponseMessage.builder()
            .header(ResponseMessageHeader.builder()
                .result(true)
                .message("")
                .status(HttpStatus.OK.value())
                .build()).body(null)
            .build());
    }

}
