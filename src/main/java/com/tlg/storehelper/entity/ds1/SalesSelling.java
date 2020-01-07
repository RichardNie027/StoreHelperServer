package com.tlg.storehelper.entity.ds1;

import java.io.Serializable;

/**导购销售数据*/
public class SalesSelling implements Serializable {
    public String salesCode;
    public String salesName;
    public String storeCode;
    public String storeLevel;
    public int transactions;
    public int quantity;
    public int money;
    public int days;

}
