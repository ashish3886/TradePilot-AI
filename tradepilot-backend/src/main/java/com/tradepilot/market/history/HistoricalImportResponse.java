package com.tradepilot.market.history;

import com.tradepilot.market.entity.Timeframe;

import java.time.OffsetDateTime;
import java.util.List;

public record HistoricalImportResponse(

        Long instrumentId,
        String symbol,
        Timeframe timeframe,

        int totalRows,
        int importedRows,
        int duplicateRows,
        int invalidRows,

        OffsetDateTime firstTimestamp,
        OffsetDateTime lastTimestamp,

        List<String> errors

) {
}