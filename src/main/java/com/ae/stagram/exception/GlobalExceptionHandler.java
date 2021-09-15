package com.ae.stagram.exception;

import com.ae.stagram.dto.response.ResponseMessage;
import com.ae.stagram.dto.response.ResponseMessageHeader;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {TokenNotFoundException.class, IllegalArgumentException.class,
        FirebaseAuthException.class})
    public ResponseEntity<?> AuthException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ResponseMessage.builder()
                .header(ResponseMessageHeader.builder()
                    .result(false)
                    .message(ex.getMessage())
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .build())
                .body(null)
                .build());
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<?> UserException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ResponseMessage.builder()
                .header(ResponseMessageHeader.builder()
                    .result(false)
                    .message(ex.getMessage())
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .build())
                .body(null)
                .build());
    }
}
