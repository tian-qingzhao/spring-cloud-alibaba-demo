package com.tqz.alibaba.cloud.auth.po;

/**
 * <p>key和value对象的封装
 *
 * @author tianqingzhao
 * @since 2023/1/10 10:59
 */
public class Pair<K, V> {
    
    private K key;
    
    private V value;
    
    public Pair() {
    }
    
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }
    
    public K getKey() {
        return key;
    }
    
    public void setKey(K key) {
        this.key = key;
    }
    
    public V getValue() {
        return value;
    }
    
    public void setValue(V value) {
        this.value = value;
    }
}
