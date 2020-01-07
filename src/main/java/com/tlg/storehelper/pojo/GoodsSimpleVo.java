package com.tlg.storehelper.pojo;

import java.io.Serializable;

/**
 * 用于显示商品基本信息
 */
public class GoodsSimpleVo implements Serializable {
    /**货号*/
    public String goodsNo;
    /**名称*/
    public String goodsName;
    /**价格*/
    public int price;
    /**销量*/
    public int sales;
    /**库存*/
    public int stock;
    /**其它信息*/
    public String info;
    /**同款商品数*/
    public int sameStyleCount;
    /**同款货号，逗号分隔*/
    public String sameStyleGoodsNos;

    public GoodsSimpleVo(String goodsNo, String goodsName, int price, int sales, int stock, String info, int sameStyleCount, String sameStyleGoodsNos) {
        this.goodsNo = goodsNo;
        this.goodsName = goodsName;
        this.price = price;
        this.sales = sales;
        this.stock = stock;
        this.info = info;
        this.sameStyleCount = sameStyleCount;
        this.sameStyleGoodsNos = sameStyleGoodsNos;
    }
}
