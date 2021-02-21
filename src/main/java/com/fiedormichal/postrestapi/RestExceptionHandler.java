package com.fiedormichal.postrestapi;

import com.fiedormichal.postrestapi.apierror.ApiError;
import com.fiedormichal.postrestapi.exception.PostNotFoundException;
import com.fiedormichal.postrestapi.exception.PostsUpdateFailedException;
import com.fiedormichal.postrestapi.exception.UserNotFoundException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex,
                                                        HttpHeaders headers,
                                                        HttpStatus status,
                                                        WebRequest request) {
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());

        ApiError apiError = new ApiError(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST,
                "Mismatch Type",
                errors);
        return buildResponseEntity(apiError);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        List<String> errors;
        errors= ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error->error.getObjectName() + " : " + error.getDefaultMessage())
                .collect(Collectors.toList());

        ApiError apiError = new ApiError(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST,
                "Validation Errors",
                errors);

        return buildResponseEntity(apiError);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
            List<String> errors = new ArrayList<>();
            errors.add(ex.getMessage());

            ApiError apiError = new ApiError(
                    LocalDateTime.now(),
                    HttpStatus.BAD_REQUEST,
                    "Malformed JSON Request",
                    errors);
        return buildResponseEntity(apiError);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
                                                                     HttpHeaders headers,
                                                                     HttpStatus status,
                                                                     WebRequest request) {

        List<String>errors = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(type->builder.append(type).append(", "));
        errors.add(builder.toString());

        ApiError apiError = new ApiError(
                LocalDateTime.now(),
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "Unsupported Media Type.",
                errors);
        return buildResponseEntity(apiError);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex,
                                                                   HttpHeaders headers,
                                                                   HttpStatus status,
                                                                   WebRequest request) {
        List<String> errors = new ArrayList<>();
        errors.add(String.format("Could not find the %s method for URL %s",
                ex.getHttpMethod(), ex.getRequestURL()));

        ApiError apiError = new ApiError(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND,
                "Method Not Found.",
                errors);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(PostNotFoundException.class)
    protected ResponseEntity<Object> handlePostNotFound(PostNotFoundException ex){
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());

        ApiError apiError = new ApiError(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND,
                "Post Not Found.",
                errors);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(PostsUpdateFailedException.class)
    protected ResponseEntity<Object> handlePostNotFound(PostsUpdateFailedException ex){
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());

        ApiError apiError = new ApiError(
                LocalDateTime.now(),
                HttpStatus.NOT_MODIFIED,
                "Posts Update Failed.",
                errors);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(UserNotFoundException.class)
    protected ResponseEntity<Object> handlePostNotFound(UserNotFoundException ex){
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());

        ApiError apiError = new ApiError(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND,
                "User Not Found.",
                errors);
        return buildResponseEntity(apiError);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<?> handleConstraintViolation(ConstraintViolationException ex){
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());

        ApiError apiError = new ApiError(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST,
                "Constraint Violations",
                errors);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<?> handleAll(Exception ex){
        List<String> errors = new ArrayList<>();
        errors.add(ex.getLocalizedMessage());

        ApiError apiError = new ApiError(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST,
                "Error Occured",
                errors);
        return buildResponseEntity(apiError);
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError){
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
