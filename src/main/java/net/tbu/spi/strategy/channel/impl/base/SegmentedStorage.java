package net.tbu.spi.strategy.channel.impl.base;

import lombok.extern.slf4j.Slf4j;
import net.tbu.spi.strategy.channel.dto.LobbyOrder;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.dto.pp.PpGameRound;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.map.mutable.primitive.LongObjectHashMap;

import javax.annotation.Nullable;
import java.time.Duration;

@Slf4j
public final class SegmentedStorage<T extends LobbyOrder> {

    private static final int SEGMENT_COUNT = 4;

    private final MutableList<MutableLongObjectMap<T>> bucket = new FastList<>(SEGMENT_COUNT);

    private SegmentedStorage() {
        for (var i = 0; i < SEGMENT_COUNT; i++) {
            // 0x200_0000 == 33_554_432
            bucket.set(i, new LongObjectHashMap<>(0x200_0000));
        }
    }

    private void put(MutableList<T> orders) {
//        for (var order : orders) {
//            var bucketNum = (order.getSettledTime().getHour() + 8) % SEGMENT_COUNT;
//            bucket.get(bucketNum).put(order.getOrderRef(), order);
//        }
    }

    @Nullable
    private PpGameRound get(long id) {
//        for (var i = 0; i < SEGMENT_COUNT; i++) {
//            var gameRound = bucket.get(i).get(id);
//            if (gameRound != null) return gameRound;
//        }
        return null;
    }

    private void clear() {
        bucket.each(MutableLongObjectMap::clear);
    }


    private MutableList<PpGameRound> selectBy(TimeRangeParam param) {
        log.info("SegmentedStorage selectBy: {}", param);
        Duration duration = param.duration();
        if (duration.toHours() > 1) {
            log.error("SegmentedStorage selectBy duration: {}", duration);
            throw new IllegalArgumentException("Time " + param + " is not supported");
        }
        return null;
    }


}
