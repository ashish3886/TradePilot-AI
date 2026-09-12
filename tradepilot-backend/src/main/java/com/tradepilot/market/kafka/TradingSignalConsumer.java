package com.tradepilot.market.kafka;

import com.tradepilot.config.KafkaTopicConfig;
import com.tradepilot.market.cache.TradingSignalCacheService;
import com.tradepilot.market.event.TradingSignalEvent;
import com.tradepilot.market.signal.TradingSignalPersistenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TradingSignalConsumer {

    private final TradingSignalCacheService cacheService;

    private final TradingSignalPersistenceService
            persistenceService;

    @KafkaListener(
            topics = KafkaTopicConfig.MARKET_SIGNALS_TOPIC,
            groupId = "tradepilot-signal-store"
    )
    public void consume(
            TradingSignalEvent event
    ) {

        log.info(
                "Received trading signal: symbol={}, signal={}, confidence={}",
                event.symbol(),
                event.signal(),
                event.confidence()
        );

        persistenceService.save(event);

        cacheService.saveLatestSignal(event);
    }
}