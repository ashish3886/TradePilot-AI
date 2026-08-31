package com.tradepilot.market.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(
        name = "market_candles",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_market_candle",
                        columnNames = {
                                "instrument_id",
                                "candle_timestamp",
                                "timeframe"
                        }
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketCandle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "instrument_id",
            nullable = false
    )
    private Instrument instrument;

    @Column(
            name = "candle_timestamp",
            nullable = false
    )
    private OffsetDateTime candleTimestamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Timeframe timeframe;

    @Column(
            name = "open_price",
            nullable = false,
            precision = 18,
            scale = 4
    )
    private BigDecimal openPrice;

    @Column(
            name = "high_price",
            nullable = false,
            precision = 18,
            scale = 4
    )
    private BigDecimal highPrice;

    @Column(
            name = "low_price",
            nullable = false,
            precision = 18,
            scale = 4
    )
    private BigDecimal lowPrice;

    @Column(
            name = "close_price",
            nullable = false,
            precision = 18,
            scale = 4
    )
    private BigDecimal closePrice;

    @Column(nullable = false)
    private Long volume;

    @Column(
            name = "created_at",
            nullable = false
    )
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }
}