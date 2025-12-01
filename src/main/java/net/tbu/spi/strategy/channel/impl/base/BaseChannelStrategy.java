package net.tbu.spi.strategy.channel.impl.base;

import net.tbu.spi.entity.TReconciliationBatch;

import javax.annotation.concurrent.ThreadSafe;

/**
 * 对账主逻辑扩展基类
 *
 * @author peng.jin
 */
@ThreadSafe
public abstract non-sealed class BaseChannelStrategy extends BaseChannelStrategyMainProcess {

    @Override
    protected String getChannelName() {
        return getChannelType().getPlatformName();
    }

    /**
     * 预先检查, 由子类实现具体的检查内容
     * 返回[true]  : 检查通过
     * 返回[false] : 检查不通过
     *
     * @return boolean
     */
    @Override
    protected boolean preCheck() {
        return true;
    }

    @Override
    protected boolean hasSpecialHandle() {
        return false;
    }

    @Override
    protected void doSpecialHandle(TReconciliationBatch batch) {
        throw new UnsupportedOperationException(channelName + " unsupported special handle");
    }

}
