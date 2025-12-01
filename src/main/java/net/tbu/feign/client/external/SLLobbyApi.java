package net.tbu.feign.client.external;

import net.tbu.spi.strategy.channel.dto.sl.SLLobbyOrdersReq;
import net.tbu.spi.strategy.channel.dto.sl.SLLobbyOrdersResp;
import net.tbu.spi.strategy.channel.dto.sl.SLLobbySummaryReq;
import net.tbu.spi.strategy.channel.dto.sl.SLLobbySummaryResp;

public interface SLLobbyApi {

    /**
     * 查询厅方汇总接口
     *
     * @param summaryReq  SLLobbySummaryReq
     * @param channelName String
     * @return SLLobbySummaryResp
     */
    default SLLobbySummaryResp getDailyOrders(SLLobbySummaryReq summaryReq, String channelName) {
        return getDailyOrders(summaryReq, channelName, System.nanoTime());
    }

    /**
     * 查询厅方汇总接口
     *
     * @param summaryReq  SLLobbySummaryReq
     * @param channelName String
     * @return SLLobbySummaryResp
     */
    SLLobbySummaryResp getDailyOrders(SLLobbySummaryReq summaryReq, String channelName, long nano);

    /**
     * 查询厅方明细接口
     *
     * @param ordersReq   SLLobbyOrdersReq
     * @param sortBillNo  String
     * @param channelName String
     * @return SLLobbyOrdersResp
     */
    default SLLobbyOrdersResp getOrders(SLLobbyOrdersReq ordersReq, String sortBillNo, String channelName) {
        return getOrders(ordersReq, sortBillNo, channelName, System.nanoTime());
    }

    /**
     * 查询厅方明细接口
     *
     * @param ordersReq   SLLobbyOrdersReq
     * @param sortBillNo  String
     * @param channelName String
     * @return SLLobbyOrdersResp
     */
    SLLobbyOrdersResp getOrders(SLLobbyOrdersReq ordersReq, String sortBillNo, String channelName, long nano);

}
