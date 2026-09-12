package com.tradepilot.market.history;

import com.tradepilot.market.entity.Instrument;
import com.tradepilot.market.entity.MarketCandle;
import com.tradepilot.market.entity.Timeframe;
import com.tradepilot.market.repository.InstrumentRepository;
import com.tradepilot.market.repository.MarketCandleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HistoricalMarketDataImportService {

    private final HistoricalMarketDataProvider provider;

    private final InstrumentRepository
            instrumentRepository;

    private final MarketCandleRepository
            candleRepository;

    @Transactional
    public HistoricalImportResponse importCsv(
            Long instrumentId,
            Timeframe timeframe,
            MultipartFile file
    ) {

        Instrument instrument =
                instrumentRepository
                        .findById(instrumentId)
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "Instrument not found: "
                                                        + instrumentId
                                        )
                        );

        HistoricalCsvParser.ParseResult parseResult =
                provider.parse(file);

        List<HistoricalCandleRow> rows =
                parseResult.rows()
                        .stream()
                        .sorted(
                                Comparator.comparing(
                                        HistoricalCandleRow::timestamp
                                )
                        )
                        .toList();

        if (rows.isEmpty()) {

            return new HistoricalImportResponse(
                    instrumentId,
                    instrument.getSymbol(),
                    timeframe,
                    parseResult.totalRows(),
                    0,
                    0,
                    parseResult.totalRows(),
                    null,
                    null,
                    parseResult.errors()
            );
        }

        OffsetDateTime firstTimestamp =
                rows.get(0).timestamp();

        OffsetDateTime lastTimestamp =
                rows.get(
                        rows.size() - 1
                ).timestamp();

        /*
         * Load already-existing timestamps using
         * a single DB query.
         */
        Set<OffsetDateTime> existingTimestamps =
                new HashSet<>(
                        candleRepository
                                .findExistingTimestamps(
                                        instrumentId,
                                        timeframe,
                                        firstTimestamp,
                                        lastTimestamp
                                )
                );

        /*
         * Also prevents duplicate timestamps inside
         * the uploaded CSV itself.
         */
        Set<OffsetDateTime> seen =
                new HashSet<>();

        List<MarketCandle> toInsert =
                new ArrayList<>();

        int duplicateRows = 0;

        for (HistoricalCandleRow row : rows) {

            if (!seen.add(
                    row.timestamp()
            )) {

                duplicateRows++;
                continue;
            }

            if (existingTimestamps.contains(
                    row.timestamp()
            )) {

                duplicateRows++;
                continue;
            }

            MarketCandle candle =
                    new MarketCandle();

            candle.setInstrument(
                    instrument
            );

            candle.setTimeframe(
                    timeframe
            );

            candle.setCandleTimestamp(
                    row.timestamp()
            );

            candle.setOpenPrice(
                    row.open()
            );

            candle.setHighPrice(
                    row.high()
            );

            candle.setLowPrice(
                    row.low()
            );

            candle.setClosePrice(
                    row.close()
            );

            candle.setVolume(
                    row.volume()
            );

            toInsert.add(candle);
        }

        /*
         * JPA will persist the candles.
         *
         * We'll optimize JDBC batching separately
         * once correctness is verified.
         */
        candleRepository.saveAll(
                toInsert
        );

        log.info(
                "Historical import completed. symbol={}, timeframe={}, imported={}, duplicates={}, invalid={}",
                instrument.getSymbol(),
                timeframe,
                toInsert.size(),
                duplicateRows,
                parseResult.errors().size()
        );

        int invalidRows =
                parseResult.totalRows()
                        - rows.size();

        return new HistoricalImportResponse(
                instrumentId,
                instrument.getSymbol(),
                timeframe,

                parseResult.totalRows(),

                toInsert.size(),

                duplicateRows,

                invalidRows,

                firstTimestamp,
                lastTimestamp,

                parseResult.errors()
        );
    }
}