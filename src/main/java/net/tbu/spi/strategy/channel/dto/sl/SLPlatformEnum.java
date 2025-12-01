package net.tbu.spi.strategy.channel.dto.sl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.tbu.common.enums.PlatformEnum;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.impl.collector.Collectors2;

import java.util.List;

import static java.util.Arrays.stream;

/**
 * SL厅相关请求参数配置
 */
@Getter
@AllArgsConstructor
public enum SLPlatformEnum {

    INVALID(PlatformEnum.INVALID, "", "", "", List.of()),

    /**
     * (name=AQUA, platformId=183, sourceGame=G09, gametype=IRGO, vidList=[IR01, IR02])
     */
    AQUA(PlatformEnum.AQUA, "183", "G09", "IRGO", List.of("IR01", "IR02")),

    /**
     * (name=BINGO, platformId=079, sourceGame=G04, gametype=, vidList=[BG01, FB01])
     */
    BINGO(PlatformEnum.BINGO, "079", "G04", "", List.of("BG01", "FB01")),

    /**
     * (name=COLORGAME, platformId=172, sourceGame=G13, gametype=, vidList=[CG01, CW01])
     */
    COLORGAME(PlatformEnum.COLORGAME, "172", "G13", "", List.of("CG01", "CW01")),

    /**
     * (name=EBG, platformId=080, sourceGame=G09, gametype=EBGO, vidList=[EB01, EB02, EB03])
     */
    EBGO(PlatformEnum.EBGO, "080", "G09", "EBGO", List.of("EB01", "EB02", "EB03")),

    /**
     * (name=GINTO, platformId=178, sourceGame=G09, gametype=MBGO, vidList=[MB01, MB02, MB03])
     */
    GINTO(PlatformEnum.GINTO, "178", "G09", "MBGO", List.of("MB01", "MB02", "MB03")),

    /**
     * (name=INBETWEEN, platformId=229, sourceGame=G34, gametype=IBTW, vidList=[IB01])
     */
    INBETWEEN(PlatformEnum.INBETWEEN, "229", "G34", "IBTW", List.of("IB01")),

    /**
     * (name=PDB, platformId=186, sourceGame=G20, gametype=RBDG, vidList=[RB01])
     */
    PDB(PlatformEnum.PDB, "186", "G20", "RBDG", List.of("RB01")),

    /**
     * (name=PDBT, platformId=178, sourceGame=G39, gametype=PDBT, vidList=[DB01])
     */
    PDBT(PlatformEnum.PDBT, "178", "G39", "PDBT", List.of("DB01")),

    /**
     * (name=PULAPUTI, platformId=184, sourceGame=G16, gametype=RBGO, vidList=[RG01])
     */
    PULAPUTI(PlatformEnum.PULAPUTI, "184", "G16", "RBGO", List.of("RG01")),

    /**
     * (name=BLRE, platformId=187, sourceGame=G31, gameType=BLRE, vidList=[BL01])
     */
    BLRE(PlatformEnum.BLRE, "187", "G31", "BLRE", List.of("BL01")),

    ;

    private final PlatformEnum platform;
    private final String platformId;
    private final String sourceGame;
    private final String gameType;
    private final List<String> vidList;

    private static final ImmutableMap<String, SLPlatformEnum> MAP = stream(SLPlatformEnum.values())
            .collect(Collectors2.toMap(e -> e.platformId, e -> e))
            .toImmutable();

    public static SLPlatformEnum getByPlatformId(String platformId) {
        return MAP.getOrDefault(platformId, INVALID);
    }

}
