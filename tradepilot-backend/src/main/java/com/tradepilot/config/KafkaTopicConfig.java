package com.tradepilot.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    public static final String MARKET_CANDLES_TOPIC = "market.candles";
    public static final String MARKET_SIGNALS_TOPIC = "market.signals";
    @Bean
    public NewTopic marketCandlesTopic() {
        return new NewTopic(
                MARKET_CANDLES_TOPIC,
                1,
                (short) 1
        );
    }

    @Bean
    public NewTopic marketSignalsTopic() {
        return new NewTopic(
                MARKET_SIGNALS_TOPIC,
                1,
                (short) 1
        );
    }
}