package com.tradepilot.market.signal;

import com.tradepilot.market.entity.TradingSignalEntity;
import com.tradepilot.market.event.TradingSignalEvent;
import com.tradepilot.market.repository.TradingSignalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradingSignalPersistenceService {

    private final TradingSignalRepository repository;

    @Transactional
    public void save(
            TradingSignalEvent event
    ) {

        boolean exists =
                repository
                        .existsByInstrumentIdAndTimeframeAndCandleTimestamp(
                                event.instrumentId(),
                                event.timeframe(),
                                event.candleTimestamp()
                        );

        if (exists) {

            log.debug(
                    "Duplicate trading signal ignored. instrumentId={}, timeframe={}, timestamp={}",
                    event.instrumentId(),
                    event.timeframe(),
                    event.candleTimestamp()
            );

            return;
        }

        TradingSignalEntity entity =
                new TradingSignalEntity();

        entity.setInstrumentId(
                event.instrumentId()
        );

        entity.setSymbol(
                event.symbol()
        );

        entity.setTimeframe(
                event.timeframe()
        );

        entity.setCandleTimestamp(
                event.candleTimestamp()
        );

        entity.setSignal(
                event.signal()
        );

        entity.setBullishScore(
                event.bullishScore()
        );

        entity.setBearishScore(
                event.bearishScore()
        );

        entity.setConfidence(
                event.confidence()
        );

        entity.setEntryPrice(
                event.entry()
        );

        entity.setStopLoss(
                event.stopLoss()
        );

        entity.setTargetPrice(
                event.target()
        );

        entity.setRiskRewardRatio(
                event.riskRewardRatio()
        );

        entity.setReasons(
                String.join(
                        " | ",
                        event.reasons()
                )
        );

        if (event.signal() == SignalType.HOLD) {

            entity.setEvaluationStatus(
                    SignalEvaluationStatus.SKIPPED
            );

        } else {

            entity.setEvaluationStatus(
                    SignalEvaluationStatus.PENDING
            );
        }

        repository.save(entity);

        log.info(
                "Trading signal stored: symbol={}, signal={}, timestamp={}",
                event.symbol(),
                event.signal(),
                event.candleTimestamp()
        );
    }
}