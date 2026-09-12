package com.tradepilot.market.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tradepilot.market.event.TradingSignalEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class TradingSignalCacheService {

    private static final String KEY_PREFIX =
            "market:signal:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void saveLatestSignal(
            TradingSignalEvent event
    ) {

        String key =
                KEY_PREFIX
                        + event.symbol()
                        + ":"
                        + event.timeframe()
                        + ":latest";

        try {

            String json =
                    objectMapper.writeValueAsString(
                            event
                    );

            redisTemplate
                    .opsForValue()
                    .set(
                            key,
                            json
                    );

        } catch (JacksonException e) {

            throw new IllegalStateException(
                    "Failed to serialize trading signal",
                    e
            );
        }
    }
}