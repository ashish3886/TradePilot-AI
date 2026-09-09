package tarde;

import com.tradepilot.market.entity.Timeframe;
import com.tradepilot.market.indicator.IndicatorSnapshot;
import com.tradepilot.market.signal.SignalEngine;
import com.tradepilot.market.signal.SignalType;
import com.tradepilot.market.signal.TradingSignal;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SignalEngineTest {

    private final SignalEngine signalEngine =
            new SignalEngine();

    @Test
    void shouldGenerateBuySignal() {

        IndicatorSnapshot snapshot =
                new IndicatorSnapshot(
                        1L,
                        "BANKNIFTY",
                        Timeframe.FIVE_MINUTES,
                        OffsetDateTime.now(),

                        bd("55000"),

                        bd("55100"),
                        bd("54900"),

                        bd("62"),

                        bd("54800"),

                        bd("100"),
                        bd("80"),
                        bd("20"),

                        bd("120")
                );

        TradingSignal signal =
                signalEngine.generate(snapshot);

        assertEquals(
                SignalType.BUY,
                signal.signal()
        );

        assertEquals(
                5,
                signal.bullishScore()
        );

        assertNotNull(signal.stopLoss());
        assertNotNull(signal.target());
    }

    private BigDecimal bd(
            String value
    ) {
        return new BigDecimal(value);
    }
}