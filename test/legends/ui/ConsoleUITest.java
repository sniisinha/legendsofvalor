package legends.ui;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleUITest {

    private static final String RED_X = ConsoleUI.RED + "x" + ConsoleUI.RESET;

    @Test
    void stripAnsiRemovesColorCodes() {
        assertEquals("x", ConsoleUI.stripAnsi(RED_X));
        assertEquals("x", ConsoleUI.stripAnsi("\u001B[1;31mx\u001B[0m"));
        assertEquals("plain", ConsoleUI.stripAnsi("plain"));
        assertEquals("", ConsoleUI.stripAnsi(null));
    }

    @Test
    void paddingFillsToWidth() {
        assertEquals("ab  ", ConsoleUI.padRight("ab", 4));
        assertEquals("  ab", ConsoleUI.padLeft("ab", 4));
        assertEquals(" ab ", ConsoleUI.padCenter("ab", 4));
        assertEquals(" ab  ", ConsoleUI.padCenter("ab", 5));
    }

    @Test
    void paddingIgnoresAnsiCodesWhenMeasuring() {
        assertEquals(RED_X + "  ", ConsoleUI.padRight(RED_X, 3));
        assertEquals("  " + RED_X, ConsoleUI.padLeft(RED_X, 3));
        assertEquals(" " + RED_X + " ", ConsoleUI.padCenter(RED_X, 3));
    }

    @Test
    void textWiderThanTheColumnIsLeftUntouched() {
        assertEquals("abcdef", ConsoleUI.padRight("abcdef", 3));
        assertEquals("abcdef", ConsoleUI.padLeft("abcdef", 3));
        assertEquals("abcdef", ConsoleUI.padCenter("abcdef", 3));
    }

    @Test
    void paddingTreatsNullAsEmpty() {
        assertEquals("  ", ConsoleUI.padRight(null, 2));
        assertEquals("  ", ConsoleUI.padLeft(null, 2));
        assertEquals("  ", ConsoleUI.padCenter(null, 2));
    }

    @Test
    void repeatHandlesEdgeCases() {
        assertEquals("ababab", ConsoleUI.repeat("ab", 3));
        assertEquals("", ConsoleUI.repeat("ab", 0));
        assertEquals("", ConsoleUI.repeat("ab", -1));
        assertEquals("", ConsoleUI.repeat(null, 3));
    }

    @Test
    void tableAlignsColumns() {
        List<String> out = ConsoleUI.table(Arrays.asList(
                new String[]{"Name", "HP"},
                new String[]{"Al", "100"}));
        assertEquals(Arrays.asList("Name  HP ", "Al    100"), out);
    }

    @Test
    void tableHandlesRaggedAndNullRows() {
        List<String> out = ConsoleUI.table(Arrays.asList(
                new String[]{"a", "b"},
                new String[]{"c"},
                null,
                new String[]{null, "d"}));
        assertEquals(Arrays.asList("a  b", "c   ", "    ", "   d"), out);
    }

    @Test
    void tableAlignsColoredCellsByVisibleWidth() {
        List<String> out = ConsoleUI.table(Arrays.asList(
                new String[]{RED_X, "1"},
                new String[]{"yy", "2"}));
        assertEquals("x   1", ConsoleUI.stripAnsi(out.get(0)));
    }

    @Test
    void emptyTablesProduceNoRows() {
        assertTrue(ConsoleUI.table(null).isEmpty());
        assertTrue(ConsoleUI.table(Collections.<String[]>emptyList()).isEmpty());
        assertTrue(ConsoleUI.table(Collections.singletonList(new String[0])).isEmpty());
    }

    @Test
    void boxedHandlesMissingContent() {
        assertDoesNotThrow(() -> ConsoleUI.boxed(null, null));
        assertDoesNotThrow(() -> ConsoleUI.boxed("Title", Arrays.asList("line", null, RED_X)));
    }
}
