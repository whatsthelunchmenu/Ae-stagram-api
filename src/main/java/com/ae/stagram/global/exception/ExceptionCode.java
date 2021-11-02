package com.ae.stagram.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {

    // Header
    TOKEN_NOT_FOUND(400, "유효하지 않는 토큰입니다."),

    // User
    USER_NOT_FOUND(400, "유저 정보를 찾을 수 없습니다."),

    // Feed
    FEED_NOT_FOUND(400, "피드를 찾을 수 없습니다."),

    ;

    private final int status;
    private final String messsage;
}
