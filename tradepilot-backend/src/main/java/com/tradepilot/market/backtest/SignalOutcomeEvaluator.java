package com.tradepilot.market.backtest;

import com.tradepilot.market.entity.MarketCandle;
import com.tradepilot.market.entity.Timeframe;
import com.tradepilot.market.entity.TradingSignalEntity;
import com.tradepilot.market.repository.MarketCandleRepository;
import com.tradepilot.market.repository.TradingSignalRepository;
import com.tradepilot.market.signal.SignalEvaluationStatus;
import com.tradepilot.market.signal.SignalOutcome;
import com.tradepilot.market.signal.SignalType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignalOutcomeEvaluator {

    private static final int EVALUATION_HORIZON_BARS = 12;

    private final TradingSignalRepository signalRepository;
    private final MarketCandleRepository candleRepository;

    @Transactional
    public void evaluatePendingSignals(
            Long instrumentId,
            Timeframe timeframe
    ) {

        List<TradingSignalEntity> pendingSignals =
                signalRepository
                        .findByInstrumentIdAndTimeframeAndEvaluationStatusOrderByCandleTimestampAsc(
                                instrumentId,
                                timeframe,
                                SignalEvaluationStatus.PENDING
                        );

        for (TradingSignalEntity signal : pendingSignals) {

            evaluateSignal(signal);
        }
    }

    private void evaluateSignal(
            TradingSignalEntity signal
    ) {

        /*
         * IMPORTANT:
         * Only candles AFTER the signal candle are used.
         */
        List<MarketCandle> futureCandles =
                candleRepository
                        .findByInstrumentIdAndTimeframeAndCandleTimestampGreaterThanOrderByCandleTimestampAsc(
                                signal.getInstrumentId(),
                                signal.getTimeframe(),
                                signal.getCandleTimestamp(),
                                PageRequest.of(
                                        0,
                                        EVALUATION_HORIZON_BARS
                                )
                        );

        if (futureCandles.isEmpty()) {
            return;
        }

        int barsHeld = 0;

        for (MarketCandle candle : futureCandles) {

            barsHeld++;

            EvaluationResult result =
                    evaluateCandle(
                            signal,
                            candle
                    );

            if (result != null) {

                complete(
                        signal,
                        result.outcome(),
                        result.exitPrice(),
                        candle.getCandleTimestamp(),
                        barsHeld
                );

                return;
            }
        }

        /*
         * Don't expire the trade until the full
         * evaluation horizon has actually arrived.
         */
        if (futureCandles.size()
                >= EVALUATION_HORIZON_BARS) {

            MarketCandle lastCandle =
                    futureCandles.get(
                            futureCandles.size() - 1
                    );

            complete(
                    signal,
                    SignalOutcome.EXPIRED,
                    lastCandle.getClosePrice(),
                    lastCandle.getCandleTimestamp(),
                    EVALUATION_HORIZON_BARS
            );
        }
    }

    private EvaluationResult evaluateCandle(
            TradingSignalEntity signal,
            MarketCandle candle
    ) {

        if (signal.getSignal() == SignalType.BUY) {

            return evaluateBuy(
                    signal,
                    candle
            );
        }

        if (signal.getSignal() == SignalType.SELL) {

            return evaluateSell(
                    signal,
                    candle
            );
        }

        return null;
    }

    private EvaluationResult evaluateBuy(
            TradingSignalEntity signal,
            MarketCandle candle
    ) {

        BigDecimal target =
                signal.getTargetPrice();

        BigDecimal stopLoss =
                signal.getStopLoss();

        boolean targetHit =
                candle.getHighPrice()
                        .compareTo(target) >= 0;

        boolean stopHit =
                candle.getLowPrice()
                        .compareTo(stopLoss) <= 0;

        /*
         * With OHLC data we don't know whether the
         * high or low occurred first inside the candle.
         *
         * Conservative backtest assumption:
         * if target AND SL were hit in the same candle,
         * count it as LOSS.
         */
        if (targetHit && stopHit) {

            return new EvaluationResult(
                    SignalOutcome.LOSS,
                    stopLoss
            );
        }

        if (stopHit) {

            return new EvaluationResult(
                    SignalOutcome.LOSS,
                    stopLoss
            );
        }

        if (targetHit) {

            return new EvaluationResult(
                    SignalOutcome.WIN,
                    target
            );
        }

        return null;
    }

    private EvaluationResult evaluateSell(
            TradingSignalEntity signal,
            MarketCandle candle
    ) {

        BigDecimal target =
                signal.getTargetPrice();

        BigDecimal stopLoss =
                signal.getStopLoss();

        boolean targetHit =
                candle.getLowPrice()
                        .compareTo(target) <= 0;

        boolean stopHit =
                candle.getHighPrice()
                        .compareTo(stopLoss) >= 0;

        if (targetHit && stopHit) {

            return new EvaluationResult(
                    SignalOutcome.LOSS,
                    stopLoss
            );
        }

        if (stopHit) {

            return new EvaluationResult(
                    SignalOutcome.LOSS,
                    stopLoss
            );
        }

        if (targetHit) {

            return new EvaluationResult(
                    SignalOutcome.WIN,
                    target
            );
        }

        return null;
    }

    private void complete(
            TradingSignalEntity signal,
            SignalOutcome outcome,
            BigDecimal exitPrice,
            OffsetDateTime exitTimestamp,
            int barsHeld
    ) {

        signal.setOutcome(outcome);

        signal.setExitPrice(exitPrice);

        signal.setExitCandleTimestamp(
                exitTimestamp
        );

        signal.setBarsHeld(
                barsHeld
        );

        signal.setEvaluationStatus(
                SignalEvaluationStatus.COMPLETED
        );

        /*
         * We use the market candle timestamp here
         * because that's when the outcome became known.
         */
        signal.setEvaluatedAt(
                exitTimestamp
        );

        signalRepository.save(signal);

        log.info(
                "Signal evaluated: symbol={}, signal={}, outcome={}, entry={}, exit={}, barsHeld={}",
                signal.getSymbol(),
                signal.getSignal(),
                outcome,
                signal.getEntryPrice(),
                exitPrice,
                barsHeld
        );
    }

    private record EvaluationResult(
            SignalOutcome outcome,
            BigDecimal exitPrice
    ) {
    }
}