package com.tradepilot.market.event;

import com.tradepilot.market.entity.Timeframe;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record MarketCandleEvent(
        Long instrumentId,
        String symbol,
        OffsetDateTime candleTimestamp,
        Timeframe timeframe,
        BigDecimal openPrice,
        BigDecimal highPrice,
        BigDecimal lowPrice,
        BigDecimal closePrice,
        Long volume
) {
}