package com.tradepilot.market.provide;

import com.tradepilot.market.entity.Timeframe;
import com.tradepilot.market.event.MarketCandleEvent;
import com.tradepilot.market.kafka.MarketCandleProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "tradepilot.market.mock.enabled",
        havingValue = "true"
)
@Slf4j
public class MockMarketDataProvider {

    private final MarketCandleProducer producer;

    private BigDecimal previousClose =
            new BigDecimal("55000.0000");

    @Scheduled(fixedRate = 5000)
    public void generateCandle() {

        BigDecimal open = previousClose;

        BigDecimal movement = BigDecimal.valueOf(
                ThreadLocalRandom.current().nextDouble(-100, 100)
        );

        BigDecimal close = open.add(movement);

        BigDecimal highMovement = BigDecimal.valueOf(
                ThreadLocalRandom.current().nextDouble(10, 80)
        );

        BigDecimal lowMovement = BigDecimal.valueOf(
                ThreadLocalRandom.current().nextDouble(10, 80)
        );

        BigDecimal high = open.max(close).add(highMovement);
        BigDecimal low = open.min(close).subtract(lowMovement);

        long volume =
                ThreadLocalRandom.current()
                        .nextLong(500_000, 2_000_000);

        open = scale(open);
        high = scale(high);
        low = scale(low);
        close = scale(close);

        MarketCandleEvent event =
                new MarketCandleEvent(
                        1L,
                        "BANKNIFTY",
                        OffsetDateTime.now(ZoneOffset.UTC),
                        Timeframe.FIVE_MINUTES,
                        open,
                        high,
                        low,
                        close,
                        volume
                );

        producer.publish(event);

        previousClose = close;

        log.info(
                "Mock candle published: symbol={}, open={}, high={}, low={}, close={}",
                event.symbol(),
                open,
                high,
                low,
                close
        );
    }

    private BigDecimal scale(BigDecimal value) {
        return value.setScale(
                4,
                RoundingMode.HALF_UP
        );
    }
}