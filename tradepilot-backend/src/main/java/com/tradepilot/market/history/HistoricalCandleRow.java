package com.tradepilot.market.history;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record HistoricalCandleRow(

        OffsetDateTime timestamp,

        BigDecimal open,
        BigDecimal high,
        BigDecimal low,
        BigDecimal close,

        Long volume

) {
}