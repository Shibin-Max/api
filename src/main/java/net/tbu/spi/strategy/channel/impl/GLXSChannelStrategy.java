package net.tbu.spi.strategy.channel.impl;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.common.enums.TimeUnitTypeEnum;
import net.tbu.exception.CustomizeRuntimeException; // 引入自定义异常
import net.tbu.feign.client.internal.ThirdPartyGatewayFeignService;
import net.tbu.spi.strategy.channel.dto.LobbyOrderResult;
import net.tbu.spi.strategy.channel.dto.TimeRangeParam;
import net.tbu.spi.strategy.channel.dto.glxs.GLXSLobbyOrderDelegate;
import net.tbu.spi.strategy.channel.dto.glxs.GalaxRequest;
import net.tbu.spi.strategy.channel.dto.glxs.GlxsBetReportResponse;
import net.tbu.spi.strategy.channel.impl.base.BaseChannelStrategy;
import org.apache.commons.lang3.StringUtils; // 建议引入 StringUtils
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.ZoneOffset;

import static net.tbu.spi.strategy.channel.dto.LobbyConstant.SYS_FMT;

@Slf4j
@Service
public class GLXSChannelStrategy extends BaseChannelStrategy {

    private static final int PAGE_SIZE = 100;

    @Resource
    private ThirdPartyGatewayFeignService feignService;

    @Override
    public PlatformEnum getChannelType() {
        return PlatformEnum.GLXS_SW;
    }

    @Override
    protected TimeUnitTypeEnum getMaxQueryDetailTime() {
        return TimeUnitTypeEnum.HOUR;
    }

    @Override
    public LobbyOrderResult getOutOrders(TimeRangeParam param) {
        log.info("[GLXS] 开始拉取注单，时间范围: {}", param);
        var result = new LobbyOrderResult(param);

        // 使用父类的切片逻辑，每半小时拉一次
        for (TimeRangeParam once : splitTimeParam(param, TimeUnitTypeEnum.HALF_HOUR)) {
            var orders = pullAllPages(once);

            orders.stream()
                    .map(GLXSLobbyOrderDelegate::new)
                    .forEach(result::putOrder);

            log.info("[GLXS] 区间 {} 拉取完成，数量: {}", once, orders.size());
        }

        log.info("[GLXS] 全量拉取完成，总注单数: {}", result.size());
        return result;
    }

    private MutableList<GlxsBetReportResponse.BetTransaction> pullAllPages(TimeRangeParam param) {
        String[] time = toUtcTime(param);
        String startTime = time[0];
        String endTime = time[1];

        MutableList<GlxsBetReportResponse.BetTransaction> allOrders = new FastList<>(2000);
        int page = 1;

        while (true) {
            GalaxRequest req = new GalaxRequest();
            req.setUri("/api/Lobby/GetBetsReport");
            req.setHttpMethod("POST");
            req.setPlatformId(PlatformEnum.GLXS_SW.getPlatformId());
            req.setBetStartDate(startTime);
            req.setBetEndDate(endTime);
            req.setPageNumber(page);
            // 显式设置 pageSize，虽然默认是 100
            req.setPageSize(PAGE_SIZE);

            String respJson;
            try {
                respJson = feignService.callGateway(req);
            } catch (Exception e) {
                // 网络异常直接抛出
                throw new CustomizeRuntimeException("[GLXS] 网关调用异常: " + e.getMessage());
            }

            if (StringUtils.isBlank(respJson)) {
                throw new CustomizeRuntimeException("[GLXS] 响应为空，区间: " + param);
            }

            GlxsBetReportResponse resp = JSON.parseObject(respJson, GlxsBetReportResponse.class);

            if (resp == null || resp.getErrorCode() == null || resp.getErrorCode() != 0) {
                String errorMsg = (resp != null) ? String.valueOf(resp.getErrorCode()) : "null response";
                log.error("[GLXS] 接口报错 errorCode={}, 停止拉取", errorMsg);
                // 抛出异常，触发重试机制
                throw new CustomizeRuntimeException("[GLXS] 接口返回错误码: " + errorMsg);
            }

            var pageData = resp.getTransactions();
            if (pageData == null) {
                pageData = new FastList<>(0);
            }

            allOrders.addAll(pageData);

            log.info("[GLXS] 时间[{}-{}] 第{}页，本页{}条，累计{}条，TotalBets={}",
                    startTime, endTime, page, pageData.size(), allOrders.size(), resp.getTotalBets());

            // 分页终止条件
            if (pageData.size() < PAGE_SIZE) {
                break;
            }

            if (page > 500) {
                throw new CustomizeRuntimeException("[GLXS] 分页过多(>500)，可能存在死循环");
            }

            page++;
            try {
                Thread.sleep(200); // 稍微增加一点间隔，更稳
            } catch (Exception ignored) {
            }
        }
        return allOrders;
    }

    /**
     * 转换为 UTC 时间字符串，格式: yyyy-MM-dd'T'HH:mm:ss.SSSZ
     */
    private String[] toUtcTime(TimeRangeParam param) {
        String start = param.start().withZoneSameInstant(ZoneOffset.UTC).format(SYS_FMT) + "Z";
        String end = param.end().withZoneSameInstant(ZoneOffset.UTC).format(SYS_FMT) + "Z";
        return new String[]{start, end};
    }
}
