package net.tbu.common.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 数据源类型枚举
 *
 * @author FT hao.yu
 * @time 2024/12/24 15:26
 */
@Getter
@RequiredArgsConstructor
public enum SourceTypeEnum implements IntEnumInterface {

    FILE(1, "文件"),
    API(2, "接口"),
    REPTILE(3, "爬虫");

    private final int eventId;
    private final String desc;

}
