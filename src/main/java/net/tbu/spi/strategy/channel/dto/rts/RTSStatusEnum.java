package net.tbu.spi.strategy.channel.dto.rts;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum RTSStatusEnum {
    RESOLVED("Resolved", "已结算"),
    CANCELLED("Cancelled", "已取消");

    private final String name;
    private final String desc;

    public static String getDescByName(String name) {
        if (StringUtils.isBlank(name)) {
            return StringUtils.EMPTY;
        }
        return Arrays.stream(RTSStatusEnum.values())
                .filter(e -> e.getName().equals(name))
                .map(RTSStatusEnum::getDesc)
                .findFirst()
                .orElse("");
    }
}
