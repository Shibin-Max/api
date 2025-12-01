package net.tbu.spi.strategy.channel.dto;

import net.tbu.spi.entity.TInBetSummaryRecord;
import net.tbu.spi.entity.TOutBetSummaryRecord;

public record SummaryRecordPair(
        TInBetSummaryRecord inSummaryRecord,
        TOutBetSummaryRecord outSummaryRecord) {
}
