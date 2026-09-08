package com.tradepilot.market.kafka;

import com.tradepilot.config.KafkaTopicConfig;
import com.tradepilot.market.event.MarketCandleEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarketCandleProducer {

    private final KafkaTemplate<String, MarketCandleEvent> kafkaTemplate;

    public void publish(MarketCandleEvent event) {

        String key = event.symbol();

        kafkaTemplate.send(
                KafkaTopicConfig.MARKET_CANDLES_TOPIC,
                key,
                event
        );
    }
}