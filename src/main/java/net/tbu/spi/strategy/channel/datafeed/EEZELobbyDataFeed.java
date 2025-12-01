package net.tbu.spi.strategy.channel.datafeed;

import lombok.extern.slf4j.Slf4j;
import net.tbu.common.enums.RequestTypeEnum;
import net.tbu.common.utils.JsonExecutors;
import net.tbu.exception.CustomizeRuntimeException;
import net.tbu.feign.client.internal.ThirdPartyGatewayFeignService;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.dto.eeze.EezeLobbyOrderReq;
import net.tbu.spi.strategy.channel.dto.eeze.EezeLobbyOrderResp;
import net.tbu.spi.strategy.channel.dto.eeze.EezeLobbyOrderResp.EezeLobbyOrder;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import static net.tbu.common.constants.ComConstant.EEZE_LIST;
import static net.tbu.common.enums.PlatformEnum.EEZE;
import static net.tbu.common.utils.LocalDateTimeUtil.convertDateToString;

@Slf4j
@Component
public class EEZELobbyDataFeed {

    @Resource
    private ThirdPartyGatewayFeignService feignService;

    /**
     * 获取订单的接口单独实现, 并将可见范围设置为包可见, 便于进行单元测试
     *
     * @param param TimeRangeParam
     * @return MutableList<EezeLobbyOrder>
     */
    public MutableList<EezeLobbyOrder> getLobbyOrders(TimeRangeParam param) {
        ///创建调用游戏网关查询明细的req
        var req = new EezeLobbyOrderReq();
        req.setReckonStartTime(convertDateToString(param.start().toLocalDateTime()));
        /// 因为是小于等于结束时间, 所以减去1秒
        req.setReckonEndTime(convertDateToString(param.end().toLocalDateTime().minusSeconds(1)));
        req.setPlatformId(EEZE.getPlatformId());
        req.setUri(EEZE_LIST);
        req.setHttpMethod(RequestTypeEnum.POST.getDesc());

        //0定义最终返回的TPG明细集合
//        List<TPGRespDto> finalTPGRespList = new ArrayList<>();
        // 0-1计算总页数,从第一页开始,每次限制两万条
        int totalPages = 1;
        int limit = 20000;

        // 1使用do-while循环处理分页请求
        int pageNum = 1;

        MutableList<EezeLobbyOrder> result = new FastList<>(0x40_000);
        List<EezeLobbyOrder> orders;

        while (pageNum <= totalPages) {
            req.setCurrent(pageNum);
            req.setSize(limit);
            EezeLobbyOrderResp response = Optional.of(req)
                    .map(feignService::callGateway)
                    .map(s -> JsonExecutors.fromJson(s, EezeLobbyOrderResp.class))
                    .map(s -> {
                        if (s.getCode() != 0) {
                            throw new CustomizeRuntimeException("EEZEChannelStrategy getOutOrders method two response error " + s.getMsg());
                        }
                        return s;
                    })
                    .orElse(new EezeLobbyOrderResp());
            totalPages = new BigDecimal(response.getData().getTotal())
                    .divide(new BigDecimal(limit), 0, RoundingMode.CEILING)
                    .intValue();
            log.info("调用EEZE明细接口第{}/{}页, 总共:{}条, 当前接口:{}", pageNum, totalPages, response.getData().getSize(), EEZE_LIST);
            //2解析每页的数据，把数据放到最终的List集合
            result.addAll(response.getData()
                    .getList());
            pageNum++;
        }
        log.info("getOutOrders return count: {}, param: {}", result.size(), param);

        return result;
    }

}
