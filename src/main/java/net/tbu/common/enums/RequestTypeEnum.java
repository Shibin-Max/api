package net.tbu.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * 请求类型枚举
 *
 * @author FT hao.yu
 * @time 2024/12/24 15:26
 */
@Getter
@RequiredArgsConstructor
public enum RequestTypeEnum implements IntEnumInterface {

    GET(1, "GET"),
    POST(2, "POST"),
    PUT(3, "PUT"),
    DELETE(4, "DELETE");

    private final int eventId;
    private final String desc;

    public static String getDesc(Integer eventId) {
        if (eventId == null) {
            return null;
        }
        return Arrays.stream(RequestTypeEnum.values())
                .filter(obj -> obj.getEventId() == eventId)
                .map(RequestTypeEnum::getDesc)
                .findFirst()
                .orElse(null);
    }

}
