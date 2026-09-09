package com.tradepilot.market.indicator;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

@Component
public class EmaCalculator {

    private static final MathContext MC =
            new MathContext(12, RoundingMode.HALF_UP);

    public BigDecimal calculate(
            List<BigDecimal> prices,
            int period
    ) {

        if (prices == null || prices.size() < period) {
            return null;
        }

        BigDecimal sma = prices
                .subList(0, period)
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(
                        BigDecimal.valueOf(period),
                        MC
                );

        BigDecimal multiplier =
                BigDecimal.valueOf(2)
                        .divide(
                                BigDecimal.valueOf(period + 1L),
                                MC
                        );

        BigDecimal ema = sma;

        for (int i = period; i < prices.size(); i++) {

            BigDecimal price = prices.get(i);

            ema = price
                    .subtract(ema, MC)
                    .multiply(multiplier, MC)
                    .add(ema, MC);
        }

        return ema.setScale(
                4,
                RoundingMode.HALF_UP
        );
    }
}