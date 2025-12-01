package net.tbu.spi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.tbu.spi.entity.TReconciliationRule;
import net.tbu.spi.mapper.TReconciliationRuleMapper;
import net.tbu.spi.service.ITReconciliationRuleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 对账规则表 服务实现类
 * </p>
 *
 * @author hao.yu
 * @since 2024-12-24
 */
@Service
public class TReconciliationRuleServiceImpl extends ServiceImpl<TReconciliationRuleMapper, TReconciliationRule>
        implements ITReconciliationRuleService {

    @Override
    public List<TReconciliationRule> selectList(Integer platformVersionType) {
        LambdaQueryWrapper<TReconciliationRule> query = new QueryWrapper<TReconciliationRule>()
                .lambda()
                .eq(TReconciliationRule::getPlatformVersionType, platformVersionType);
        //.eq(TReconciliationRule::getRuleStatus, StatusEnum.ENABLE.getEventId());
        return baseMapper.selectList(query);
    }
}
