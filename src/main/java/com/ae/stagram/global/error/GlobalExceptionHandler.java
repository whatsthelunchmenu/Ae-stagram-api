package com.ae.stagram.global.error;

import com.ae.stagram.domain.user.exception.UserNotFoundException;
import com.ae.stagram.global.common.ResponseMessage;
import com.ae.stagram.global.common.ResponseMessageHeader;
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
            .body(ResponseMessage.fail(ex.getMessage(), HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<?> UserException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ResponseMessage.fail(ex.getMessage(), HttpStatus.UNAUTHORIZED));
    }
}
