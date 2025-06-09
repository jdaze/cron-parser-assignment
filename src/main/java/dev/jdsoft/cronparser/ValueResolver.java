package dev.jdsoft.cronparser;

import java.util.List;

@FunctionalInterface
public interface ValueResolver {
    List<Integer> resolve(List<Integer> possibleValues, String expression) throws Exception;
}