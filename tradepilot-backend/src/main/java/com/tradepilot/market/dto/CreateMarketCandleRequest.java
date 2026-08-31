package com.tradepilot.market.dto;

import com.tradepilot.market.entity.Timeframe;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record CreateMarketCandleRequest(

        @NotNull
        Long instrumentId,

        @NotNull
        OffsetDateTime candleTimestamp,

        @NotNull
        Timeframe timeframe,

        @NotNull
        @DecimalMin("0.0")
        BigDecimal openPrice,

        @NotNull
        @DecimalMin("0.0")
        BigDecimal highPrice,

        @NotNull
        @DecimalMin("0.0")
        BigDecimal lowPrice,

        @NotNull
        @DecimalMin("0.0")
        BigDecimal closePrice,

        @NotNull
        @PositiveOrZero
        Long volume
) {
}