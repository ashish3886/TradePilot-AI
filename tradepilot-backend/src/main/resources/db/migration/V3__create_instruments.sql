CREATE TABLE instruments (
    id BIGSERIAL PRIMARY KEY,

    symbol VARCHAR(50) NOT NULL,

    name VARCHAR(100) NOT NULL,

    exchange VARCHAR(30) NOT NULL,

    instrument_type VARCHAR(20) NOT NULL,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_instrument_symbol
        UNIQUE (symbol)
);