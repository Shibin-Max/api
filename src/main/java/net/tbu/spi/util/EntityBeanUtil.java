package net.tbu.spi.util;

import net.tbu.common.enums.PlatformEnum;
import net.tbu.spi.entity.TInBetSummaryRecord;
import net.tbu.spi.entity.TOutBetSummaryRecord;
import net.tbu.spi.entity.TReconciliationBatch;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.function.Function;

public final class EntityBeanUtil {

    private EntityBeanUtil() {
    }

    /**
     * @param inSummary TInBetSummaryRecord
     * @param batch     TReconciliationBatch
     */
    public static void setSummaryRecordBy(@Nonnull TInBetSummaryRecord inSummary,
                                          @Nonnull TReconciliationBatch batch) {
        inSummary.setId(null)
                .setParentId(null)
                .setBatchDate(batch.getBatchDate())
                .setBatchNumber(batch.getBatchNumber())
                .setChannelId(batch.getChannelId())
                .setChannelName(batch.getChannelName());
    }

    /**
     * @param outSummary TOutBetSummaryRecord
     * @param batch      TReconciliationBatch
     */
    public static void setSummaryRecordBy(@Nonnull TOutBetSummaryRecord outSummary,
                                          @Nonnull TReconciliationBatch batch) {
        outSummary.setId(null)
                .setParentId(null)
                .setBatchDate(batch.getBatchDate())
                .setBatchNumber(batch.getBatchNumber())
                .setChannelId(batch.getChannelId())
                .setChannelName(batch.getChannelName());
    }

    /**
     * 获取订单号的唯一引用, 对订单的订单号进行处理, 如果有分段前缀, 则删除前缀
     *
     */
    private static final Function<String, String> COMMON_EXTRACTOR = (String betNumber) -> {
        if (betNumber == null)
            return "";
        if (betNumber.contains("_")) {
            var split = betNumber.split("_");
            return split[split.length - 1];
        } else
            return betNumber;
    };

    private static final Function<String, String> COMMON_EXTRACTOR_GEM = (String betNumber) -> {
        if (betNumber == null)
            return "";
        if (betNumber.contains("_")) {
            String[] split = betNumber.split("_");
            return split[0]; // 返回第一段
        } else {
            return betNumber;
        }
    };

    public static String getOrderRef(String betNumber, String platformId) {
        if (PlatformEnum.GEMH.getPlatformId().equals(platformId) ||
            PlatformEnum.GEMMG.getPlatformId().equals(platformId) ||
            PlatformEnum.GEMUD.getPlatformId().equals(platformId) ||
            PlatformEnum.GEMH.getPlatformName().equals(platformId) ||
            PlatformEnum.GEMMG.getPlatformName().equals(platformId) ||
            PlatformEnum.GEMUD.getPlatformName().equals(platformId)) {
            return getOrderRef(betNumber, COMMON_EXTRACTOR_GEM);
        }
        return getOrderRef(betNumber, COMMON_EXTRACTOR);
    }

    public static String getOrderRef(String betNumber, Function<String, String> extractor) {
        return extractor.apply(betNumber);
    }


    public static void accumulation(TInBetSummaryRecord accumulate, TInBetSummaryRecord record) {
        accumulate.setSumUnitQuantity((accumulate.getSumUnitQuantity() == null ? 0L : accumulate.getSumUnitQuantity()) + record.getSumUnitQuantity());
        accumulate.setSumBetAmount((accumulate.getSumBetAmount() == null ? BigDecimal.ZERO : accumulate.getSumBetAmount()).add(record.getSumBetAmount()));
        accumulate.setSumEffBetAmount((accumulate.getSumEffBetAmount() == null ? BigDecimal.ZERO : accumulate.getSumEffBetAmount()).add(record.getSumEffBetAmount()));
        accumulate.setSumWlValue((accumulate.getSumWlValue() == null ? BigDecimal.ZERO : accumulate.getSumWlValue()).add(record.getSumWlValue()));
    }

}
