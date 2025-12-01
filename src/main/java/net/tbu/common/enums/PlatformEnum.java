
package net.tbu.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.eclipse.collections.api.map.ImmutableMap;

import static java.util.Arrays.stream;
import static org.eclipse.collections.impl.collector.Collectors2.toMap;

/**
 * 平台枚举（厅名枚举）
 * 每个枚举值包含：
 *  - platformId：平台编号（用于业务逻辑）
 *  - platformName：平台名称（用于显示）
 *  - tableSuffix：数据库表名后缀（完整表名格式：ORDERS_<tableSuffix>）
 *
 * 示例：
 *  ORDERS_GEMINI 表名对应 tableSuffix = "GEMINI"
 *
 * @author FT hao.yu
 * @time 2024/12/24 15:26
 */
@Getter
@RequiredArgsConstructor
public enum PlatformEnum {

    INVALID("-1", "INVALID", "INVALID"),

    /**
     * 正向厅
     */
    CQ9("073", "CQ9", "CQ9"),
    HBN("099", "HBN", "HBN"),
    PG("100", "PG", "PG"),
    TPG("103", "TPG", "TPG"),
    JDB("104", "JDB", "JDB"),
    SHABA_V2("131", "SHABA_V2", "SHABA_V2"),
    PLAYSTAR("141", "PLAYSTAR", "PLAYSTAR"),
    JILI("161", "JILI", "JILI"),
    JILI_SP("162", "JILI_SP", "JILI"),
    TADA("163", "TADA", "TADA"),
    PP("199", "PP", "PRAGMATICPLAY"),
    GAMEZONE("175", "GAMEZONE", "TIPTOP"),
    FACAI("176", "FACAI", "FACAI"),
    EEZE("177", "EEZE", "EEZE"),
    OSM("180", "OSM", "OSM"),
    HACKSAW("207", "HACKSAW", "HACKSAW"),


    SWERTE("231", "SWERTE", "SWERTE"),
    BPO("233", "BPO", "BPO"),
    IGO("301", "IGO", "IGO"),
    KALARO("168", "KALARO", "KALARO"),
    GLXS_SW("196", "GLXS", "GLXS"),
    LNW("236", "LNW", "LNW"),
    FTG("200", "FTG", "FTG"),


    /**
     * SL厅
     */
    AQUA("183", "AQUA", "IRGO"),
    BINGO("079", "BINGO", "CG"),
    COLORGAME("172", "COLORGAME", "COLORGAME"),
    EBGO("080", "EBGO", "EBGO"),
    GINTO("178", "GINTO", "MBGO"),
    INBETWEEN("229", "INBETWEEN", "INBETWEEN"),
    PDB("186", "PDB", "PDB"),
    PULAPUTI("184", "PULAPUTI", "PULAPUTI"),
    BLRE("187", "BLRE", "BLRE"),

    /**
     * RTS厅
     */
    NETENT("106", "NETENT", "RTS"),
    REDTIGER("107", "REDTIGER", "RTS"),
    EVOLUTION("108", "EVOLUTION", "RTS"),
    BTG("188", "BTG", "RTS"),
    NLC("192", "NLC", "RTS"),

    /**
     * 反向厅
     */
    HHR("501", "HHR", "SIP"),
    KM("502", "KM", "SIP"),
    YGR("503", "YGR", "SIP"),
    YB("505", "YB", "SIP"),
    GV("506", "GV", "SIP"),
    SLO("507", "SLO", "SIP"),
    FIVE_G("508", "5G", "SIP"),
    CALETA("509", "CALETA", "SIP"),
    SMART_SOFT("512", "SmartSoft", "SIP"),
    TRADEX("513", "TRADEX", "SIP"),
    REELX("515", "REELX", "SIP"),
    PDBT("516", "PDBT", "SIP"),
    EEZES("510", "EEZES", "SIP"),

    /**
     * GEMINI厅
     */
    GEMM("220", "GEMM", "GEMINI"),
    GEMH("221", "GEMH", "GEMINI"),
    GEML("222", "GEML", "GEMINI"),
    GEMMG("223", "GEMMG", "GEMINI"),
    GEMUD("224", "GEMUD", "GEMINI"),
    ;

    /**
     * 平台编号（用于业务逻辑）
     */
    private final String platformId;

    /**
     * 平台名称（用于显示）
     */
    private final String platformName;

    /**
     * ORDERS_<tableSuffix> 表名后缀
     * 示例：ORDERS_GEMINI
     */
    private final String tableSuffix;

    // PlatformId -> PlatformName 映射表
    private static final ImmutableMap<String, String> PLATFORM_NAME_MAP =
            stream(PlatformEnum.values())
                    .collect(toMap(PlatformEnum::getPlatformId, PlatformEnum::getPlatformName))
                    .toImmutable();

    /**
     * 根据平台ID获取平台名称
     */
    public static String getPlatformName(String platformId) {
        return PLATFORM_NAME_MAP.getOrDefault(platformId, null);
    }

    // PlatformId -> TableSuffix 映射表
    private static final ImmutableMap<String, String> TABLE_SUFFIX_MAP =
            stream(PlatformEnum.values())
                    .collect(toMap(PlatformEnum::getPlatformId, PlatformEnum::getTableSuffix))
                    .toImmutable();

    /**
     * 根据平台ID获取表名后缀
     */
    public static String getTableSuffix(String platformId) {
        return TABLE_SUFFIX_MAP.getOrDefault(platformId, null);
    }
}
