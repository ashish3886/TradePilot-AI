package com.tradepilot.market.signal;

import com.tradepilot.market.entity.Timeframe;
import com.tradepilot.market.indicator.IndicatorService;
import com.tradepilot.market.indicator.IndicatorSnapshot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TradingSignalService {

    private final IndicatorService indicatorService;
    private final SignalEngine signalEngine;

    public TradingSignal generate(
            Long instrumentId,
            Timeframe timeframe
    ) {

        IndicatorSnapshot indicators =
                indicatorService.calculate(
                        instrumentId,
                        timeframe
                );

        return signalEngine.generate(
                indicators
        );
    }
}