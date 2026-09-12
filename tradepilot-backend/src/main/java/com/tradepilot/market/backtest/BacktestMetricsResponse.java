package com.tradepilot.market.backtest;

import com.tradepilot.market.entity.Timeframe;

import java.math.BigDecimal;

public record BacktestMetricsResponse(

        Long instrumentId,
        Timeframe timeframe,

        long totalSignals,

        long completedTrades,
        long pendingTrades,
        long skippedSignals,

        long wins,
        long losses,
        long expired,

        BigDecimal winRatePercent,

        BigDecimal grossProfitPoints,
        BigDecimal grossLossPoints,
        BigDecimal netPnlPoints,

        BigDecimal profitFactor,
        BigDecimal expectancyPoints,

        BigDecimal averageBarsHeld,

        long buyCompletedTrades,
        BigDecimal buyWinRatePercent,

        long sellCompletedTrades,
        BigDecimal sellWinRatePercent

) {
}