package net.tbu.spi.strategy.channel.impl.base;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.tbu.common.enums.DeviationTypeEnum;
import net.tbu.spi.entity.TReconciliationDeviation;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import javax.annotation.Nullable;

@Slf4j
@Getter
public final class DeviationStorage {

    /// 本次任务全部的长款差异数据, 用于最终统计, 0x40_000 == 262144
    private final MutableMap<String, TReconciliationDeviation> laDeviations = new UnifiedMap<>(0x40_000);

    /// 本次任务全部的短款差异数据, 用于最终统计, 0x40_000 == 262144
    private final MutableMap<String, TReconciliationDeviation> saDeviations = new UnifiedMap<>(0x40_000);

    /// 本次任务全部的金额不等差异数据
    private final MutableMap<String, TReconciliationDeviation> adDeviations = new UnifiedMap<>(0x40_000);

    /// 本次任务全部的时间不等差异数据
    private final MutableMap<String, TReconciliationDeviation> btdDeviations = new UnifiedMap<>(0x40_000);

    public boolean hasLaDeviations() {
        return laDeviations.notEmpty();
    }

    public boolean hasSaDeviations() {
        return saDeviations.notEmpty();
    }

    public boolean hasAdDeviations() {
        return adDeviations.notEmpty();
    }

    public boolean hasBtdDeviations() {
        return btdDeviations.notEmpty();
    }

    /**
     * 依据索引取出长款数据
     *
     * @param orderRef String
     * @return TReconciliationDeviation
     */
    @Nullable
    public TReconciliationDeviation tryTakeLA(String orderRef) {
        return laDeviations.remove(orderRef);
    }

    /**
     * 依据索引取出短款数据
     *
     * @param orderRef String
     * @return TReconciliationDeviation
     */
    @Nullable
    public TReconciliationDeviation tryTakeSA(String orderRef) {
        return saDeviations.remove(orderRef);
    }

    /**
     * 添加数据到缓存中
     *
     * @param deviation TReconciliationDeviation
     */
    public void put(TReconciliationDeviation deviation) {
        switch (DeviationTypeEnum.getEnum(deviation.getDeviationType())) {
            case LA -> laDeviations.put(deviation.getInOrderRef(), deviation);
            case SA -> saDeviations.put(deviation.getOutOrderRef(), deviation);
            case AD -> adDeviations.put(deviation.getInOrderRef(), deviation);
            case BTD -> btdDeviations.put(deviation.getInOrderRef(), deviation);
            default ->
                    throw new IllegalStateException("DeviationStorage::put has unexpected value by [INVALID], deviation: " + deviation);
        }
    }

    /**
     * 检查指定数据在缓存中是否存在
     *
     * @param deviation TReconciliationDeviation
     * @return boolean
     */
    public boolean notContain(TReconciliationDeviation deviation) {
        return !switch (DeviationTypeEnum.getEnum(deviation.getDeviationType())) {
            case LA -> laDeviations.containsKey(deviation.getInOrderRef());
            case SA -> saDeviations.containsKey(deviation.getOutOrderRef());
            case AD -> adDeviations.containsKey(deviation.getInOrderRef());
            case BTD -> btdDeviations.containsKey(deviation.getInOrderRef());
            default ->
                    throw new IllegalStateException("DeviationStorage::isContain has unexpected value by [INVALID], deviation: " + deviation);
        };
    }

    /**
     * 当前缓存总量统计
     *
     * @return long
     */
    public long count() {
        return ((long) laCount()) + saCount() + adCount() + btdCount();
    }

    public int laCount() {
        return laDeviations.size();
    }

    public int saCount() {
        return saDeviations.size();
    }

    public int adCount() {
        return adDeviations.size();
    }

    public int btdCount() {
        return btdDeviations.size();
    }

}