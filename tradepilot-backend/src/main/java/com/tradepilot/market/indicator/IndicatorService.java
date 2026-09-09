package com.tradepilot.market.indicator;

import com.tradepilot.market.entity.MarketCandle;
import com.tradepilot.market.entity.Timeframe;
import com.tradepilot.market.repository.MarketCandleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IndicatorService {

    private final MarketCandleRepository marketCandleRepository;
    private final EmaCalculator emaCalculator;
    private final RsiCalculator rsiCalculator;
    private final VwapCalculator vwapCalculator;
    private final MacdCalculator macdCalculator;
    private final AtrCalculator atrCalculator;

    @Transactional(readOnly = true)
    public IndicatorSnapshot calculate(
            Long instrumentId,
            Timeframe timeframe
    ) {

        List<MarketCandle> candles =
                marketCandleRepository
                        .findTop100ByInstrumentIdAndTimeframeOrderByCandleTimestampDesc(
                                instrumentId,
                                timeframe
                        );

        if (candles.isEmpty()) {
            throw new IllegalArgumentException(
                    "No candles found for instrument: " + instrumentId
            );
        }

        Collections.reverse(candles);

        List<BigDecimal> closePrices = new ArrayList<>();

        for (MarketCandle candle : candles) {
            closePrices.add(candle.getClosePrice());
        }

        BigDecimal ema9 =
                emaCalculator.calculate(closePrices, 9);

        BigDecimal ema21 =
                emaCalculator.calculate(closePrices, 21);

        BigDecimal rsi14 =
                rsiCalculator.calculate(closePrices, 14);

        BigDecimal vwap =
                vwapCalculator.calculate(
                        candles
                );
        MacdResult macdResult =
                macdCalculator.calculate(
                        closePrices
                );
        BigDecimal atr14 =
                atrCalculator.calculate(
                        candles,
                        14
                );

        MarketCandle latest =
                candles.get(candles.size() - 1);

        BigDecimal macd = null;
        BigDecimal macdSignal = null;
        BigDecimal macdHistogram = null;

        if (macdResult != null) {
            macd = macdResult.macd();
            macdSignal = macdResult.signal();
            macdHistogram = macdResult.histogram();
        }
        return new IndicatorSnapshot(
                latest.getInstrument().getId(),
                latest.getInstrument().getSymbol(),
                timeframe,
                latest.getCandleTimestamp(),
                latest.getClosePrice(),
                ema9,
                ema21,
                rsi14,
                vwap,
                macd,
                macdSignal,
                macdHistogram,
                atr14


        );
    }
}