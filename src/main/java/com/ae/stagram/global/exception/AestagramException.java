package com.ae.stagram.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class AestagramException extends RuntimeException{

    private ExceptionCode code;

    public AestagramException(ExceptionCode code) {
        this.code = code;
    }

    public AestagramException(ExceptionCode code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public int getHttpStatus(){
        return code.getStatus();
    }

}
