CREATE TABLE market_candles (
    id BIGSERIAL PRIMARY KEY,

    instrument_id BIGINT NOT NULL,

    candle_timestamp TIMESTAMPTZ NOT NULL,

    timeframe VARCHAR(10) NOT NULL,

    open_price NUMERIC(18, 4) NOT NULL,

    high_price NUMERIC(18, 4) NOT NULL,

    low_price NUMERIC(18, 4) NOT NULL,

    close_price NUMERIC(18, 4) NOT NULL,

    volume BIGINT NOT NULL DEFAULT 0,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_market_candle_instrument
        FOREIGN KEY (instrument_id)
        REFERENCES instruments(id),

    CONSTRAINT uk_market_candle
        UNIQUE (instrument_id, candle_timestamp, timeframe)
);

CREATE INDEX idx_market_candles_instrument_timestamp
    ON market_candles (instrument_id, candle_timestamp DESC);