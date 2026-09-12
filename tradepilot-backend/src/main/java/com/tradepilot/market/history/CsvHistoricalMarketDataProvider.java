package com.tradepilot.market.history;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class CsvHistoricalMarketDataProvider
        implements HistoricalMarketDataProvider {

    private final HistoricalCsvParser csvParser;

    @Override
    public HistoricalCsvParser.ParseResult parse(
            MultipartFile file
    ) {

        return csvParser.parse(file);
    }
}