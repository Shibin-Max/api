package net.tbu.spi.mapper;

import net.tbu.spi.entity.TReconciliationBatchLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 对账复核数据发送邮件记录表 Mapper 接口
 * </p>
 *
 * @author intech
 * @since 2025-05-26
 */

@Mapper
public interface TReconciliationBatchLogMapper extends BaseMapper<TReconciliationBatchLog> {

}
