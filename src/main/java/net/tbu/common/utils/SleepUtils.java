package net.tbu.common.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class SleepUtils {

    private SleepUtils() {
        throw new IllegalAccessError("SleepUtils is utility class");
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

}
