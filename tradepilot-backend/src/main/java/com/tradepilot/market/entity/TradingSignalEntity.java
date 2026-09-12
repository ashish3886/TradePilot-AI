package com.tradepilot.market.entity;

import com.tradepilot.market.signal.SignalEvaluationStatus;
import com.tradepilot.market.signal.SignalOutcome;
import com.tradepilot.market.signal.SignalType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(
        name = "trading_signals",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_trading_signal",
                        columnNames = {
                                "instrument_id",
                                "timeframe",
                                "candle_timestamp"
                        }
                )
        }
)
@Getter
@Setter
public class TradingSignalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "instrument_id",
            nullable = false
    )
    private Long instrumentId;

    @Column(
            nullable = false,
            length = 50
    )
    private String symbol;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private Timeframe timeframe;

    @Column(
            name = "candle_timestamp",
            nullable = false
    )
    private OffsetDateTime candleTimestamp;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 10
    )
    private SignalType signal;

    @Column(
            name = "bullish_score",
            nullable = false
    )
    private Integer bullishScore;

    @Column(
            name = "bearish_score",
            nullable = false
    )
    private Integer bearishScore;

    @Column(
            nullable = false,
            precision = 6,
            scale = 2
    )
    private BigDecimal confidence;

    @Column(
            name = "entry_price",
            precision = 18,
            scale = 4
    )
    private BigDecimal entryPrice;

    @Column(
            name = "stop_loss",
            precision = 18,
            scale = 4
    )
    private BigDecimal stopLoss;

    @Column(
            name = "target_price",
            precision = 18,
            scale = 4
    )
    private BigDecimal targetPrice;

    @Column(
            name = "risk_reward_ratio",
            precision = 10,
            scale = 4
    )
    private BigDecimal riskRewardRatio;

    @Column(
            columnDefinition = "TEXT"
    )
    private String reasons;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "evaluation_status",
            nullable = false,
            length = 20
    )
    private SignalEvaluationStatus evaluationStatus;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private SignalOutcome outcome;

    @Column(
            name = "exit_price",
            precision = 18,
            scale = 4
    )
    private BigDecimal exitPrice;

    @Column(
            name = "exit_candle_timestamp"
    )
    private OffsetDateTime exitCandleTimestamp;

    @Column(
            name = "bars_held"
    )
    private Integer barsHeld;

    @Column(
            name = "evaluated_at"
    )
    private OffsetDateTime evaluatedAt;

    @Column(
            name = "created_at",
            nullable = false
    )
    private OffsetDateTime createdAt;

    @PrePersist
    public void prePersist() {

        if (evaluationStatus == null) {
            evaluationStatus =
                    SignalEvaluationStatus.PENDING;
        }

        if (createdAt == null) {
            createdAt =
                    OffsetDateTime.now();
        }
    }
}