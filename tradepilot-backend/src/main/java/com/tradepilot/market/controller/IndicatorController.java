package com.tradepilot.market.controller;

import com.tradepilot.market.entity.Timeframe;
import com.tradepilot.market.indicator.IndicatorService;
import com.tradepilot.market.indicator.IndicatorSnapshot;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/market/indicators")
@RequiredArgsConstructor
public class IndicatorController {

    private final IndicatorService indicatorService;

    @GetMapping
    public IndicatorSnapshot getIndicators(
            @RequestParam Long instrumentId,
            @RequestParam Timeframe timeframe
    ) {

        return indicatorService.calculate(
                instrumentId,
                timeframe
        );
    }
}