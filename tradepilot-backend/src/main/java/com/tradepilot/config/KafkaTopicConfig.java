package com.tradepilot.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    public static final String MARKET_CANDLES_TOPIC = "market.candles";

    @Bean
    public NewTopic marketCandlesTopic() {
        return new NewTopic(
                MARKET_CANDLES_TOPIC,
                1,
                (short) 1
        );
    }
}