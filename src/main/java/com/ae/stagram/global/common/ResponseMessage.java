package com.ae.stagram.global.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseMessage {

    private ResponseMessageHeader header;

    private Object body;

    public static ResponseMessage success() {
        return ResponseMessage.builder()
            .header(ResponseMessageHeader.builder()
                .result(true)
                .message("성공했습니다.")
                .status(HttpStatus.OK.value())
                .build())
            .body(null)
            .build();
    }

    public static ResponseMessage success(Object bodyMessage) {
        return ResponseMessage.builder()
            .header(ResponseMessageHeader.builder()
                .result(true)
                .message("성공했습니다.")
                .status(HttpStatus.OK.value())
                .build())
            .body(bodyMessage)
            .build();
    }

    public static ResponseMessage fail(String errorMessage, HttpStatus httpStatus) {
        return ResponseMessage.builder()
            .header(ResponseMessageHeader.builder()
                .result(false)
                .message(errorMessage)
                .status(httpStatus.value())
                .build())
            .body(null)
            .build();
    }
}
