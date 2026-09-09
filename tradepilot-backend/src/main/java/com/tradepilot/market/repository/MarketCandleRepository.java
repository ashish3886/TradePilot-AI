package com.tradepilot.market.repository;

import com.tradepilot.market.entity.MarketCandle;
import com.tradepilot.market.entity.Timeframe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface MarketCandleRepository
        extends JpaRepository<MarketCandle, Long> {

    List<MarketCandle> findByInstrumentIdAndTimeframeAndCandleTimestampBetweenOrderByCandleTimestampAsc(
            Long instrumentId,
            Timeframe timeframe,
            OffsetDateTime from,
            OffsetDateTime to
    );

    boolean existsByInstrumentIdAndTimeframeAndCandleTimestamp(
            Long instrumentId,
            Timeframe timeframe,
            OffsetDateTime candleTimestamp
    );

    List<MarketCandle>
    findTop100ByInstrumentIdAndTimeframeOrderByCandleTimestampDesc(
            Long instrumentId,
            Timeframe timeframe
    );
}