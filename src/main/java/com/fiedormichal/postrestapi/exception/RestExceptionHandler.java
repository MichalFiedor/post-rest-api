package com.fiedormichal.postrestapi.exception;

import com.fiedormichal.postrestapi.apierror.ApiError;
import com.fiedormichal.postrestapi.exception.NoContentException;
import com.fiedormichal.postrestapi.exception.PostNotFoundException;
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
import java.io.IOException;
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

        return buildResponseEntity(getApiError(errors, HttpStatus.BAD_REQUEST, "Mismatch Type."));
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

        return buildResponseEntity(getApiError(errors, HttpStatus.BAD_REQUEST, "Validation Errors."));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
            List<String> errors = new ArrayList<>();
            errors.add(ex.getMessage());
        return buildResponseEntity(getApiError(errors, HttpStatus.BAD_REQUEST, "Malformed JSON Request."));
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

        return buildResponseEntity(getApiError(errors, HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported Media Type."));
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex,
                                                                   HttpHeaders headers,
                                                                   HttpStatus status,
                                                                   WebRequest request) {
        List<String> errors = new ArrayList<>();
        errors.add(String.format("Could not find the %s method for URL %s",
                ex.getHttpMethod(), ex.getRequestURL()));

        return buildResponseEntity(getApiError(errors, HttpStatus.NOT_FOUND, "Method Not Found."));
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
        return buildResponseEntity(getApiError(errors, HttpStatus.NOT_FOUND, "Post Not Found."));
    }

    @ExceptionHandler(IOException.class)
    protected ResponseEntity<Object> handlePostNotFound(IOException ex){
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());

        return buildResponseEntity(getApiError(errors, HttpStatus.NOT_FOUND, "API Connection Failure."));
    }

    @ExceptionHandler(NoContentException.class)
    protected ResponseEntity<Object> handlePostNotFound(NoContentException ex){
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());

        return buildResponseEntity(getApiError(errors, HttpStatus.NOT_FOUND, "Post Not Found."));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<?> handleConstraintViolation(ConstraintViolationException ex){
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());

        return buildResponseEntity(getApiError(errors, HttpStatus.BAD_REQUEST, "Constraint Violations."));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<?> handleAll(Exception ex){
        List<String> errors = new ArrayList<>();
        errors.add(ex.getLocalizedMessage());

        return buildResponseEntity(getApiError(errors, HttpStatus.BAD_REQUEST, "Error Occurred."));
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError){
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    private ApiError getApiError(List<String> errors, HttpStatus status, String message) {
        return new ApiError(
                LocalDateTime.now(),
                status,
                message,
                errors);
    }
}