package net.tbu.spi.strategy.channel.dto.shaba;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum SHABATicketStatusEnum {

    WAITING("waiting", "等待中", false),
    RUNNING("running", "进行中", false),
    VOID("void", "作废", false),
    REFUND("refund", "退款", false),
    REJECT("reject", "已取消", false),

    LOSE("lose", "输", true),
    WON("won", "赢", true),
    DRAW("draw", "和局", true),
    HALF_WON("half won", "半赢", true),
    HALF_LOSE("half lose", "半输", true),

    ;

    private final String status;
    private final String desc;
    private final boolean isValid;

    private static final Map<String, Boolean> VALID_MAP =
            Stream.of(values())
                    .collect(Collectors.toMap(SHABATicketStatusEnum::getStatus, SHABATicketStatusEnum::isValid));

    public static boolean isValid(String status) {
        return VALID_MAP.getOrDefault(status, false);
    }

}
