package com.kaminski.webflux.service;

import com.kaminski.webflux.entity.User;
import com.kaminski.webflux.mapper.UserMapper;
import com.kaminski.webflux.model.request.UserRequest;
import com.kaminski.webflux.repository.UserRepository;
import com.kaminski.webflux.service.exeception.ObjectNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserService service;

    @Test
    void testSave() {

        var request = new UserRequest("test", "test@mail.com", "123");
        var entity = User.builder().name("test").email("test").build();

        when(mapper.toEntity(any(UserRequest.class))).thenReturn(entity);
        when(repository.save(any(User.class))).thenReturn(Mono.just(entity));

        var response = service.save(request);

        StepVerifier.create(response)
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();

        verify(repository).save(any(User.class));

    }

    @Test
    void testFindById() {

        var entity = User.builder().name("test").email("test").build();
        when(repository.findById(anyString())).thenReturn(Mono.just(entity));

        var response = service.findById("1");

        StepVerifier.create(response)
                .expectNextMatches(user -> user.getClass().equals(User.class))
                .expectComplete()
                .verify();

        verify(repository).findById(anyString());

    }

    @Test
    void testFindAll() {

        var entity = User.builder().name("test").email("test").build();
        when(repository.findAll()).thenReturn(Flux.just(entity));

        var response = service.findAll();

        StepVerifier.create(response)
                .expectNextMatches(user -> user.getClass().equals(User.class))
                .expectComplete()
                .verify();

        verify(repository).findAll();

    }

    @Test
    void testUpdate() {

        var request = new UserRequest("test", "test@mail.com", "123");
        var entity = User.builder().name("test").email("test").build();

        when(mapper.toEntity(any(UserRequest.class), any(User.class))).thenReturn(entity);
        when(repository.findById(anyString())).thenReturn(Mono.just(entity));
        when(repository.save(any(User.class))).thenReturn(Mono.just(entity));

        var response = service.update("1", request);

        StepVerifier.create(response)
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();

        verify(repository).save(any(User.class));

    }

    @Test
    void testDelete() {

        var entity = User.builder().name("test").email("test").build();
        when(repository.findEndRemove(anyString())).thenReturn(Mono.just(entity));

        var response = service.delete("1");

        StepVerifier.create(response)
                .expectNextMatches(user -> user.getClass().equals(User.class))
                .expectComplete()
                .verify();

        verify(repository).findEndRemove(anyString());

    }

    @Test
    void testHandleNotFound(){
        when(repository.findById(anyString())).thenReturn(Mono.empty());

        try {
            service.findById("1").block();
        }catch (Exception e){
            assertEquals(ObjectNotFoundException.class, e.getClass());
        }

    }

}