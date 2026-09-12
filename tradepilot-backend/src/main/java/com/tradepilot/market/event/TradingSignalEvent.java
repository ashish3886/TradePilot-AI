package com.tradepilot.market.event;

import com.tradepilot.market.entity.Timeframe;
import com.tradepilot.market.signal.SignalType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record TradingSignalEvent(
        Long instrumentId,
        String symbol,
        Timeframe timeframe,
        OffsetDateTime candleTimestamp,

        SignalType signal,

        int bullishScore,
        int bearishScore,

        BigDecimal confidence,

        BigDecimal entry,
        BigDecimal stopLoss,
        BigDecimal target,
        BigDecimal riskRewardRatio,

        List<String> reasons
) {
}