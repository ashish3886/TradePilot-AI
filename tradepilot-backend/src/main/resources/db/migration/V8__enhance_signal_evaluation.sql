ALTER TABLE trading_signals
ADD COLUMN exit_candle_timestamp TIMESTAMPTZ;

ALTER TABLE trading_signals
ADD COLUMN bars_held INTEGER;

UPDATE trading_signals
SET evaluation_status = 'SKIPPED'
WHERE signal = 'HOLD'
  AND evaluation_status = 'PENDING';