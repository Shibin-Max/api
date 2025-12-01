package net.tbu.common.utils;

import javax.annotation.Nonnull;

public final class StringUtil {

    private StringUtil() {
        throw new IllegalStateException(StringUtil.class.getSimpleName() + " is Utility class");
    }

    @Nonnull
    public static String toJSON(Object obj) {
        return obj == null ? "" : com.alibaba.fastjson.JSON.toJSONString(obj);
    }

}
