package com.tlg.storehelper.pojo;

import java.io.Serializable;

public class PsiVo implements Serializable {
    /**收货 receiving*/
    public int r;
    /**发货 delivery*/
    public int d;
    /**销售数量 sales*/
    public int s;
    /**销售金额 sales money*/
    public int sa;
    /**库存 inventory*/
    public int i;

    public PsiVo(int receiving, int delivery, int sales, int salesMoney, int inventory) {
        this.r = receiving;
        this.d = delivery;
        this.s = sales;
        this.sa = salesMoney;
        this.i = inventory;
    }
}
