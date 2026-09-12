package com.tradepilot.market.kafka;

import com.tradepilot.config.KafkaTopicConfig;
import com.tradepilot.market.backtest.SignalOutcomeEvaluator;
import com.tradepilot.market.event.MarketCandleEvent;
import com.tradepilot.market.service.MarketCandleIngestionService;
import com.tradepilot.market.signal.MarketSignalProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MarketCandleConsumer {

    private final MarketCandleIngestionService ingestionService;
    private final MarketSignalProcessor signalProcessor;
    private final SignalOutcomeEvaluator signalOutcomeEvaluator;

    @KafkaListener(
            topics = KafkaTopicConfig.MARKET_CANDLES_TOPIC,
            groupId = "tradepilot-market-data"
    )
    public void consume(
            MarketCandleEvent event
    ) {

        log.info(
                "Received market candle: symbol={}, timeframe={}, timestamp={}",
                event.symbol(),
                event.timeframe(),
                event.candleTimestamp()
        );

        ingestionService.process(event);

        /*
         * Evaluate EXISTING signals using the
         * newly persisted candle.
         */
        signalOutcomeEvaluator.evaluatePendingSignals(
                event.instrumentId(),
                event.timeframe()
        );

        /*
         * Generate a NEW signal for the
         * current candle.
         */
        signalProcessor.process(
                event.instrumentId(),
                event.timeframe()
        );
    }
}