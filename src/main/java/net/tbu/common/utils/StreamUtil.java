package net.tbu.common.utils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public final class StreamUtil {

    private StreamUtil() {
        throw new IllegalAccessError("StreamUtil is utility class");
    }

    public static <T> BigDecimal reduceWith(List<T> list,
                                            Function<T, BigDecimal> reduceField) {
        return list.stream()
                .map(reduceField)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.valueOf(0.0d), BigDecimal::add);
    }

    public static <T> BigDecimal reduceWith(List<T> list,
                                            Predicate<T> filter,
                                            Function<T, BigDecimal> reduceField) {
        return list.stream()
                .filter(filter)
                .map(reduceField)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.valueOf(0.0d), BigDecimal::add);
    }


}
