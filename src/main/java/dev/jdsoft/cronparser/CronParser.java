package dev.jdsoft.cronparser;

import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CronParser {

    private static final Map<String, String> WEEK_DAYS_MAP = Map.of(
            "SUN", "0",
            "MON", "1",
            "TUE", "2",
            "WED", "3",
            "THU", "4",
            "FRI", "5",
            "SAT", "6"
    );

    private static final Map<String, String> MONTHS_MAP = Map.ofEntries(
            Map.entry("JAN", "1"),
            Map.entry("FEB", "2"),
            Map.entry("MAR", "3"),
            Map.entry("APR", "4"),
            Map.entry("MAY", "5"),
            Map.entry("JUN", "6"),
            Map.entry("JUL", "7"),
            Map.entry("AUG", "8"),
            Map.entry("SEP", "9"),
            Map.entry("OCT", "10"),
            Map.entry("NOV", "11"),
            Map.entry("DEC", "12")
    );

    private static final List<Integer> MINUTES = IntStream.rangeClosed(0, 59)
            .boxed().collect(Collectors.toList());

    private static final List<Integer> HOURS = IntStream.rangeClosed(0, 23)
            .boxed().collect(Collectors.toList());

    private static final List<Integer> MONTHS = IntStream.rangeClosed(1, 12)
            .boxed().collect(Collectors.toList());

    private static final List<Integer> DAYS_OF_WEEK = IntStream.rangeClosed(0, 7)
            .boxed().collect(Collectors.toList()); // 0 or 7 = SUNDAY

    public CronExpression parse(String expression) throws Exception {
        var parts = expression.split(" ");
        if (parts.length != 6) {
            throw new Exception("Invalid cron expression");
        }
        var minutes = parseMinutes(parts[0]);
        var hours = parseHours(parts[1]);
        var month = parseMonth(parts[3]);
        var daysOfMonth = parseDaysOfMonth(parts[2], month);
        var daysOfWeek = parseDaysOfWeek(parts[4]);
        var command = parts[5];

        return new CronExpression(minutes, hours, daysOfMonth, month, daysOfWeek, command);
    }

    private List<Integer> parseMinutes(String part) throws Exception {
        var sign = CronSign.detect(part);
        return sign.resolve(MINUTES, part);
    }

    private List<Integer> parseHours(String part) throws Exception {
        var sign = CronSign.detect(part);
        return sign.resolve(HOURS, part);
    }

    private List<Integer> parseDaysOfMonth(String part, List<Integer> months) throws Exception {
        var sign = CronSign.detect(part);
        var maxDays = getMaxDaysFromMonths(months);
        var possibleValues = IntStream.rangeClosed(1, maxDays)
                .boxed().collect(Collectors.toList());
        return sign.resolve(possibleValues, part);
    }

    private List<Integer> parseMonth(String part) throws Exception {
        part = normalizeInput(part, MONTHS_MAP);
        var sign = CronSign.detect(part);
        return sign.resolve(MONTHS, part);
    }

    private List<Integer> parseDaysOfWeek(String part) throws Exception {
        part = normalizeInput(part, WEEK_DAYS_MAP);
        var sign = CronSign.detect(part);
        var values = sign.resolve(DAYS_OF_WEEK, part);
        return values.stream()
                .map(n -> n == 7 ? 0 : n)
                .distinct()
                .collect(Collectors.toList());
    }

    private int getMaxDaysFromMonths(List<Integer> months) {
        int currentYear = Year.now().getValue();

        return months.stream()
                .map(month -> YearMonth.of(currentYear, month).lengthOfMonth())
                .max(Integer::compareTo)
                .orElse(0); // in case the list is empty
    }

    private String normalizeInput(String part, Map<String, String> valuesMap) {
        String result = part;
        for (Map.Entry<String, String> entry : valuesMap.entrySet()) {
            result = result.replaceAll("\\b" + entry.getKey() + "\\b", entry.getValue());
        }
        return result;
    }
}
