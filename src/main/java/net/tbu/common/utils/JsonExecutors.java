package net.tbu.common.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import org.apache.http.HttpResponse;

import java.io.InputStream;

/**
 * JSON处理器
 * @author YuHao
 */
public final class JsonExecutors {

    private JsonExecutors() {
        throw new IllegalAccessError("JsonExecutors is utility class");
    }

    /**
     * 将对象转换为JSON
     *
     * @param value 要转换的对象
     *              Json
     */
    public static String toJson(Object value) {
        return JSON.toJSONString(value);
    }

    /**
     * 将对象转换为JSON
     *
     * @param value 要转换的对象
     *              Json
     */
    public static String toPrettyJson(Object value) {
        return JSON.toJSONString(value, JSONWriter.Feature.PrettyFormat);
    }

    /**
     * 从JSON反序列化对象
     *
     * @param json    Json
     * @param classes 对象类型Class
     * @param <T>     对象类型
     *                对象
     */
    public static <T> T fromJson(String json, Class<T> classes) {
        return JSON.parseObject(json, classes);
    }

    /**
     * 从JSON输入流反序列化对象
     *
     * @param inputStream Json输入流
     * @param classes     对象类型Class
     * @param <T>         对象类型
     *                    对象
     */
    public static <T> T fromJson(InputStream inputStream, Class<T> classes) {
        return fromJson(StringExecutors.toString(inputStream), classes);
    }

    /**
     * 从JSON输入流反序列化对象
     *
     * @param response Json输入流
     * @param classes  对象类型Class
     * @param <T>      对象类型
     *                 对象
     */
    public static <T> T fromJson(HttpResponse response, Class<T> classes) {
        return fromJson(StringExecutors.toString(response), classes);
    }

    /**
     * 从JSON输入流反序列化对象
     *
     * @param response Json输入流
     * @param classes  对象类型Class
     * @param <T>      对象类型
     *                 对象
     */
    public static <T> T fromJson(java.net.http.HttpResponse<String> response, Class<T> classes) {
        return fromJson(StringExecutors.toString(response), classes);
    }

}
