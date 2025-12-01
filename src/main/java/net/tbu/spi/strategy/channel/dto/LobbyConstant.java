package net.tbu.spi.strategy.channel.dto;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.ofPattern;

public enum LobbyConstant {

    ;

    public static final String YYYY_MM_DD_T_HH_MM_SS_SSSSSS = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS";

    public static final String YYYY_MM_DD_T_HH_MM_SS_SSS = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final DateTimeFormatter SYS_FMT = ofPattern(YYYY_MM_DD_T_HH_MM_SS_SSS);

    public static final String YYYY_MM_DD_T_HH_MM_SS = "yyyy-MM-dd'T'HH:mm:ss";

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter SIMPLE_FMT = ofPattern(YYYY_MM_DD_HH_MM_SS);

    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String YYYYMMDDHHMM = "yyMMddHHmm";
    public static final String YYYYMMDDHH = "yyMMddHH";

    public static final ZoneId SYS_ZONE_ID = ZoneId.of("Asia/Shanghai");

    /**
     * JDB时间相关
     */
    public static final DateTimeFormatter JDB_FMT = ofPattern("dd-MM-yyyy HH:mm:ss");
    public static final ZoneId JDB_ZONE_ID = ZoneOffset.ofHours(-4);

    /**
     * HBN接口请求参数时间格式
     */
    public static final ZoneOffset HBN_ZONE_OFFSET = ZoneOffset.UTC;
    public static final DateTimeFormatter HBN_REQ_DT_FMT = ofPattern(YYYYMMDDHHMMSS);
    public static final DateTimeFormatter HBN_RSP_DT_FMT = ofPattern(YYYY_MM_DD_T_HH_MM_SS_SSS);

    /**
     * CQ9
     */
    public static final ZoneOffset CQ9_ZONE_OFFSET = ZoneOffset.ofHours(-4);
    public static final DateTimeFormatter CQ9_REQ_DT_FMT = ofPattern(YYYY_MM_DD_T_HH_MM_SS_SSS);
    public static final DateTimeFormatter CQ9_RSP_DT_FMT = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    /**
     * FACAI
     */
    public static final ZoneOffset FACAI_ZONE_OFFSET = ZoneOffset.ofHours(-4);
    public static final DateTimeFormatter FACAI_REQ_DT_FMT = ofPattern(YYYY_MM_DD_HH_MM_SS);

    /**
     * SL
     */
    public static final DateTimeFormatter SL_RSP_DT_FMT = ofPattern(YYYY_MM_DD_HH_MM_SS);

    /**
     * IGO
     */
    public static final DateTimeFormatter IGO_REQ_DT_FMT = ofPattern(YYYY_MM_DD_HH_MM_SS);
    public static final DateTimeFormatter IGO_RSP_DT_FMT = ofPattern(YYYY_MM_DD_T_HH_MM_SS_SSSSSS);

    /**
     * TPG
     */
    public static final DateTimeFormatter TPG_REQ_DT_FMT = ofPattern(YYYY_MM_DD_T_HH_MM_SS);
    public static final DateTimeFormatter TPG_RSP_DT_FMT = ofPattern(YYYY_MM_DD_HH_MM_SS);

    /**
     * GEMM
     */
    public static final ZoneOffset GEMM_ZONE_OFFSET = ZoneOffset.UTC;
    public static final DateTimeFormatter GEMM_REQ_DT_FMT = ofPattern(YYYYMMDDHHMM);
    public static final DateTimeFormatter GEMM_REQ_DT_FMTH = ofPattern(YYYYMMDDHH);
    public static final DateTimeFormatter GEMM_RSP_DT_FMT = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

}
