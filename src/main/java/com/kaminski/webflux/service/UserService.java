package com.kaminski.webflux.service;

import com.kaminski.webflux.entity.User;
import com.kaminski.webflux.mapper.UserMapper;
import com.kaminski.webflux.model.request.UserRequest;
import com.kaminski.webflux.repository.UserRepository;
import com.kaminski.webflux.service.exeception.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    public Mono<User> save(final UserRequest request){
        var user = mapper.toEntity(request);
        return repository.save(user);
    }

    public Mono<User> findById(final String id){
        return handleNotFound(repository.findById(id), id);
    }

    public Flux<User> findAll(){
        return repository.findAll();
    }

    public Mono<User> update(final String id, final UserRequest request) {
        return findById(id)
                .map(entity -> mapper.toEntity(request, entity))
                .flatMap(repository::save);
    }

    public Mono<User> delete(final String id){
        return handleNotFound(repository.findEndRemove(id), id);
    }

    private <T> Mono<T> handleNotFound(Mono<T> mono, String id){

        var message = String.format(
                "Object not found. Id: %s, Type: %s", id, User.class);

        return mono.switchIfEmpty(Mono.error(
                new ObjectNotFoundException(message)
        ));
    }

}