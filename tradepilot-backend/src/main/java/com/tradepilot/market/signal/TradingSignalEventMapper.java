package com.tradepilot.market.signal;

import com.tradepilot.market.event.TradingSignalEvent;
import org.springframework.stereotype.Component;

@Component
public class TradingSignalEventMapper {

    public TradingSignalEvent toEvent(
            TradingSignal signal
    ) {

        return new TradingSignalEvent(
                signal.instrumentId(),
                signal.symbol(),
                signal.timeframe(),
                signal.candleTimestamp(),

                signal.signal(),

                signal.bullishScore(),
                signal.bearishScore(),

                signal.confidence(),

                signal.entry(),
                signal.stopLoss(),
                signal.target(),
                signal.riskRewardRatio(),

                signal.reasons()
        );
    }
}