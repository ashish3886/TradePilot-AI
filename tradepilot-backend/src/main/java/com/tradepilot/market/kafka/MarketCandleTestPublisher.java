/*
package com.tradepilot.market.kafka;

import com.tradepilot.market.entity.Timeframe;
import com.tradepilot.market.event.MarketCandleEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class MarketCandleTestPublisher implements CommandLineRunner {

    private final MarketCandleProducer producer;

    @Override
    public void run(String... args) {

        MarketCandleEvent event = new MarketCandleEvent(
                1L,
                "BANKNIFTY",
                OffsetDateTime.now(),
                Timeframe.FIVE_MINUTES,
                new BigDecimal("24950.0000"),
                new BigDecimal("25020.0000"),
                new BigDecimal("24920.0000"),
                new BigDecimal("25005.0000"),
                1250000L
        );

        producer.publish(event);

        System.out.println("Test market candle published to Kafka");
    }
}*/
