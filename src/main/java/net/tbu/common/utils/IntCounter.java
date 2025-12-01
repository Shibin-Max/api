package net.tbu.common.utils;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public final class IntCounter {

    private int count;

    public IntCounter() {
        this(0);
    }

    public IntCounter(int initialValue) {
        this.count = initialValue;
    }

    public int incrementAndGet() {
        return ++count;
    }

    public int count() {
        return count;
    }

}
