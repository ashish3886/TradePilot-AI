package com.tradepilot.market.controller;

import com.tradepilot.market.backtest.BacktestMetricsResponse;
import com.tradepilot.market.backtest.BacktestMetricsService;
import com.tradepilot.market.backtest.ConfidenceAnalysisService;
import com.tradepilot.market.backtest.ConfidencePerformanceResponse;
import com.tradepilot.market.entity.Timeframe;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/market/backtest")
@RequiredArgsConstructor
public class BacktestController {

    private final BacktestMetricsService
            backtestMetricsService;

    private final ConfidenceAnalysisService
            confidenceAnalysisService;

    @GetMapping("/metrics")
    public BacktestMetricsResponse metrics(
            @RequestParam Long instrumentId,
            @RequestParam Timeframe timeframe
    ) {

        return backtestMetricsService.calculate(
                instrumentId,
                timeframe
        );
    }

    @GetMapping("/confidence")
    public List<ConfidencePerformanceResponse>
    confidencePerformance(
            @RequestParam Long instrumentId,
            @RequestParam Timeframe timeframe
    ) {

        return confidenceAnalysisService.analyze(
                instrumentId,
                timeframe
        );
    }
}