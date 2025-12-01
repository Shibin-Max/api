package net.tbu.spi.strategy.channel.impl;

import lombok.extern.slf4j.Slf4j;
import net.tbu.common.constants.ComConstant;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.enums.RequestTypeEnum;
import net.tbu.common.enums.TimeUnitTypeEnum;
import net.tbu.common.utils.JsonExecutors;
import net.tbu.common.utils.LocalDateTimeUtil;
import net.tbu.exception.CustomizeRuntimeException;
import net.tbu.feign.client.internal.ThirdPartyGatewayFeignService;
import net.tbu.spi.strategy.channel.dto.LobbyOrderResult;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.dto.eeze.EezeLobbyOrderDelegate;
import net.tbu.spi.strategy.channel.dto.eeze.EezeLobbyOrderReq;
import net.tbu.spi.strategy.channel.dto.eeze.EezeLobbyOrderResp;
import net.tbu.spi.strategy.channel.impl.base.BaseChannelStrategy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;


/**
 * eeze数据量7万，不用汇总接口，时间粒度直接按照天和半天
 * (已上线)
 */
@Slf4j
@Service
public class EEZEChannelStrategy extends BaseChannelStrategy {

    /**
     * feign执行工具
     */
    @Resource
    private ThirdPartyGatewayFeignService gatewayFeignService;

    @Override
    public PlatformEnum getChannelType() {
        return PlatformEnum.EEZE;
    }

    /**
     * 重写最小粒度
     */
    @Override
    protected TimeUnitTypeEnum getMaxQueryDetailTime() {
        return TimeUnitTypeEnum.HALF_DAY;
    }

    /**
     * 查询EEZE汇总厅的明细
     */
    @Override
    public LobbyOrderResult getOutOrders(TimeRangeParam param) {
        log.info("{} getOutOrders with time: {}", channelName, param);

        /// 创建最终返回的结果集
        var result = new LobbyOrderResult(param);

        ///创建调用游戏网关查询明细的req
        EezeLobbyOrderReq req = new EezeLobbyOrderReq();
        req.setReckonStartTime(LocalDateTimeUtil.convertDateToString(param.start().toLocalDateTime()));
        /// 因为是小于等于结束时间，所以减去1秒
        req.setReckonEndTime(LocalDateTimeUtil.convertDateToString(param.end().toLocalDateTime().minusSeconds(1)));
        req.setPlatformId(PlatformEnum.EEZE.getPlatformId());
        req.setUri(ComConstant.EEZE_LIST);
        req.setHttpMethod(RequestTypeEnum.POST.getDesc());
        log.info("[EEZEChannelStrategy.getOutOrders][step1]  [reckonStartTime({})] [reckonEndTime({})] [platformId({})] [uri({})] [httpMethod({})]",
                req.getReckonStartTime(),
                req.getReckonEndTime(),
                req.getPlatformId(),
                req.getUri(),
                req.getHttpMethod());
        //0定义最终返回的TPG明细集合
//        List<TPGRespDto> finalTPGRespList = new ArrayList<>();
        // 0-1计算总页数,从第一页开始,每次限制两万条
        int totalPages = 1;
        int limit = 20000;

        // 1使用do-while循环处理分页请求
        int pageNum = 1;
        while (pageNum <= totalPages) {
            req.setCurrent(pageNum);
            req.setSize(limit);
            EezeLobbyOrderResp response = Optional.of(req)
                    .map(gatewayFeignService::callGateway)
                    .map(s -> JsonExecutors.fromJson(s, EezeLobbyOrderResp.class))
                    .map(s -> {
                        if (s.getCode() != 0) {
                            throw new CustomizeRuntimeException(String.format("%s EEZEChannelStrategy getOutOrders method two response error %s %s", channelName, s.getCode(), s.getMsg()));
                        }
                        return s;
                    })
                    .orElse(new EezeLobbyOrderResp());
            totalPages = new BigDecimal(response.getData().getTotal())
                    .divide(new BigDecimal(limit), 0, RoundingMode.CEILING)
                    .intValue();
            log.info("[EEZEChannelStrategy.getOutOrders][step2] [channelName({})] [page({}/{})] 总共[totalSize({})]条",
                    channelName, pageNum, totalPages, response.getData().getSize());
            //2解析每页的数据, 把数据放到最终的List集合
            response.getData()
                    .getList()
                    .stream()
                    .filter(s -> s.getFlag() == 1)
                    .peek(s -> {
                        //g32的所有注单会同步到G01，在G32的主单号需要使用19开头的主单号查询，在G01是25开头的
                        //电子钱包 新字段为空，代理钱包新字段不为空
                        if (StringUtils.isNotBlank(s.getOrderNoG32()) && !"0".equals(s.getOrderNoG32())) {
                            s.setBillNo(s.getOrderNoG32());
                        }
                    })
                    .map(EezeLobbyOrderDelegate::new)
                    .forEach(result::putOrder);
            pageNum++;
        }
        log.info("[EEZEChannelStrategy.getOutOrders][step3] [channelName({})] [returnSize({})] [param({})]",
                channelName, result.size(), param);
        return result;
    }

}
