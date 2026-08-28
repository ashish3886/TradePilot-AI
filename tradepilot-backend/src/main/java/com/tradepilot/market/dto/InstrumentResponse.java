package com.tradepilot.market.dto;

import java.time.OffsetDateTime;

public record InstrumentResponse(
        Long id,
        String symbol,
        String name,
        String exchange,
        String instrumentType,
        boolean active,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}