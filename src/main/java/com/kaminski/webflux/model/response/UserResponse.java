package com.kaminski.webflux.model.response;

public record UserResponse(
        String id,
        String name,
        String email,
        String password
) {
}
