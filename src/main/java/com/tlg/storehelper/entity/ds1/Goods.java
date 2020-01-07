package com.tlg.storehelper.entity.ds1;

import java.io.Serializable;

public class Goods implements Serializable {

    /**商品货号*/
    public String goodsNo;
    /**商品名称*/
    public String goodsName;
    /**商品款号*/
    public String styleNo;
    /**商品颜色值*/
    public String colorDesc;
    /**商品图片名*/
    public String pic;
    /**吊牌价*/
    public int price;
//    /**同款货号集合大小*/
//    public int sameStyleCount;
}
