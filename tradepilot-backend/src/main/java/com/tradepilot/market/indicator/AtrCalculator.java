package com.tradepilot.market.indicator;

import com.tradepilot.market.entity.MarketCandle;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
public class AtrCalculator {

    private static final MathContext MC =
            new MathContext(12, RoundingMode.HALF_UP);

    public BigDecimal calculate(
            List<MarketCandle> candles,
            int period
    ) {

        if (candles == null
                || candles.size() <= period) {
            return null;
        }

        List<BigDecimal> trueRanges =
                new ArrayList<>();

        for (int i = 1; i < candles.size(); i++) {

            MarketCandle current =
                    candles.get(i);

            MarketCandle previous =
                    candles.get(i - 1);

            BigDecimal highLow =
                    current.getHighPrice()
                            .subtract(
                                    current.getLowPrice(),
                                    MC
                            );

            BigDecimal highPreviousClose =
                    current.getHighPrice()
                            .subtract(
                                    previous.getClosePrice(),
                                    MC
                            )
                            .abs();

            BigDecimal lowPreviousClose =
                    current.getLowPrice()
                            .subtract(
                                    previous.getClosePrice(),
                                    MC
                            )
                            .abs();

            BigDecimal trueRange =
                    max(
                            highLow,
                            highPreviousClose,
                            lowPreviousClose
                    );

            trueRanges.add(trueRange);
        }

        if (trueRanges.size() < period) {
            return null;
        }

        /*
         * First ATR = SMA of the first "period"
         * true-range values.
         */
        BigDecimal sum =
                BigDecimal.ZERO;

        for (int i = 0; i < period; i++) {

            sum = sum.add(
                    trueRanges.get(i),
                    MC
            );
        }

        BigDecimal atr =
                sum.divide(
                        BigDecimal.valueOf(period),
                        MC
                );

        /*
         * Wilder smoothing:
         *
         * ATR =
         * ((Previous ATR × (period - 1)) + Current TR)
         * / period
         */
        for (int i = period;
             i < trueRanges.size();
             i++) {

            atr =
                    atr.multiply(
                                    BigDecimal.valueOf(
                                            period - 1L
                                    ),
                                    MC
                            )
                            .add(
                                    trueRanges.get(i),
                                    MC
                            )
                            .divide(
                                    BigDecimal.valueOf(period),
                                    MC
                            );
        }

        return atr.setScale(
                4,
                RoundingMode.HALF_UP
        );
    }

    private BigDecimal max(
            BigDecimal first,
            BigDecimal second,
            BigDecimal third
    ) {

        return first
                .max(second)
                .max(third);
    }
}