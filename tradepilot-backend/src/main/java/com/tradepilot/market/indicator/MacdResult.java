package com.tradepilot.market.indicator;

import java.math.BigDecimal;

public record MacdResult(
        BigDecimal macd,
        BigDecimal signal,
        BigDecimal histogram
) {
}