package tarde;

import com.tradepilot.market.indicator.RsiCalculator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RsiCalculatorTest {

    private final RsiCalculator calculator =
            new RsiCalculator();

    @Test
    void shouldReturn100WhenPricesOnlyIncrease() {

        List<BigDecimal> prices =
                List.of(
                        bd("100"), bd("101"), bd("102"),
                        bd("103"), bd("104"), bd("105"),
                        bd("106"), bd("107"), bd("108"),
                        bd("109"), bd("110"), bd("111"),
                        bd("112"), bd("113"), bd("114")
                );

        BigDecimal rsi =
                calculator.calculate(prices, 14);

        assertEquals(
                0,
                new BigDecimal("100.00").compareTo(rsi)
        );
    }

    @Test
    void shouldReturnNullWhenInsufficientData() {

        List<BigDecimal> prices =
                List.of(
                        bd("100"),
                        bd("101"),
                        bd("102")
                );

        BigDecimal rsi =
                calculator.calculate(prices, 14);

        assertNull(rsi);
    }

    @Test
    void shouldRemainBetweenZeroAndHundred() {

        List<BigDecimal> prices =
                List.of(
                        bd("100"), bd("102"), bd("101"),
                        bd("104"), bd("103"), bd("105"),
                        bd("102"), bd("106"), bd("108"),
                        bd("107"), bd("110"), bd("109"),
                        bd("111"), bd("108"), bd("112"),
                        bd("113"), bd("110"), bd("114")
                );

        BigDecimal rsi =
                calculator.calculate(prices, 14);

        assertNotNull(rsi);
        assertTrue(rsi.compareTo(BigDecimal.ZERO) >= 0);
        assertTrue(rsi.compareTo(new BigDecimal("100")) <= 0);
    }

    private BigDecimal bd(String value) {
        return new BigDecimal(value);
    }
}