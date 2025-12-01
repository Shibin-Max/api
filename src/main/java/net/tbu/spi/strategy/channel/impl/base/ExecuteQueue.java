package net.tbu.spi.strategy.channel.impl.base;

import net.tbu.spi.strategy.channel.dto.TimeRangeParam;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.LinkedList;
import java.util.Queue;

@NotThreadSafe
public final class ExecuteQueue {

    private final Queue<TimeRangeParam> innerQueue = new LinkedList<>();

    private ExecuteQueue() {
    }

    public static ExecuteQueue newInstance() {
        return new ExecuteQueue();
    }

    public void offer(TimeRangeParam param) {
        innerQueue.offer(param);
    }

    public TimeRangeParam poll() {
        return innerQueue.poll();
    }

    public boolean notEmpty() {
        return !innerQueue.isEmpty();
    }

}