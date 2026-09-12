package com.tradepilot.market.history;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class HistoricalCsvParser {

    private static final ZoneId MARKET_ZONE =
            ZoneId.of("Asia/Kolkata");

    private static final DateTimeFormatter LOCAL_FORMAT =
            DateTimeFormatter.ofPattern(
                    "yyyy-MM-dd HH:mm:ss"
            );

    public ParseResult parse(
            MultipartFile file
    ) {

        List<HistoricalCandleRow> rows =
                new ArrayList<>();

        List<String> errors =
                new ArrayList<>();

        int totalRows = 0;

        try (
                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        file.getInputStream(),
                                        StandardCharsets.UTF_8
                                )
                        )
        ) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {

                lineNumber++;

                if (line.isBlank()) {
                    continue;
                }

                /*
                 * Skip header
                 */
                if (lineNumber == 1
                        && line.toLowerCase()
                        .contains("timestamp")) {

                    continue;
                }

                totalRows++;

                try {

                    HistoricalCandleRow row =
                            parseLine(line);

                    validate(row);

                    rows.add(row);

                } catch (Exception ex) {

                    /*
                     * Don't return thousands of errors
                     * in the REST response.
                     */
                    if (errors.size() < 20) {

                        errors.add(
                                "Line "
                                        + lineNumber
                                        + ": "
                                        + ex.getMessage()
                        );
                    }
                }
            }

        } catch (IOException ex) {

            throw new IllegalStateException(
                    "Unable to read historical CSV file",
                    ex
            );
        }

        return new ParseResult(
                totalRows,
                rows,
                errors
        );
    }

    private HistoricalCandleRow parseLine(
            String line
    ) {

        String[] values =
                line.split(",");

        if (values.length < 6) {

            throw new IllegalArgumentException(
                    "Expected 6 CSV columns"
            );
        }

        return new HistoricalCandleRow(

                parseTimestamp(
                        values[0].trim()
                ),

                new BigDecimal(
                        values[1].trim()
                ),

                new BigDecimal(
                        values[2].trim()
                ),

                new BigDecimal(
                        values[3].trim()
                ),

                new BigDecimal(
                        values[4].trim()
                ),

                Long.parseLong(
                        values[5].trim()
                )
        );
    }

    private OffsetDateTime parseTimestamp(
            String value
    ) {

        /*
         * First try ISO:
         *
         * 2025-01-02T09:15:00+05:30
         */
        try {

            return OffsetDateTime.parse(value);

        } catch (Exception ignored) {
        }

        /*
         * Then:
         *
         * 2025-01-02 09:15:00
         */
        LocalDateTime localDateTime =
                LocalDateTime.parse(
                        value,
                        LOCAL_FORMAT
                );

        return localDateTime
                .atZone(MARKET_ZONE)
                .toOffsetDateTime();
    }

    private void validate(
            HistoricalCandleRow row
    ) {

        if (row.open().signum() <= 0
                || row.high().signum() <= 0
                || row.low().signum() <= 0
                || row.close().signum() <= 0) {

            throw new IllegalArgumentException(
                    "OHLC prices must be positive"
            );
        }

        if (row.volume() < 0) {

            throw new IllegalArgumentException(
                    "Volume cannot be negative"
            );
        }

        if (row.high().compareTo(
                row.low()
        ) < 0) {

            throw new IllegalArgumentException(
                    "High cannot be below low"
            );
        }

        if (row.open().compareTo(
                row.high()
        ) > 0
                || row.open().compareTo(
                row.low()
        ) < 0) {

            throw new IllegalArgumentException(
                    "Open must be between low and high"
            );
        }

        if (row.close().compareTo(
                row.high()
        ) > 0
                || row.close().compareTo(
                row.low()
        ) < 0) {

            throw new IllegalArgumentException(
                    "Close must be between low and high"
            );
        }
    }

    public record ParseResult(

            int totalRows,

            List<HistoricalCandleRow> rows,

            List<String> errors

    ) {
    }
}