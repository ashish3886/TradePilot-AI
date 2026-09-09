package com.tradepilot.market.indicator;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
public class MacdCalculator {

    private static final MathContext MC =
            new MathContext(12, RoundingMode.HALF_UP);

    public MacdResult calculate(
            List<BigDecimal> prices
    ) {

        int fastPeriod = 12;
        int slowPeriod = 26;
        int signalPeriod = 9;

        if (prices == null
                || prices.size() < slowPeriod + signalPeriod - 1) {
            return null;
        }

        List<BigDecimal> fastEmaSeries =
                calculateEmaSeries(
                        prices,
                        fastPeriod
                );

        List<BigDecimal> slowEmaSeries =
                calculateEmaSeries(
                        prices,
                        slowPeriod
                );

        List<BigDecimal> macdSeries =
                new ArrayList<>();

        /*
         * EMA-12 starts earlier than EMA-26.
         *
         * We align both series from the point
         * where EMA-26 first becomes available.
         */
        int offset =
                slowPeriod - fastPeriod;

        for (int i = 0; i < slowEmaSeries.size(); i++) {

            BigDecimal fastEma =
                    fastEmaSeries.get(
                            i + offset
                    );

            BigDecimal slowEma =
                    slowEmaSeries.get(i);

            BigDecimal macd =
                    fastEma.subtract(
                            slowEma,
                            MC
                    );

            macdSeries.add(macd);
        }

        if (macdSeries.size() < signalPeriod) {
            return null;
        }

        List<BigDecimal> signalSeries =
                calculateEmaSeries(
                        macdSeries,
                        signalPeriod
                );

        BigDecimal latestMacd =
                macdSeries.get(
                        macdSeries.size() - 1
                );

        BigDecimal latestSignal =
                signalSeries.get(
                        signalSeries.size() - 1
                );

        BigDecimal histogram =
                latestMacd.subtract(
                        latestSignal,
                        MC
                );

        return new MacdResult(
                scale(latestMacd),
                scale(latestSignal),
                scale(histogram)
        );
    }

    private List<BigDecimal> calculateEmaSeries(
            List<BigDecimal> prices,
            int period
    ) {

        List<BigDecimal> emaSeries =
                new ArrayList<>();

        if (prices.size() < period) {
            return emaSeries;
        }

        BigDecimal sum =
                BigDecimal.ZERO;

        for (int i = 0; i < period; i++) {
            sum = sum.add(
                    prices.get(i),
                    MC
            );
        }

        BigDecimal ema =
                sum.divide(
                        BigDecimal.valueOf(period),
                        MC
                );

        emaSeries.add(ema);

        BigDecimal multiplier =
                BigDecimal.valueOf(2)
                        .divide(
                                BigDecimal.valueOf(
                                        period + 1L
                                ),
                                MC
                        );

        for (int i = period;
             i < prices.size();
             i++) {

            BigDecimal price =
                    prices.get(i);

            ema = price
                    .subtract(ema, MC)
                    .multiply(multiplier, MC)
                    .add(ema, MC);

            emaSeries.add(ema);
        }

        return emaSeries;
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