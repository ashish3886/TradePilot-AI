package com.tradepilot.market.kafka;

import com.tradepilot.config.KafkaTopicConfig;
import com.tradepilot.market.event.MarketCandleEvent;
import com.tradepilot.market.service.MarketCandleIngestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MarketCandleConsumer {

    private final MarketCandleIngestionService ingestionService;

    @KafkaListener(
            topics = KafkaTopicConfig.MARKET_CANDLES_TOPIC,
            groupId = "tradepilot-market-data"
    )
    public void consume(MarketCandleEvent event) {

        log.info(
                "Received market candle: symbol={}, timeframe={}, timestamp={}",
                event.symbol(),
                event.timeframe(),
                event.candleTimestamp()
        );

        ingestionService.process(event);
    }
}