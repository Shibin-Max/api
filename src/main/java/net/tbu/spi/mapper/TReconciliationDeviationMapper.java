package net.tbu.spi.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.tbu.spi.entity.TReconciliationDeviation;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 对账差错表 Mapper 接口
 * </p>
 *
 * @author hao.yu
 * @since 2024-12-24
 */

@Mapper
public interface TReconciliationDeviationMapper extends BaseMapper<TReconciliationDeviation> {

}
