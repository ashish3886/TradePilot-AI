package com.tradepilot.dto;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}