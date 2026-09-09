package tarde;

import com.tradepilot.market.entity.MarketCandle;
import com.tradepilot.market.indicator.VwapCalculator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VwapCalculatorTest {

    private final VwapCalculator calculator =
            new VwapCalculator();

    @Test
    void shouldCalculateVwap() {

        MarketCandle c1 =
                candle(
                        "100",
                        "110",
                        "90",
                        "105",
                        100L
                );

        MarketCandle c2 =
                candle(
                        "105",
                        "115",
                        "100",
                        "110",
                        200L
                );

        BigDecimal vwap =
                calculator.calculate(
                        List.of(c1, c2)
                );

        assertNotNull(vwap);

        assertTrue(
                vwap.compareTo(
                        BigDecimal.ZERO
                ) > 0
        );
    }

    @Test
    void shouldReturnNullForEmptyList() {

        BigDecimal vwap =
                calculator.calculate(
                        List.of()
                );

        assertNull(vwap);
    }

    private MarketCandle candle(
            String open,
            String high,
            String low,
            String close,
            Long volume
    ) {

        MarketCandle candle =
                new MarketCandle();

        candle.setOpenPrice(
                new BigDecimal(open)
        );

        candle.setHighPrice(
                new BigDecimal(high)
        );

        candle.setLowPrice(
                new BigDecimal(low)
        );

        candle.setClosePrice(
                new BigDecimal(close)
        );

        candle.setVolume(volume);

        return candle;
    }
}