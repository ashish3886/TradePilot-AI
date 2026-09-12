package com.tradepilot.market.signal;

import com.tradepilot.market.entity.Timeframe;
import com.tradepilot.market.event.TradingSignalEvent;
import com.tradepilot.market.kafka.TradingSignalProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarketSignalProcessor {

    private final TradingSignalService tradingSignalService;
    private final TradingSignalEventMapper eventMapper;
    private final TradingSignalProducer signalProducer;

    public void process(
            Long instrumentId,
            Timeframe timeframe
    ) {

        try {

            TradingSignal tradingSignal =
                    tradingSignalService.generate(
                            instrumentId,
                            timeframe
                    );

            TradingSignalEvent event =
                    eventMapper.toEvent(
                            tradingSignal
                    );

            signalProducer.publish(event);

        } catch (IllegalArgumentException ex) {

            /*
             * This can happen during startup while we
             * don't yet have enough candles for all
             * indicators.
             */

            log.debug(
                    "Signal not generated yet for instrumentId={}, timeframe={}: {}",
                    instrumentId,
                    timeframe,
                    ex.getMessage()
            );
        }
    }
}