package com.fiedormichal.postrestapi.exception;

import com.fiedormichal.postrestapi.apierror.ApiError;
import com.fiedormichal.postrestapi.apierror.ApiErrorMsg;
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
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.fiedormichal.postrestapi.apierror.ApiErrorMsg.*;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex,
                                                        HttpHeaders headers,
                                                        HttpStatus status,
                                                        WebRequest request) {
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());

        return buildResponseEntity(getApiError(errors, HttpStatus.BAD_REQUEST, MISMATCH_TYPE.getValue()));
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

        return buildResponseEntity(getApiError(errors, HttpStatus.BAD_REQUEST, VALIDATION_ERRORS.getValue()));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
            List<String> errors = new ArrayList<>();
            errors.add(ex.getMessage());
        return buildResponseEntity(getApiError(errors, HttpStatus.BAD_REQUEST, MALFORMED_JSON_REQUEST.getValue()));
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

        return buildResponseEntity(getApiError(errors, HttpStatus.UNSUPPORTED_MEDIA_TYPE, UNSUPPORTED_MEDIA_TYPE.getValue()));
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex,
                                                                   HttpHeaders headers,
                                                                   HttpStatus status,
                                                                   WebRequest request) {
        List<String> errors = new ArrayList<>();
        errors.add(String.format("Could not find the %s method for URL %s",
                ex.getHttpMethod(), ex.getRequestURL()));

        return buildResponseEntity(getApiError(errors, HttpStatus.NOT_FOUND, METHOD_NOT_FOUND.getValue()));
    }

    @ExceptionHandler(PostNotFoundException.class)
    protected ResponseEntity<Object> handlePostNotFound(PostNotFoundException ex){
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());

        return buildResponseEntity(getApiError(errors, HttpStatus.NOT_FOUND, POST_NOT_FOUND.getValue()));
    }

    @ExceptionHandler(PostTitleNotFoundException.class)
    protected ResponseEntity<Object> handleTitleNotFound(PostTitleNotFoundException ex){
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());
        return buildResponseEntity(getApiError(errors, HttpStatus.NOT_FOUND, POST_WITH_GIVEN_TITLE_NOT_FOUND.getValue()));
    }

    @ExceptionHandler(IOException.class)
    protected ResponseEntity<Object> handlePostNotFound(IOException ex){
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());

        return buildResponseEntity(getApiError(errors, HttpStatus.NOT_FOUND, API_CONNECTION_FAILURE.getValue()));
    }

    @ExceptionHandler(NoContentException.class)
    protected ResponseEntity<Object> handlePostNotFound(NoContentException ex){
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());

        return buildResponseEntity(getApiError(errors, HttpStatus.NOT_FOUND, POSTS_NOT_FOUND.getValue()));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<?> handleAll(Exception ex){
        List<String> errors = new ArrayList<>();
        errors.add(ex.getLocalizedMessage());

        return buildResponseEntity(getApiError(errors, HttpStatus.BAD_REQUEST, ERROR_OCCURRED.getValue()));
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
