package net.tbu.common.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;

import java.util.List;
import java.util.Objects;

/**
 * @author FT hao.yu cglib包中的bean拷贝(性能优于Spring当中的BeanUtils)
 * @time 2024/09/30 15:26
 */
public class BeanCglibUtils {

    private BeanCglibUtils() {
        super();
    }

    /**
     * 深拷贝, 我们可以直接传实例化的拷贝对象和被实例化的拷贝对象进行深拷贝
     *
     * @param source 源对象
     * @param target 目标类
     */
    public static <T> T copy(Object source, Class<T> target) {
        if (source == null || target == null) {
            return null;
        }
        // 实例化目标对象
        try {
            // 创建新的对象实例
            T newInstance = target.newInstance();
            // 返回新对象
            return copy(source, newInstance);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T copy(Object source, T target) {
        if (source == null || target == null) {
            return null;
        }
        // 实例化目标对象
        try {
            CopyOptions options = CopyOptions.create()
                    .setIgnoreNullValue(true)  // 忽略源对象属性为空的情况
                    .setIgnoreError(true);     // 忽略复制过程中出现的错误
            BeanUtil.copyProperties(source, target, options);
            // 返回新对象
            return target;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * List深拷贝
     *
     * @param sources 源集合
     * @param target  目标类
     * @param <S>     源类型
     * @param <T>     目标类型
     * @return 目标类集合
     */
    public static <S, T> List<T> copyList(List<S> sources, Class<T> target) {
        // 用来判断目标类型空指针异常
        Objects.requireNonNull(target);
        return sources.stream()
                .map(src -> copy(src, target))
                .toList();
    }
}
