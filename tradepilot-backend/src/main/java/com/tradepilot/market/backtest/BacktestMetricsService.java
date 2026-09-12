package com.tradepilot.market.backtest;

import com.tradepilot.market.entity.Timeframe;
import com.tradepilot.market.entity.TradingSignalEntity;
import com.tradepilot.market.repository.TradingSignalRepository;
import com.tradepilot.market.signal.SignalEvaluationStatus;
import com.tradepilot.market.signal.SignalOutcome;
import com.tradepilot.market.signal.SignalType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BacktestMetricsService {

    private final TradingSignalRepository repository;

    @Transactional(readOnly = true)
    public BacktestMetricsResponse calculate(
            Long instrumentId,
            Timeframe timeframe
    ) {

        long totalSignals =
                repository.countByInstrumentIdAndTimeframe(
                        instrumentId,
                        timeframe
                );

        long pendingTrades =
                repository
                        .countByInstrumentIdAndTimeframeAndEvaluationStatus(
                                instrumentId,
                                timeframe,
                                SignalEvaluationStatus.PENDING
                        );

        long skippedSignals =
                repository
                        .countByInstrumentIdAndTimeframeAndEvaluationStatus(
                                instrumentId,
                                timeframe,
                                SignalEvaluationStatus.SKIPPED
                        );

        List<TradingSignalEntity> completed =
                repository
                        .findByInstrumentIdAndTimeframeAndEvaluationStatusOrderByCandleTimestampAsc(
                                instrumentId,
                                timeframe,
                                SignalEvaluationStatus.COMPLETED
                        );

        long completedTrades =
                completed.size();

        long wins = 0;
        long losses = 0;
        long expired = 0;

        long buyCompleted = 0;
        long sellCompleted = 0;

        long buyWins = 0;
        long buyLosses = 0;

        long sellWins = 0;
        long sellLosses = 0;

        long barsHeldTotal = 0;
        long barsHeldCount = 0;

        BigDecimal grossProfit =
                BigDecimal.ZERO;

        BigDecimal grossLoss =
                BigDecimal.ZERO;

        for (TradingSignalEntity trade : completed) {

            // ---------------------------
            // Outcome statistics
            // ---------------------------

            if (trade.getOutcome()
                    == SignalOutcome.WIN) {

                wins++;

            } else if (trade.getOutcome()
                    == SignalOutcome.LOSS) {

                losses++;

            } else if (trade.getOutcome()
                    == SignalOutcome.EXPIRED) {

                expired++;
            }

            // ---------------------------
            // BUY / SELL statistics
            // ---------------------------

            if (trade.getSignal()
                    == SignalType.BUY) {

                buyCompleted++;

                if (trade.getOutcome()
                        == SignalOutcome.WIN) {

                    buyWins++;

                } else if (trade.getOutcome()
                        == SignalOutcome.LOSS) {

                    buyLosses++;
                }

            } else if (trade.getSignal()
                    == SignalType.SELL) {

                sellCompleted++;

                if (trade.getOutcome()
                        == SignalOutcome.WIN) {

                    sellWins++;

                } else if (trade.getOutcome()
                        == SignalOutcome.LOSS) {

                    sellLosses++;
                }
            }

            // ---------------------------
            // Bars held
            // ---------------------------

            if (trade.getBarsHeld() != null) {

                barsHeldTotal +=
                        trade.getBarsHeld();

                barsHeldCount++;
            }

            // ---------------------------
            // P&L calculation
            // ---------------------------

            BigDecimal pnl =
                    calculatePnl(trade);

            if (pnl == null) {
                continue;
            }

            if (pnl.compareTo(
                    BigDecimal.ZERO
            ) > 0) {

                grossProfit =
                        grossProfit.add(pnl);

            } else if (pnl.compareTo(
                    BigDecimal.ZERO
            ) < 0) {

                grossLoss =
                        grossLoss.add(
                                pnl.abs()
                        );
            }
        }

        BigDecimal netPnl =
                grossProfit.subtract(
                        grossLoss
                );

        BigDecimal winRate =
                percentage(
                        wins,
                        wins + losses
                );

        BigDecimal buyWinRate =
                percentage(
                        buyWins,
                        buyWins + buyLosses
                );

        BigDecimal sellWinRate =
                percentage(
                        sellWins,
                        sellWins + sellLosses
                );

        BigDecimal profitFactor = null;

        if (grossLoss.compareTo(
                BigDecimal.ZERO
        ) > 0) {

            profitFactor =
                    grossProfit.divide(
                            grossLoss,
                            4,
                            RoundingMode.HALF_UP
                    );
        }

        BigDecimal expectancy = null;

        if (completedTrades > 0) {

            expectancy =
                    netPnl.divide(
                            BigDecimal.valueOf(
                                    completedTrades
                            ),
                            4,
                            RoundingMode.HALF_UP
                    );
        }

        BigDecimal averageBarsHeld = null;

        if (barsHeldCount > 0) {

            averageBarsHeld =
                    BigDecimal
                            .valueOf(
                                    barsHeldTotal
                            )
                            .divide(
                                    BigDecimal.valueOf(
                                            barsHeldCount
                                    ),
                                    2,
                                    RoundingMode.HALF_UP
                            );
        }

        return new BacktestMetricsResponse(
                instrumentId,
                timeframe,

                totalSignals,

                completedTrades,
                pendingTrades,
                skippedSignals,

                wins,
                losses,
                expired,

                winRate,

                scale(grossProfit),
                scale(grossLoss),
                scale(netPnl),

                profitFactor,
                expectancy,

                averageBarsHeld,

                buyCompleted,
                buyWinRate,

                sellCompleted,
                sellWinRate
        );
    }

    private BigDecimal calculatePnl(
            TradingSignalEntity trade
    ) {

        if (trade.getEntryPrice() == null
                || trade.getExitPrice() == null) {

            return null;
        }

        if (trade.getSignal()
                == SignalType.BUY) {

            return trade
                    .getExitPrice()
                    .subtract(
                            trade.getEntryPrice()
                    );
        }

        if (trade.getSignal()
                == SignalType.SELL) {

            return trade
                    .getEntryPrice()
                    .subtract(
                            trade.getExitPrice()
                    );
        }

        return null;
    }

    private BigDecimal percentage(
            long numerator,
            long denominator
    ) {

        if (denominator == 0) {
            return BigDecimal.ZERO
                    .setScale(2);
        }

        return BigDecimal
                .valueOf(numerator)
                .multiply(
                        BigDecimal.valueOf(100)
                )
                .divide(
                        BigDecimal.valueOf(
                                denominator
                        ),
                        2,
                        RoundingMode.HALF_UP
                );
    }

    private BigDecimal scale(
            BigDecimal value
    ) {

        return value.setScale(
                4,
                RoundingMode.HALF_UP
        );
    }
}