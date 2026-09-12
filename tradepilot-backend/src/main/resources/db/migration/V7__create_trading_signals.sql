CREATE TABLE trading_signals (
    id BIGSERIAL PRIMARY KEY,

    instrument_id BIGINT NOT NULL,

    symbol VARCHAR(50) NOT NULL,

    timeframe VARCHAR(30) NOT NULL,

    candle_timestamp TIMESTAMPTZ NOT NULL,

    signal VARCHAR(10) NOT NULL,

    bullish_score INTEGER NOT NULL,
    bearish_score INTEGER NOT NULL,

    confidence NUMERIC(6, 2) NOT NULL,

    entry_price NUMERIC(18, 4),

    stop_loss NUMERIC(18, 4),

    target_price NUMERIC(18, 4),

    risk_reward_ratio NUMERIC(10, 4),

    reasons TEXT,

    evaluation_status VARCHAR(20)
        NOT NULL DEFAULT 'PENDING',

    outcome VARCHAR(20),

    exit_price NUMERIC(18, 4),

    evaluated_at TIMESTAMPTZ,

    created_at TIMESTAMPTZ
        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_trading_signal_instrument
        FOREIGN KEY (instrument_id)
        REFERENCES instruments(id),

    CONSTRAINT uk_trading_signal
        UNIQUE (
            instrument_id,
            timeframe,
            candle_timestamp
        )
);

CREATE INDEX idx_trading_signals_lookup
    ON trading_signals (
        instrument_id,
        timeframe,
        candle_timestamp DESC
    );

CREATE INDEX idx_trading_signals_pending
    ON trading_signals (
        evaluation_status
    );