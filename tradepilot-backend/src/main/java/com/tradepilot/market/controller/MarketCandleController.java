package com.tradepilot.market.controller;

import com.tradepilot.market.dto.CreateMarketCandleRequest;
import com.tradepilot.market.dto.MarketCandleResponse;
import com.tradepilot.market.service.MarketCandleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/market/candles")
@RequiredArgsConstructor
public class MarketCandleController {

    private final MarketCandleService candleService;

    @PostMapping
    public MarketCandleResponse createCandle(
            @Valid @RequestBody CreateMarketCandleRequest request) {

        return candleService.createCandle(request);
    }

    @GetMapping
    public List<MarketCandleResponse> getCandles(

            @RequestParam Long instrumentId,

            @RequestParam String timeframe,

            @RequestParam OffsetDateTime from,

            @RequestParam OffsetDateTime to) {

        return candleService.getCandles(
                instrumentId,
                timeframe,
                from,
                to
        );
    }
}