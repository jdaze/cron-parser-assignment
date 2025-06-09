import dev.jdsoft.cronparser.CronParser;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class CronParserTest {

    private final CronParser cronParser = new CronParser();

    @Test
    void shouldReturnAllCronUnitValues() throws Exception {
        var expr = cronParser.parse("* * * * * /path/to/command");
        assertEquals(IntStream.rangeClosed(0, 59).boxed().collect(Collectors.toList()),
                expr.getMinutes());
        assertEquals("/path/to/command", expr.getCommand());
    }

    @Test
    void shouldParseCronBasedOnStep() throws Exception {
        var expr = cronParser.parse("*/15 0/6 1/15 0/2 0 /run/task");
        assertEquals(List.of(0, 15, 30, 45), expr.getMinutes());
        assertEquals(List.of(0, 6, 12, 18), expr.getHours());
        assertEquals(List.of(1, 16, 31), expr.getDaysOfMonth());
        assertEquals(List.of(2, 4, 6, 8, 10, 12), expr.getMonth());
        assertEquals(List.of(0), expr.getDaysOfWeek());
        assertEquals("/run/task", expr.getCommand());
    }

    @Test
    void shouldParseCronBasedOnRangeAndText() throws Exception {
        var expr = cronParser.parse("0-2 0-6 1-8 JAN-JUN SUN /run/task");
        assertEquals(List.of(0, 1, 2), expr.getMinutes());
        assertEquals(List.of(0, 1, 2, 3, 4, 5, 6), expr.getHours());
        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8), expr.getDaysOfMonth());
        assertEquals(List.of(1, 2, 3, 4, 5, 6), expr.getMonth());
        assertEquals(List.of(0), expr.getDaysOfWeek());
        assertEquals("/run/task", expr.getCommand());
    }

    @Test
    void shouldParseSundayAs7() throws Exception {
        var expr = cronParser.parse("*/15 0-6 1,15 1-6 7 /run/task");
        assertEquals(List.of(0, 15, 30, 45), expr.getMinutes());
        assertEquals(List.of(0, 1, 2, 3, 4, 5, 6), expr.getHours());
        assertEquals(List.of(1, 15), expr.getDaysOfMonth());
        assertEquals(List.of(1, 2, 3, 4, 5, 6), expr.getMonth());
        assertEquals(List.of(0), expr.getDaysOfWeek());
        assertEquals("/run/task", expr.getCommand());
    }

    @Test
    void shouldParseSingleValues() throws Exception {
        var expr = cronParser.parse("5 12 10 6 3 /do/something");
        assertEquals(List.of(5), expr.getMinutes());
        assertEquals(List.of(12), expr.getHours());
        assertEquals(List.of(10), expr.getDaysOfMonth());
        assertEquals(List.of(6), expr.getMonth());
        assertEquals(List.of(3), expr.getDaysOfWeek());
        assertEquals("/do/something", expr.getCommand());
    }

    @Test
    void shouldParseSingleValuesWithText() throws Exception {
        var expr = cronParser.parse("5 12 10 JUN WED /do/something");
        assertEquals(List.of(5), expr.getMinutes());
        assertEquals(List.of(12), expr.getHours());
        assertEquals(List.of(10), expr.getDaysOfMonth());
        assertEquals(List.of(6), expr.getMonth());
        assertEquals(List.of(3), expr.getDaysOfWeek());
        assertEquals("/do/something", expr.getCommand());
    }

    @Test
    void shouldParseCommaSeparatedValues() throws Exception {
        var expr = cronParser.parse("0,30 8,18 1,15 5,11 1,5 /usr/bin/job");
        assertEquals(List.of(0, 30), expr.getMinutes());
        assertEquals(List.of(8, 18), expr.getHours());
        assertEquals(List.of(1, 15), expr.getDaysOfMonth());
        assertEquals(List.of(5, 11), expr.getMonth());
        assertEquals(List.of(1, 5), expr.getDaysOfWeek());
        assertEquals("/usr/bin/job", expr.getCommand());
    }

    @Test
    void shouldParseCommaSeparatedValuesWithText() throws Exception {
        var expr = cronParser.parse("0,30 8,18 1,15 MAY,NOV MON,FRI /usr/bin/job");
        assertEquals(List.of(0, 30), expr.getMinutes());
        assertEquals(List.of(8, 18), expr.getHours());
        assertEquals(List.of(1, 15), expr.getDaysOfMonth());
        assertEquals(List.of(5, 11), expr.getMonth());
        assertEquals(List.of(1, 5), expr.getDaysOfWeek());
        assertEquals("/usr/bin/job", expr.getCommand());
    }

    @Test
    void shouldThrowInvalidCronExpressionOnInvalidAmountOfArguments() {
        var exception = assertThrows(Exception.class, () -> cronParser.parse("* * * * /invalid/command"));
        assertTrue(exception.getMessage().contains("Invalid cron expression"));
    }

    @Test
    void shouldThrowExceptionBecauseValuesOutOfPossibleRange() {
        var exception = assertThrows(Exception.class, () -> cronParser.parse("0-61 8,18 1,15 5,11 1,5 /usr/bin/job"));
        assertTrue(exception.getMessage().contains("Invalid values found"));
    }
}
