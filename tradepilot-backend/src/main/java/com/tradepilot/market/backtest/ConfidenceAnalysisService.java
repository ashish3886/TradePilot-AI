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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConfidenceAnalysisService {

    private final TradingSignalRepository repository;

    @Transactional(readOnly = true)
    public List<ConfidencePerformanceResponse> analyze(
            Long instrumentId,
            Timeframe timeframe
    ) {

        List<TradingSignalEntity> completed =
                repository
                        .findByInstrumentIdAndTimeframeAndEvaluationStatusOrderByCandleTimestampAsc(
                                instrumentId,
                                timeframe,
                                SignalEvaluationStatus.COMPLETED
                        );

        Map<BigDecimal, List<TradingSignalEntity>> grouped =
                completed.stream()
                        .collect(
                                Collectors.groupingBy(
                                        trade ->
                                                trade.getConfidence()
                                                        .stripTrailingZeros()
                                )
                        );

        List<ConfidencePerformanceResponse> result =
                new ArrayList<>();

        grouped.forEach(
                (confidence, trades) ->
                        result.add(
                                calculateBucket(
                                        confidence,
                                        trades
                                )
                        )
        );

        result.sort(
                Comparator.comparing(
                        ConfidencePerformanceResponse::confidence
                )
        );

        return result;
    }

    private ConfidencePerformanceResponse calculateBucket(
            BigDecimal confidence,
            List<TradingSignalEntity> trades
    ) {

        long completedTrades =
                trades.size();

        long wins = 0;
        long losses = 0;
        long expired = 0;

        long buyTrades = 0;
        long buyWins = 0;
        long buyLosses = 0;

        long sellTrades = 0;
        long sellWins = 0;
        long sellLosses = 0;

        BigDecimal grossProfit =
                BigDecimal.ZERO;

        BigDecimal grossLoss =
                BigDecimal.ZERO;

        for (TradingSignalEntity trade : trades) {

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

            if (trade.getSignal()
                    == SignalType.BUY) {

                buyTrades++;

                if (trade.getOutcome()
                        == SignalOutcome.WIN) {

                    buyWins++;

                } else if (trade.getOutcome()
                        == SignalOutcome.LOSS) {

                    buyLosses++;
                }
            }

            if (trade.getSignal()
                    == SignalType.SELL) {

                sellTrades++;

                if (trade.getOutcome()
                        == SignalOutcome.WIN) {

                    sellWins++;

                } else if (trade.getOutcome()
                        == SignalOutcome.LOSS) {

                    sellLosses++;
                }
            }

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

        BigDecimal averagePnl =
                BigDecimal.ZERO;

        if (completedTrades > 0) {

            averagePnl =
                    netPnl.divide(
                            BigDecimal.valueOf(
                                    completedTrades
                            ),
                            4,
                            RoundingMode.HALF_UP
                    );
        }

        return new ConfidencePerformanceResponse(
                confidence.setScale(
                        2,
                        RoundingMode.HALF_UP
                ),

                completedTrades,
                wins,
                losses,
                expired,

                winRate,

                scale(grossProfit),
                scale(grossLoss),
                scale(netPnl),

                profitFactor,
                averagePnl,

                buyTrades,
                buyWins,
                buyWinRate,

                sellTrades,
                sellWins,
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