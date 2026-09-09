package tarde;

import com.tradepilot.market.indicator.MacdCalculator;
import com.tradepilot.market.indicator.MacdResult;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MacdCalculatorTest {

    private final MacdCalculator calculator =
            new MacdCalculator();

    @Test
    void shouldCalculateMacd() {

        List<BigDecimal> prices =
                new ArrayList<>();

        for (int i = 1; i <= 50; i++) {
            prices.add(
                    BigDecimal.valueOf(
                            100 + i
                    )
            );
        }

        MacdResult result =
                calculator.calculate(
                        prices
                );

        assertNotNull(result);
        assertNotNull(result.macd());
        assertNotNull(result.signal());
        assertNotNull(result.histogram());
    }

    @Test
    void shouldReturnNullWhenHistoryIsInsufficient() {

        List<BigDecimal> prices =
                List.of(
                        new BigDecimal("100"),
                        new BigDecimal("101"),
                        new BigDecimal("102")
                );

        MacdResult result =
                calculator.calculate(
                        prices
                );

        assertNull(result);
    }
}