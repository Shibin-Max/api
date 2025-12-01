package net.tbu.common.utils;

import cn.hutool.core.util.NumberUtil;

import java.math.BigDecimal;

import static java.lang.Boolean.TRUE;
import static java.util.Optional.ofNullable;

public final class PredicateUtil {

    private PredicateUtil() {
        throw new IllegalAccessError("PredicateUtil is utility class");
    }

    public static boolean isEqual(Boolean premise, Long long0, Long long1) {
        return ofNullable(premise).orElse(TRUE) && !NumberUtil.equals(long0, long1);
    }

    public static boolean isEqual(Boolean premise, BigDecimal decimal0, BigDecimal decimal1) {
        return ofNullable(premise).orElse(TRUE) && !NumberUtil.equals(decimal0, decimal1);
    }

}
