package net.tbu.spi.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.tbu.spi.entity.TReconciliationBatchLog;
import net.tbu.spi.mapper.TReconciliationBatchLogMapper;
import net.tbu.spi.service.ITReconciliationBatchLogService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 对账复核数据发送邮件记录表 服务实现类
 * </p>
 *
 * @author intech
 * @since 2025-05-26
 */
@Service
public class TReconciliationBatchLogServiceImpl extends ServiceImpl<TReconciliationBatchLogMapper, TReconciliationBatchLog>
        implements ITReconciliationBatchLogService {

}
