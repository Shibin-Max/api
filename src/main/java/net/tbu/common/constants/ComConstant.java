package net.tbu.common.constants;


import java.time.ZoneId;

/**
 * 通用常量
 */
public final class ComConstant {

    private ComConstant() {
    }

    // SITE CODE
    public static final String B55_SITE_CODE = "B55";
    public static final String C66_SITE_CODE = "C66";

    //分页相关
    public static final Integer PAGE = 1;
    public static final Integer INVERSE_PAGE_SIZE = 5000;

    public static final Integer PAGE_SIZE = 50000;

    public static final Integer JILI_PAGE_SIZE = 25000;

    /**
     * 操作人常量-系统 *
     */
    public static final String CREATED_BY = "System";

    public static final String RULE_DISABLE = "当前规则已被禁用!";

    public static final ZoneId SYS_ZONE_ID = ZoneId.systemDefault();


    //反向厅汇总数据查询url
    public static final String INVERSE_HHR_SUMMARY = "/api/digi/transaction/summary";
    public static final String INVERSE_SUMMARY = "/transaction/summary";
    public static final String INVERSE_HHR_LIST = "/api/digi/transaction/list";
    public static final String INVERSE_LIST = "/transaction/list";

    // RTS明细接口url
    public static final String RTS_LIST = "/api/gamehistory/v1/casino/games";

    // PG厅方汇总和明细接口url
    public static final String PG_SUMMARY = "/external-datagrabber/Bet/v4/GetPlayerBetsSummationTotal";
    public static final String PG_LIST = "/external-datagrabber/Bet/v4/GetHistoryForSpecificTimeRange";

    // OSM厅方汇总和明细接口url
    public static final String OSM_SUMMARY = "/api/studiobetsummary";
    public static final String OSM_LIST_URI = "/api/userbethistory";

    // EEZE厅方汇总和明细接口url
    public static final String EEZE_SUMMARY = "/dc/getSummaryReport";
    public static final String EEZE_LIST = "/dc/getOrders";

    // FACAI厅方明细接口url
    public static final String FACAI_LIST_URI = "/GetHistoryRecordList";

    // GEMM厅方接口url
    public static final String GEMM_LIST = "/result/query/";
    public static final String GEMM_SUMMARY = "/summary/hourly/";


}
