package com.tlg.storehelper.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ShopHistoryItemVo implements Serializable {
    /**单号*/
    public String listNo;
    /**货号*/
    public String goodsNo;
    /**色尺*/
    public String size;
    /**货名*/
    public String goodsName;
    /**数量*/
    public int quantity;
    /**牌价*/
    public int price;
    /**折扣*/
    public float discount;
    /**结算价*/
    public int realPrice;
    /**金额*/
    public int amount;
    /**导购员*/
    public String sales;

}
