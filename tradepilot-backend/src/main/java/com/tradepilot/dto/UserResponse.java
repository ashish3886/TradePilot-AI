package com.tradepilot.dto;

import java.time.OffsetDateTime;

public record UserResponse(
        Long id,
        String username,
        String email,
        OffsetDateTime createdAt
) {
}