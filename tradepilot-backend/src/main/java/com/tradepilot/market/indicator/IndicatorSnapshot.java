package com.tradepilot.market.indicator;

import com.tradepilot.market.entity.Timeframe;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record IndicatorSnapshot(
        Long instrumentId,
        String symbol,
        Timeframe timeframe,
        OffsetDateTime candleTimestamp,
        BigDecimal closePrice,
        BigDecimal ema9,
        BigDecimal ema21,
        BigDecimal rsi14,
        BigDecimal vwap,
        BigDecimal macd,
        BigDecimal macdSignal,
        BigDecimal macdHistogram,
        BigDecimal atr14
) {
}