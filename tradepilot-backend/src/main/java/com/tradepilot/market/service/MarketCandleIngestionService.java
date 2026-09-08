package com.tradepilot.market.service;

import com.tradepilot.market.cache.MarketCandleCacheService;
import com.tradepilot.market.entity.Instrument;
import com.tradepilot.market.entity.MarketCandle;
import com.tradepilot.market.event.MarketCandleEvent;
import com.tradepilot.market.repository.InstrumentRepository;
import com.tradepilot.market.repository.MarketCandleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarketCandleIngestionService {

    private final MarketCandleRepository marketCandleRepository;
    private final InstrumentRepository instrumentRepository;
    private final MarketCandleCacheService cacheService;

    @Transactional
    public void process(MarketCandleEvent event) {

        boolean exists =
                marketCandleRepository
                        .existsByInstrumentIdAndTimeframeAndCandleTimestamp(
                                event.instrumentId(),
                                event.timeframe(),
                                event.candleTimestamp()
                        );

        if (exists) {
            log.debug(
                    "Duplicate candle ignored. instrumentId={}, timeframe={}, timestamp={}",
                    event.instrumentId(),
                    event.timeframe(),
                    event.candleTimestamp()
            );

            return;
        }

        Instrument instrument =
                instrumentRepository.findById(event.instrumentId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Instrument not found: "
                                                + event.instrumentId()
                                )
                        );

        MarketCandle candle = new MarketCandle();

        candle.setInstrument(instrument);
        candle.setCandleTimestamp(event.candleTimestamp());
        candle.setTimeframe(event.timeframe());

        candle.setOpenPrice(event.openPrice());
        candle.setHighPrice(event.highPrice());
        candle.setLowPrice(event.lowPrice());
        candle.setClosePrice(event.closePrice());

        candle.setVolume(event.volume());

        marketCandleRepository.save(candle);

        cacheService.saveLatestCandle(event);

        log.info(
                "Market candle processed: symbol={}, timeframe={}, timestamp={}",
                event.symbol(),
                event.timeframe(),
                event.candleTimestamp()
        );
    }
}