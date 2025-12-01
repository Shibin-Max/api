package net.tbu.spi.strategy.channel;


import net.tbu.common.enums.PlatformEnum;
import net.tbu.spi.entity.TReconciliationBatch;

/**
 * @author hao.yu
 */
public interface ReconciliationChannelApi {

    /**
     * 匹配厅方渠道来源类型
     */
    PlatformEnum getChannelType();

    /**
     * 方法执行
     */
    void execute(TReconciliationBatch batch) throws Exception;

}
