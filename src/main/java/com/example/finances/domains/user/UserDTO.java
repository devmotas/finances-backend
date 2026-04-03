package com.example.finances.domains.user;

import java.time.Instant;

public record UserDTO(
        Integer id,
        String name,
        String email,
        Instant createdAt,
        Instant updatedAt
) {
}