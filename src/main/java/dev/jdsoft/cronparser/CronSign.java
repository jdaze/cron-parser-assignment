package dev.jdsoft.cronparser;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@AllArgsConstructor
public enum CronSign {

    ALL('*', (possibleValues, _) -> possibleValues),
    SINGLE(((_, expression) -> List.of(Integer.parseInt(expression)))),
    RANGE('-', ((_, expression) -> {
        var values = expression.split("-");
        if (values.length != 2) {
            throw new Exception("Invalid cron expression.");
        }

        var from = Integer.parseInt(values[0]);
        var to = Integer.parseInt(values[1]);

        return IntStream.rangeClosed(from, to)
                .boxed().collect(Collectors.toList());
    })),
    CSV(',', ((_, expression) -> {
        var values = expression.split(",");
        if (values.length < 1) {
            throw new Exception("Invalid cron expression.");
        }
        return Arrays.stream(values).map(Integer::parseInt).collect(Collectors.toList());
    })),
    INCREMENTAL('/', ((possibleValues, expression) -> {
        var values = expression.split("/");
        if (values.length != 2) {
            throw new Exception("Invalid cron expression.");
        }

        int start = values[0].equals("*") ? possibleValues.getFirst() : Integer.parseInt(values[0]);
        int step = Integer.parseInt(values[1]);

        if (step <= 0) {
            throw new Exception("Step must be greater than 0.");
        }

        return possibleValues.stream()
                .filter(v -> v >= start && (v - start) % step == 0)
                .collect(Collectors.toList());
    }));

    private char character;
    private final ValueResolver resolver;

    CronSign(ValueResolver resolver) {
        this.resolver = resolver;
    }

    public List<Integer> resolve(List<Integer> possibleValues, String expression) throws Exception {
        var values = resolver.resolve(possibleValues, expression);
        validateIfOutsidePossibleValues(values, possibleValues);
        return values;
    }

    public static CronSign detect(String expression) {
        return Stream.of(
                        CronSign.INCREMENTAL,
                        CronSign.RANGE,
                        CronSign.CSV,
                        CronSign.ALL
                ).filter(sign -> expression.contains(String.valueOf(sign.character)))
                .findFirst()
                .orElse(CronSign.SINGLE);
    }

    private void validateIfOutsidePossibleValues(List<Integer> values, List<Integer> possibleValues) {
        List<Integer> invalidValues = values.stream()
                .filter(v -> !possibleValues.contains(v))
                .distinct()
                .toList();

        if (!invalidValues.isEmpty()) {
            throw new IllegalArgumentException("Invalid values found: " + invalidValues);
        }
    }
}
