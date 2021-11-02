package com.ae.stagram.global.exception;

import com.ae.stagram.domain.user.exception.UserNotFoundException;
import com.ae.stagram.global.common.response.ResponseMessage;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

//    @ExceptionHandler(value = {TokenNotFoundException.class, IllegalArgumentException.class,
//        FirebaseAuthException.class})
//    public ResponseEntity<?> authException(RuntimeException ex) {
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//            .body(ResponseMessage.fail(ex.getMessage(), HttpStatus.UNAUTHORIZED));
//    }
//
//    @ExceptionHandler(value = UserNotFoundException.class)
//    public ResponseEntity<?> userException(RuntimeException ex) {
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//            .body(ResponseMessage.fail(ex.getMessage(), HttpStatus.UNAUTHORIZED));
//    }
//
//    @ExceptionHandler(value = NullPointerException.class)
//    public ResponseEntity<?> test(RuntimeException ex){
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//            .body(ResponseMessage.fail(ex.getMessage(), HttpStatus.UNAUTHORIZED));
//    }

    @ExceptionHandler(value = AestagramException.class)
    public ResponseEntity<?> errorHandler(AestagramException ex){
        return ResponseEntity.status(HttpStatus.valueOf(ex.getHttpStatus()))
                .body(ResponseMessage.fail(ex.getMessage(), ex.getHttpStatus()));
    }

}
