package com.tlg.storehelper.pojo;

import java.io.Serializable;

public class CollocationPairVo implements Serializable {

    /**商品1*/
    public GoodsSimpleVo goods1;

    /**商品2*/
    public GoodsSimpleVo goods2;

    /**连带次数*/
    public int frequency;
}
