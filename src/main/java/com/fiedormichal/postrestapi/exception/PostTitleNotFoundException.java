package com.fiedormichal.postrestapi.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PostTitleNotFoundException extends RuntimeException {
    public PostTitleNotFoundException(String message) {
        super(message);
    }
}
