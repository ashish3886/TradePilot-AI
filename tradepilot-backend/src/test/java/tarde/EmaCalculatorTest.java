package tarde;

import com.tradepilot.market.indicator.EmaCalculator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmaCalculatorTest {

    private final EmaCalculator calculator =
            new EmaCalculator();

    @Test
    void shouldCalculateEma() {

        List<BigDecimal> prices =
                List.of(
                        new BigDecimal("10"),
                        new BigDecimal("11"),
                        new BigDecimal("12"),
                        new BigDecimal("13"),
                        new BigDecimal("14"),
                        new BigDecimal("15"),
                        new BigDecimal("16"),
                        new BigDecimal("17"),
                        new BigDecimal("18"),
                        new BigDecimal("19")
                );

        BigDecimal ema =
                calculator.calculate(
                        prices,
                        5
                );

        assertNotNull(ema);
        assertTrue(
                ema.compareTo(BigDecimal.ZERO) > 0
        );
    }

    @Test
    void shouldReturnNullWhenInsufficientData() {

        List<BigDecimal> prices =
                List.of(
                        new BigDecimal("10"),
                        new BigDecimal("11")
                );

        BigDecimal ema =
                calculator.calculate(
                        prices,
                        9
                );

        assertNull(ema);
    }
}