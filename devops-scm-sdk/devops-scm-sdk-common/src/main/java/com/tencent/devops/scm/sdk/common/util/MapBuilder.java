package com.tencent.devops.scm.sdk.common.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MapBuilder<K, V> {
    private final Map<K, V> map;

    private MapBuilder(Supplier<Map<K, V>> mapSupplier) {
        this.map = mapSupplier.get();
    }

    public static <K, V> MapBuilder<K, V> newBuilder() {
        return new MapBuilder<>(HashMap::new);
    }

    public static <K, V> MapBuilder<K, V> newBuilder(Supplier<Map<K, V>> mapSupplier) {
        return new MapBuilder<>(mapSupplier);
    }

    public static <K, V> MapBuilder<K, V> newLinkedBuilder() {
        return new MapBuilder<>(LinkedHashMap::new);
    }


    public MapBuilder<K, V> add(K key, V value) {
        map.put(key, value);
        return this;
    }

    public MapBuilder<K, V> addAll(Map<? extends K, ? extends V> entries) {
        map.putAll(entries);
        return this;
    }

    public Map<K, V> build() {
        return Collections.unmodifiableMap(map);
    }

    public Map<K, V> buildMutable() {
        return new HashMap<>(map);
    }
}
