package net.tbu.common.utils;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

public class MapUtil {
    // 自定义 AbstractMap 子类
    public static class MyAbstractMap<K, V> extends AbstractMap<K, V> {
        private final Map<K, V> delegateMap;

        // 构造函数传入一个现有的 Map
        public MyAbstractMap(Map<K, V> map) {
            this.delegateMap = map;
        }

        @Override
        public Set<Entry<K, V>> entrySet() {
            return delegateMap.entrySet();
        }
    }
}
