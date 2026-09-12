package com.tradepilot.market.kafka;

import com.tradepilot.config.KafkaTopicConfig;
import com.tradepilot.market.event.TradingSignalEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradingSignalProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(
            TradingSignalEvent event
    ) {

        kafkaTemplate.send(
                KafkaTopicConfig.MARKET_SIGNALS_TOPIC,
                event.symbol(),
                event
        );

        log.info(
                "Trading signal published: symbol={}, signal={}, confidence={}",
                event.symbol(),
                event.signal(),
                event.confidence()
        );
    }
}