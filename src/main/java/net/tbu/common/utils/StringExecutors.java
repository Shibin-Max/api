package net.tbu.common.utils;

import com.alibaba.cloud.commons.io.IOUtils;
import lombok.SneakyThrows;
import org.apache.http.HttpResponse;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串处理
 *
 * @author YuHao
 */
public final class StringExecutors {

    private StringExecutors() {
        throw new IllegalAccessError("StringExecutors is utility class");
    }

    /**
     * 从集合转换字符串
     *
     * @param collection 集合
     * @return 字符串
     */
    @SneakyThrows
    public static String toString(Collection<?> collection) {
        return collection == null ? "null" : ("size(" + collection.size() + ")");
    }

    /**
     * 从输出流转换字符串
     *
     * @param inputStream 输入流
     * @return 字符串
     */
    @SneakyThrows
    public static String toString(InputStream inputStream) {
        return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    }

    /**
     * 从HttpResponse转换字符串
     *
     * @param response HttpResponse
     * @return 字符串
     */
    @SneakyThrows
    public static String toString(HttpResponse response) {
        return toString(response.getEntity().getContent());
    }

    /**
     * 从HttpResponse转换字符串
     *
     * @param response HttpResponse
     * @return 字符串
     */
    @SneakyThrows
    public static String toString(java.net.http.HttpResponse<String> response) {
        return response.body();
    }

    /**
     * json转byte[]
     *
     * @param json json
     * @return 字符串
     */
    @SneakyThrows
    public static byte[] listToByte(String json) {
        return json.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 去除字符串中的回车\n、换行符\r、制表符\t
     *
     * @param str String
     * @return 字符串
     */
    public static String patternNRT(String str) {
        if (str != null) {
            Pattern p = Pattern.compile("\\t|\\r|\\n");
            Matcher m = p.matcher(str);
            str = m.replaceAll(" ");
        }
        return str;
    }

    /**
     * String转Map
     *
     * @param str String
     * @return Map
     */
    public static Map<String, String> convertStringToMap(String str) {
        Map<String, String> map = new HashMap<>();
        String[] keyValuePairs = str.split("&");
        for (String pair : keyValuePairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                map.put(keyValue[0], keyValue[1]);
            }
        }
        return map;
    }

    /**
     * 截取_后面的数据
     *
     * @param str String
     * @return String
     */
    public static String symbolSub(String str) {
        // 查找 `_` 符号的位置
        int index = str.indexOf('_');

        // 如果找到了 `_`
        if (index != -1) {
            // 从 `_` 后的位置开始截取
            return str.substring(index + 1);
        }
        return str;
    }

    /**
     * string 转 List
     *
     * @param str String
     * @return List<String>
     */
    public static List<String> strToList(String str) {
        return Arrays.asList(str.split(","));
    }

    /**
     * bean 转 map
     *
     * @param bean Object
     * @return Map
     */
    public static Map<String, Object> convertBeanToMap(Object bean) {
        Map<String, Object> map = new HashMap<>();
        try {
            // 获取Bean的类信息
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());

            // 获取Bean的所有属性描述符
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                String propertyName = propertyDescriptor.getName();

                // 跳过getClass方法
                if (!propertyName.equals("class")) {
                    // 获取getter方法并执行，获取属性值
                    Object value = propertyDescriptor.getReadMethod().invoke(bean);
                    map.put(propertyName, value);
                }
            }
        } catch (Exception ignored) {
        }
        return map;
    }

    /**
     * 将对象字符串缩略显示
     *
     * @param obj       对象
     * @param maxLength 最大长度
     *                  缩略后的JSON字符串
     */
    public static String toAbbreviatedString(Object obj, int maxLength) {
        if (obj == null) {
            return "null";
        }
        if (obj instanceof String str) {
            return toAbbreviatedString(str, maxLength);
        }
        return toAbbreviatedString(obj.toString(), maxLength);
    }


    /**
     * 将字符串缩略显示
     *
     * @param str       字符串
     * @param maxLength 最大长度
     *                  缩略后的JSON字符串
     */
    public static String toAbbreviatedString(String str, int maxLength) {
        if (str == null) {
            return "NULL";
        }
        if (str.isEmpty()) {
            return "EMPTY";
        }
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength) + "...(length=" + str.length() + ")";
    }

}