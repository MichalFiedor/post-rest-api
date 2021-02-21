package com.fiedormichal.postrestapi.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_MODIFIED)
public class PostsUpdateFailedException extends RuntimeException {
    public PostsUpdateFailedException(String message) {
        super(message);
    }
}
