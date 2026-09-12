package com.tradepilot.market.history;

import org.springframework.web.multipart.MultipartFile;

public interface HistoricalMarketDataProvider {

    HistoricalCsvParser.ParseResult parse(
            MultipartFile file
    );
}