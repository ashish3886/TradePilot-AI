package com.tradepilot.market.repository;

import com.tradepilot.market.entity.MarketCandle;
import com.tradepilot.market.entity.Timeframe;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    List<MarketCandle>
    findByInstrumentIdAndTimeframeAndCandleTimestampGreaterThanOrderByCandleTimestampAsc(
            Long instrumentId,
            Timeframe timeframe,
            OffsetDateTime candleTimestamp,
            Pageable pageable
    );

    @Query("""
       select c.candleTimestamp
       from MarketCandle c
       where c.instrument.id = :instrumentId
         and c.timeframe = :timeframe
         and c.candleTimestamp between :from and :to
       """)
    List<OffsetDateTime> findExistingTimestamps(
            @Param("instrumentId")
            Long instrumentId,

            @Param("timeframe")
            Timeframe timeframe,

            @Param("from")
            OffsetDateTime from,

            @Param("to")
            OffsetDateTime to
    );
}