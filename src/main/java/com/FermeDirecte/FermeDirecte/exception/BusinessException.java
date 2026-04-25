// src/main/java/com/FermeDirecte/FermeDirecte/exception/BusinessException.java
package com.FermeDirecte.FermeDirecte.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final HttpStatus status;

    public BusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
