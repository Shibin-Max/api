package net.tbu.spi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.tbu.common.enums.DeviationTypeEnum;
import net.tbu.spi.entity.TReconciliationDeviation;

import java.util.List;


/**
 * <p>
 * 对账差错表 服务类
 * </p>
 *
 * @author hao.yu
 * @since 2024-12-24
 */
public interface ITReconciliationDeviationService extends IService<TReconciliationDeviation> {

    /**
     * 根据批次号逻辑删除队长差错数据*
     *
     * @param batchNumber 批次号
     */
    void deleteByBatchNumber(String batchNumber);

    /**
     * 根据batch_number查询
     *
     * @param batchNumber String
     * @return List<TReconciliationDeviation>
     */
    List<TReconciliationDeviation> selectListBy(String batchNumber);

    /**
     * 检查指定batch_number和差异类型的总数
     *
     * @param batchNumber    String
     * @param deviationTypes DeviationTypeEnum[]
     * @return long
     */
    long countBy(String batchNumber, DeviationTypeEnum... deviationTypes);

    /**
     * 根据batch_number和差异类型查询数据
     *
     * @param batchNumber   String
     * @param deviationType DeviationTypeEnum
     * @return List<TReconciliationDeviation>
     */
    List<TReconciliationDeviation> selectListBy(String batchNumber, DeviationTypeEnum deviationType);

}
