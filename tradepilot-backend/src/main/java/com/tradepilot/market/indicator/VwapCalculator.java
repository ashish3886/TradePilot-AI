package com.tradepilot.market.indicator;

import com.tradepilot.market.entity.MarketCandle;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

@Component
public class VwapCalculator {

    private static final MathContext MC =
            new MathContext(12, RoundingMode.HALF_UP);

    private static final BigDecimal THREE =
            new BigDecimal("3");

    public BigDecimal calculate(
            List<MarketCandle> candles
    ) {

        if (candles == null || candles.isEmpty()) {
            return null;
        }

        BigDecimal cumulativePriceVolume =
                BigDecimal.ZERO;

        BigDecimal cumulativeVolume =
                BigDecimal.ZERO;

        for (MarketCandle candle : candles) {

            BigDecimal volume =
                    BigDecimal.valueOf(
                            candle.getVolume()
                    );

            if (volume.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            BigDecimal typicalPrice =
                    candle.getHighPrice()
                            .add(candle.getLowPrice(), MC)
                            .add(candle.getClosePrice(), MC)
                            .divide(THREE, MC);

            BigDecimal priceVolume =
                    typicalPrice.multiply(
                            volume,
                            MC
                    );

            cumulativePriceVolume =
                    cumulativePriceVolume.add(
                            priceVolume,
                            MC
                    );

            cumulativeVolume =
                    cumulativeVolume.add(
                            volume,
                            MC
                    );
        }

        if (cumulativeVolume.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        return cumulativePriceVolume
                .divide(cumulativeVolume, MC)
                .setScale(
                        4,
                        RoundingMode.HALF_UP
                );
    }
}