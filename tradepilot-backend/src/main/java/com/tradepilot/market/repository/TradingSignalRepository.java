package com.tradepilot.market.repository;

import com.tradepilot.market.entity.Timeframe;
import com.tradepilot.market.entity.TradingSignalEntity;
import com.tradepilot.market.signal.SignalEvaluationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface TradingSignalRepository
        extends JpaRepository<TradingSignalEntity, Long> {

    boolean existsByInstrumentIdAndTimeframeAndCandleTimestamp(
            Long instrumentId,
            Timeframe timeframe,
            OffsetDateTime candleTimestamp
    );

    List<TradingSignalEntity>
    findTop100ByInstrumentIdAndTimeframeOrderByCandleTimestampDesc(
            Long instrumentId,
            Timeframe timeframe
    );

    List<TradingSignalEntity>
    findByEvaluationStatus(
            SignalEvaluationStatus evaluationStatus
    );

    List<TradingSignalEntity>
    findByInstrumentIdAndTimeframeAndEvaluationStatusOrderByCandleTimestampAsc(
            Long instrumentId,
            Timeframe timeframe,
            SignalEvaluationStatus evaluationStatus
    );

    long countByInstrumentIdAndTimeframe(
            Long instrumentId,
            Timeframe timeframe
    );

    long countByInstrumentIdAndTimeframeAndEvaluationStatus(
            Long instrumentId,
            Timeframe timeframe,
            SignalEvaluationStatus evaluationStatus
    );
}