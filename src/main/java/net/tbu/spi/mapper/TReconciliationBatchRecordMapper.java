package net.tbu.spi.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.tbu.spi.entity.TReconciliationBatchRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 对账批次表 Mapper 接口
 * </p>
 *
 * @author hao.yu
 * @since 2024-12-24
 */

@Mapper
public interface TReconciliationBatchRecordMapper extends BaseMapper<TReconciliationBatchRecord> {

}
