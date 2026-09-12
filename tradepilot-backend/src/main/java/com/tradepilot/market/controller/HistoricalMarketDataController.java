package com.tradepilot.market.controller;

import com.tradepilot.market.entity.Timeframe;

import com.tradepilot.market.history.HistoricalImportResponse;
import com.tradepilot.market.history.HistoricalMarketDataImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(
        "/api/v1/market/historical"
)
@RequiredArgsConstructor
public class HistoricalMarketDataController {

    private final HistoricalMarketDataImportService
            importService;

    @PostMapping(
            value = "/import",
            consumes =
                    MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public HistoricalImportResponse importHistoricalData(

            @RequestParam Long instrumentId,

            @RequestParam Timeframe timeframe,

            @RequestPart("file")
            MultipartFile file
    ) {

        return importService.importCsv(
                instrumentId,
                timeframe,
                file
        );
    }
}