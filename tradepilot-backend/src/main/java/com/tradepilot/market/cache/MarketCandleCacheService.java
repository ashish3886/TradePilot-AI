package com.tradepilot.market.cache;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.tradepilot.market.event.MarketCandleEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class MarketCandleCacheService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String KEY_PREFIX = "market:candle:";

    public void saveLatestCandle(MarketCandleEvent event) {

        String key = buildKey(event);

        try {
            String json = objectMapper.writeValueAsString(event);

            redisTemplate.opsForValue().set(
                    key,
                    json
            );

        } catch (JacksonException e) {
            throw new RuntimeException(
                    "Failed to serialize market candle for Redis",
                    e
            );
        }
    }

    private String buildKey(MarketCandleEvent event) {

        return KEY_PREFIX
                + event.symbol()
                + ":"
                + event.timeframe()
                + ":latest";
    }
}