package dev.jdsoft.cronparser;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class CronExpression {

    private List<Integer> minutes;
    private List<Integer> hours;
    private List<Integer> daysOfMonth;
    private List<Integer> month;
    private List<Integer> daysOfWeek;
    private String command;

    public void prettyPrint() {
        System.out.print(this);
    }

    @Override
    public String toString() {
        return formatLine("minute", minutes) +
                formatLine("hour", hours) +
                formatLine("day of month", daysOfMonth) +
                formatLine("month", month) +
                formatLine("day of week", daysOfWeek) +
                String.format("%-20s%s\n", "command", command != null ? command : "");
    }

    private String formatLine(String label, List<Integer> values) {
        String valueStr = values != null ? values.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(" ")) : "";
        return String.format("%-20s%s\n", label, valueStr);
    }
}
