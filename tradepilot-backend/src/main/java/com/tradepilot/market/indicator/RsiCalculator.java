package com.tradepilot.market.indicator;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

@Component
public class RsiCalculator {

    private static final MathContext MC =
            new MathContext(12, RoundingMode.HALF_UP);

    private static final BigDecimal ONE_HUNDRED =
            new BigDecimal("100");

    public BigDecimal calculate(
            List<BigDecimal> prices,
            int period
    ) {

        if (prices == null || prices.size() <= period) {
            return null;
        }

        BigDecimal gainSum = BigDecimal.ZERO;
        BigDecimal lossSum = BigDecimal.ZERO;

        // Initial average gain/loss
        for (int i = 1; i <= period; i++) {

            BigDecimal change =
                    prices.get(i)
                            .subtract(prices.get(i - 1), MC);

            if (change.signum() > 0) {
                gainSum = gainSum.add(change, MC);
            } else {
                lossSum = lossSum.add(change.abs(), MC);
            }
        }

        BigDecimal periodValue =
                BigDecimal.valueOf(period);

        BigDecimal averageGain =
                gainSum.divide(periodValue, MC);

        BigDecimal averageLoss =
                lossSum.divide(periodValue, MC);

        // Wilder smoothing
        for (int i = period + 1; i < prices.size(); i++) {

            BigDecimal change =
                    prices.get(i)
                            .subtract(prices.get(i - 1), MC);

            BigDecimal gain =
                    change.signum() > 0
                            ? change
                            : BigDecimal.ZERO;

            BigDecimal loss =
                    change.signum() < 0
                            ? change.abs()
                            : BigDecimal.ZERO;

            averageGain =
                    averageGain
                            .multiply(
                                    BigDecimal.valueOf(period - 1L),
                                    MC
                            )
                            .add(gain, MC)
                            .divide(periodValue, MC);

            averageLoss =
                    averageLoss
                            .multiply(
                                    BigDecimal.valueOf(period - 1L),
                                    MC
                            )
                            .add(loss, MC)
                            .divide(periodValue, MC);
        }

        if (averageLoss.compareTo(BigDecimal.ZERO) == 0) {
            return ONE_HUNDRED.setScale(2);
        }

        BigDecimal relativeStrength =
                averageGain.divide(averageLoss, MC);

        BigDecimal rsi =
                ONE_HUNDRED.subtract(
                        ONE_HUNDRED.divide(
                                BigDecimal.ONE.add(
                                        relativeStrength,
                                        MC
                                ),
                                MC
                        ),
                        MC
                );

        return rsi.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }
}