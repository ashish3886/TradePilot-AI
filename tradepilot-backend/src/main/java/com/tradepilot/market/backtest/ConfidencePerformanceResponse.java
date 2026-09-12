package com.tradepilot.market.backtest;

import java.math.BigDecimal;

public record ConfidencePerformanceResponse(

        BigDecimal confidence,

        long completedTrades,
        long wins,
        long losses,
        long expired,

        BigDecimal winRatePercent,

        BigDecimal grossProfitPoints,
        BigDecimal grossLossPoints,
        BigDecimal netPnlPoints,

        BigDecimal profitFactor,
        BigDecimal averagePnlPoints,

        long buyTrades,
        long buyWins,
        BigDecimal buyWinRatePercent,

        long sellTrades,
        long sellWins,
        BigDecimal sellWinRatePercent

) {
}