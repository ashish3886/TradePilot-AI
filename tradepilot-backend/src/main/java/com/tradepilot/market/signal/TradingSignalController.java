 package com.tradepilot.market.signal;

import com.tradepilot.market.entity.Timeframe;
import com.tradepilot.market.signal.TradingSignal;
import com.tradepilot.market.signal.TradingSignalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/market/signals")
@RequiredArgsConstructor
public class TradingSignalController {

    private final TradingSignalService tradingSignalService;

    @GetMapping
    public TradingSignal getSignal(
            @RequestParam Long instrumentId,
            @RequestParam Timeframe timeframe
    ) {

        return tradingSignalService.generate(
                instrumentId,
                timeframe
        );
    }
}