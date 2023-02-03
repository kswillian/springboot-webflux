package com.kaminski.webflux.controller.exceptions;

import com.kaminski.webflux.service.exeception.ObjectNotFoundException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Mono<StandardError>> duplicateKeyException(
            DuplicateKeyException exception, ServerHttpRequest request){
        return ResponseEntity.status(BAD_REQUEST).body(
                Mono.just(
                        StandardError.builder()
                        .timestamp(now())
                        .status(BAD_REQUEST.value())
                        .error(BAD_REQUEST.getReasonPhrase())
                        .message(verifyDupKey(exception.getMessage()))
                        .path(request.getPath().toString())
                        .build()
                )
        );
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<Mono<ValidationError>> validationInputFieldsErrors(
            WebExchangeBindException exception, ServerHttpRequest request){
        ValidationError error = new ValidationError(
                now(), request.getPath().toString(), BAD_REQUEST.value(),
                "Validation error", "Error on validation attributes"
        );

        for(FieldError fieldError: exception.getBindingResult().getFieldErrors()){
            error.addError(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.status(BAD_REQUEST).body(Mono.just(error));

    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<Mono<StandardError>> objectNotFoundException(
            ObjectNotFoundException exception, ServerHttpRequest request){
        return ResponseEntity.status(NOT_FOUND).body(
                Mono.just(
                        StandardError.builder()
                                .timestamp(now())
                                .status(NOT_FOUND.value())
                                .error(NOT_FOUND.getReasonPhrase())
                                .message(exception.getMessage())
                                .path(request.getPath().toString())
                                .build()
                )
        );
    }

    private String verifyDupKey(String message) {
        if (message.contains("email dup key")) {
            return "E-mail already registered";
        }
        return "Dup key exception";
    }

}