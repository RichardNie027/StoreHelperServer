package com.tlg.storehelper.pojo;

import java.io.Serializable;

public class KV<K,V> implements Serializable {
    public K k;
    public V v;

    public KV(K k, V v) {
        this.k = k;
        this.v = v;
    }
}
