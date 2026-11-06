package legends.ui;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class BarUtilsTest {

    private static long count(String bar, char c) {
        return bar.chars().filter(x -> x == c).count();
    }

    @ParameterizedTest
    @CsvSource({
            "0, 100, 10, 0",
            "50, 100, 10, 5",
            "100, 100, 10, 10",
            "33, 100, 10, 3",
            "36, 100, 10, 4",
            "150, 100, 10, 10",
            "-5, 100, 10, 0"
    })
    void fillMatchesTheClampedRatio(double current, double max, int length, int filled) {
        String bar = BarUtils.makeBar(current, max, length);
        assertEquals(filled, count(bar, '█'));
        assertEquals(length - filled, count(bar, '░'));
    }

    @Test
    void zeroMaxRendersAnEmptyBar() {
        String bar = BarUtils.makeBar(0, 0, 4);
        assertEquals(0, count(bar, '█'));
        assertEquals(4, count(bar, '░'));
    }
}
