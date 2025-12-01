package net.tbu.spi.service;


import com.baomidou.mybatisplus.extension.service.IService;
import net.tbu.spi.entity.TReconciliationRule;

import java.util.List;

/**
 * <p>
 * 对账规则表 服务类
 * </p>
 *
 * @author hao.yu
 * @since 2024-12-24
 */
public interface ITReconciliationRuleService extends IService<TReconciliationRule> {

    List<TReconciliationRule> selectList(Integer platformVersionType);

}
