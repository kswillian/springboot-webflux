package com.kaminski.webflux.controller;

import com.kaminski.webflux.entity.User;
import com.kaminski.webflux.mapper.UserMapper;
import com.kaminski.webflux.model.request.UserRequest;
import com.kaminski.webflux.model.response.UserResponse;
import com.kaminski.webflux.service.UserService;
import com.mongodb.reactivestreams.client.MongoClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;
import static reactor.core.publisher.Mono.just;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
class UserControllerImplTest {

    private static final String ID = "123";
    private static final String NAME = "Willian";
    private static final String EMAIL = "willian@mail.com";
    private static final String PASSWORD = "123";
    private static final String BASE_URI = "/v1/users";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserService service;

    @MockBean
    private UserMapper mapper;

    @MockBean
    private MongoClient mongoClient;

    @Test
    @DisplayName("Test endpoint save with success")
    void testSaveWithSuccess() {

        final var request = new UserRequest("test", "test@mail.com", "123");
        final var entity = User.builder().name("test").email("test").build();

        when(service.save(any(UserRequest.class))).thenReturn(just(entity));

        webTestClient.post().uri(BASE_URI)
                .contentType(APPLICATION_JSON)
                .body(fromValue(request))
                .exchange()
                .expectStatus()
                .isCreated();

        verify(service).save(any(UserRequest.class));
    }

    @Test
    @DisplayName("Test endpoint save with bad request")
    void testSaveWithBadRequest() {

        final var request = new UserRequest("test ", "test@mail.com", "123");

        webTestClient.post().uri(BASE_URI)
                .contentType(APPLICATION_JSON)
                .body(fromValue(request))
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.path").isEqualTo("/v1/users")
                .jsonPath("$.status").isEqualTo(BAD_REQUEST.value())
                .jsonPath("$.error").isEqualTo("Validation error");

    }


    @Test
    @DisplayName("Test find by id endpoint with success")
    void testFindByIdWithSuccess() {

        final var response = new UserResponse(ID, NAME, EMAIL, PASSWORD);
        final var entity = User.builder().name(NAME).email(EMAIL).build();

        when(service.findById(anyString())).thenReturn(just(entity));
        when(mapper.toResponse(any(User.class))).thenReturn(response);

        webTestClient.get().uri(BASE_URI.concat("/").concat(ID))
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(ID)
                .jsonPath("$.name").isEqualTo(NAME)
                .jsonPath("$.email").isEqualTo(EMAIL)
                .jsonPath("$.password").isEqualTo(PASSWORD);

        verify(service).findById(anyString());

    }

    @Test
    @DisplayName("Test find all endpoint with success")
    void testFindAllWithSuccess() {

        final var response = new UserResponse(ID, NAME, EMAIL, PASSWORD);
        final var entity = User.builder().name(NAME).email(EMAIL).build();

        when(service.findAll()).thenReturn(Flux.just(entity));
        when(mapper.toResponse(any(User.class))).thenReturn(response);

        webTestClient.get().uri(BASE_URI)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(ID)
                .jsonPath("$[0].name").isEqualTo(NAME)
                .jsonPath("$[0].email").isEqualTo(EMAIL)
                .jsonPath("$[0].password").isEqualTo(PASSWORD);

        verify(service).findAll();

    }

    @Test
    @DisplayName("Test update endpoint with success")
    void testUpdateWithSuccess() {

        final var request = new UserRequest(NAME, EMAIL, PASSWORD);
        final var response = new UserResponse(ID, NAME, EMAIL, PASSWORD);
        final var entity = User.builder().name(NAME).email(EMAIL).build();

        when(service.update(anyString(), any(UserRequest.class))).thenReturn(just(entity));
        when(mapper.toResponse(any(User.class))).thenReturn(response);

        webTestClient.patch().uri(BASE_URI.concat("/".concat(ID)))
                .contentType(APPLICATION_JSON)
                .body(fromValue(request))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(ID)
                .jsonPath("$.name").isEqualTo(NAME)
                .jsonPath("$.email").isEqualTo(EMAIL)
                .jsonPath("$.password").isEqualTo(PASSWORD);

        verify(service).update(anyString(), any(UserRequest.class));
        verify(mapper).toResponse(any(User.class));

    }

    @Test
    void testDeleteWithSuccess() {

        final var entity = User.builder().name(NAME).email(EMAIL).build();

        when(service.delete(anyString())).thenReturn(just(entity));

        webTestClient.delete().uri(BASE_URI.concat("/").concat(ID))
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        verify(service).delete(anyString());

    }

}