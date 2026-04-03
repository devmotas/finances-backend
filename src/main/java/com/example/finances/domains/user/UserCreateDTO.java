package com.example.finances.domains.user;

import java.time.Instant;

public record UserCreateDTO(
        String name,
        String email,
        String password
) {
}