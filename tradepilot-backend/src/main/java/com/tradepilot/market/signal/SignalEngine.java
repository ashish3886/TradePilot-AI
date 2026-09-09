package com.tradepilot.market.signal;

import com.tradepilot.market.indicator.IndicatorSnapshot;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
public class SignalEngine {

    private static final MathContext MC =
            new MathContext(12, RoundingMode.HALF_UP);

    private static final BigDecimal RSI_BULLISH =
            new BigDecimal("55");

    private static final BigDecimal RSI_BEARISH =
            new BigDecimal("45");

    private static final BigDecimal STOP_ATR_MULTIPLIER =
            new BigDecimal("1.5");

    private static final BigDecimal TARGET_ATR_MULTIPLIER =
            new BigDecimal("2.0");

    public TradingSignal generate(
            IndicatorSnapshot snapshot
    ) {

        validate(snapshot);

        int bullishScore = 0;
        int bearishScore = 0;

        List<String> reasons =
                new ArrayList<>();

        // EMA trend
        if (snapshot.ema9()
                .compareTo(snapshot.ema21()) > 0) {

            bullishScore++;
            reasons.add("EMA9 above EMA21");

        } else if (snapshot.ema9()
                .compareTo(snapshot.ema21()) < 0) {

            bearishScore++;
            reasons.add("EMA9 below EMA21");
        }

        // VWAP
        if (snapshot.closePrice()
                .compareTo(snapshot.vwap()) > 0) {

            bullishScore++;
            reasons.add("Price above VWAP");

        } else if (snapshot.closePrice()
                .compareTo(snapshot.vwap()) < 0) {

            bearishScore++;
            reasons.add("Price below VWAP");
        }

        // RSI
        if (snapshot.rsi14()
                .compareTo(RSI_BULLISH) >= 0) {

            bullishScore++;
            reasons.add("RSI indicates bullish momentum");

        } else if (snapshot.rsi14()
                .compareTo(RSI_BEARISH) <= 0) {

            bearishScore++;
            reasons.add("RSI indicates bearish momentum");
        }

        // MACD
        if (snapshot.macd()
                .compareTo(snapshot.macdSignal()) > 0) {

            bullishScore++;
            reasons.add("MACD above signal line");

        } else if (snapshot.macd()
                .compareTo(snapshot.macdSignal()) < 0) {

            bearishScore++;
            reasons.add("MACD below signal line");
        }

        // Histogram
        if (snapshot.macdHistogram()
                .compareTo(BigDecimal.ZERO) > 0) {

            bullishScore++;
            reasons.add("MACD histogram positive");

        } else if (snapshot.macdHistogram()
                .compareTo(BigDecimal.ZERO) < 0) {

            bearishScore++;
            reasons.add("MACD histogram negative");
        }

        SignalType signal =
                determineSignal(
                        bullishScore,
                        bearishScore
                );

        BigDecimal confidence =
                calculateConfidence(
                        bullishScore,
                        bearishScore
                );

        return createTradingSignal(
                snapshot,
                signal,
                bullishScore,
                bearishScore,
                confidence,
                reasons
        );
    }

    private SignalType determineSignal(
            int bullishScore,
            int bearishScore
    ) {

        if (bullishScore >= 4
                && bullishScore > bearishScore) {

            return SignalType.BUY;
        }

        if (bearishScore >= 4
                && bearishScore > bullishScore) {

            return SignalType.SELL;
        }

        return SignalType.HOLD;
    }

    private BigDecimal calculateConfidence(
            int bullishScore,
            int bearishScore
    ) {

        int strongestScore =
                Math.max(
                        bullishScore,
                        bearishScore
                );

        return BigDecimal
                .valueOf(strongestScore)
                .divide(
                        BigDecimal.valueOf(5),
                        MC
                )
                .multiply(
                        new BigDecimal("100"),
                        MC
                )
                .setScale(
                        2,
                        RoundingMode.HALF_UP
                );
    }

    private TradingSignal createTradingSignal(
            IndicatorSnapshot snapshot,
            SignalType signal,
            int bullishScore,
            int bearishScore,
            BigDecimal confidence,
            List<String> reasons
    ) {

        BigDecimal entry =
                snapshot.closePrice();

        BigDecimal stopLoss = null;
        BigDecimal target = null;
        BigDecimal riskReward = null;

        if (signal == SignalType.BUY) {

            stopLoss =
                    entry.subtract(
                            snapshot.atr14()
                                    .multiply(
                                            STOP_ATR_MULTIPLIER,
                                            MC
                                    ),
                            MC
                    );

            target =
                    entry.add(
                            snapshot.atr14()
                                    .multiply(
                                            TARGET_ATR_MULTIPLIER,
                                            MC
                                    ),
                            MC
                    );

        } else if (signal == SignalType.SELL) {

            stopLoss =
                    entry.add(
                            snapshot.atr14()
                                    .multiply(
                                            STOP_ATR_MULTIPLIER,
                                            MC
                                    ),
                            MC
                    );

            target =
                    entry.subtract(
                            snapshot.atr14()
                                    .multiply(
                                            TARGET_ATR_MULTIPLIER,
                                            MC
                                    ),
                            MC
                    );
        }

        if (signal != SignalType.HOLD) {

            BigDecimal risk =
                    entry.subtract(stopLoss)
                            .abs();

            BigDecimal reward =
                    target.subtract(entry)
                            .abs();

            if (risk.compareTo(BigDecimal.ZERO) > 0) {

                riskReward =
                        reward.divide(
                                risk,
                                2,
                                RoundingMode.HALF_UP
                        );
            }
        }

        return new TradingSignal(
                snapshot.instrumentId(),
                snapshot.symbol(),
                snapshot.timeframe(),
                snapshot.candleTimestamp(),
                signal,
                bullishScore,
                bearishScore,
                confidence,
                scale(entry),
                scale(stopLoss),
                scale(target),
                riskReward,
                reasons
        );
    }

    private BigDecimal scale(
            BigDecimal value
    ) {

        if (value == null) {
            return null;
        }

        return value.setScale(
                4,
                RoundingMode.HALF_UP
        );
    }

    private void validate(
            IndicatorSnapshot snapshot
    ) {

        if (snapshot == null
                || snapshot.ema9() == null
                || snapshot.ema21() == null
                || snapshot.rsi14() == null
                || snapshot.vwap() == null
                || snapshot.macd() == null
                || snapshot.macdSignal() == null
                || snapshot.macdHistogram() == null
                || snapshot.atr14() == null) {

            throw new IllegalArgumentException(
                    "Insufficient indicator data to generate signal"
            );
        }
    }
}