package net.tbu.common.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * ReviewIssueType 用于复核发送邮件页面
 *
 * @author FT hao.yu
 * @time 2025/05/13 15:26
 */
@Getter
@RequiredArgsConstructor
public enum ReviewIssueTypeEnum implements IntEnumInterface {

    NO(0, "No"),
    YES(1, "Yes"),
    NO_NEED_TO_FIX(2, "No need to fix");

    private final int eventId;
    private final String desc;

    public static String getNameByEventId(Integer eventId) {
        if (eventId == null) {
            return StringUtils.EMPTY;
        }
        return Arrays.stream(ReviewIssueTypeEnum.values())
                .filter(e -> e.getEventId() == eventId)
                .map(ReviewIssueTypeEnum::name)
                .findFirst()
                .orElse("");
    }

    public static String getDescByEventId(Integer eventId) {
        if (eventId == null) {
            return StringUtils.EMPTY;
        }
        return Arrays.stream(ReviewIssueTypeEnum.values())
                .filter(e -> e.getEventId() == eventId)
                .map(ReviewIssueTypeEnum::getDesc)
                .findFirst()
                .orElse("");
    }
}
