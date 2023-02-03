package com.kaminski.webflux.controller.impl;

import com.kaminski.webflux.controller.UserController;
import com.kaminski.webflux.mapper.UserMapper;
import com.kaminski.webflux.model.request.UserRequest;
import com.kaminski.webflux.model.response.UserResponse;
import com.kaminski.webflux.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/v1/users")
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

    private final UserService service;
    private final UserMapper mapper;

    @Override
    public ResponseEntity<Mono<Void>> save(final UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.save(request).then());
    }

    @Override
    public ResponseEntity<Mono<UserResponse>> find(String id) {
        var response = service.findById(id).map(mapper::toResponse);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    public ResponseEntity<Flux<UserResponse>> findAll() {
        var response = service.findAll().map(mapper::toResponse);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    public ResponseEntity<Mono<UserResponse>> update(String id, UserRequest request) {
        var response = service.update(id, request).map(mapper::toResponse);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    public ResponseEntity<Mono<Void>> delete(String id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.delete(id).then());
    }

}