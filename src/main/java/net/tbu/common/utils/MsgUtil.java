package net.tbu.common.utils;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MsgUtil {

    @Resource
    private MessageSource messageSource;

    @Resource
    private SystemInfo systemInfo;

    /**
     * 取得国际化讯息
     */
    public String getMsg(String key, Object... params) {
        return messageSource.getMessage(key, params, systemInfo.getLocale());
    }

    /**
     * 取得国际化讯息
     */
    public String getMsg(String key) {
        return getMsg(key, "");
    }

}
