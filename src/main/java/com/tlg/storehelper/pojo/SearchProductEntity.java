package com.tlg.storehelper.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SearchProductEntity implements Serializable {

    /**尺码*/
    public String size;
    /**库存*/
    public String stock;
    /**当天销售数量*/
    public int quantity;
    /**广域库存*/
    public int allStock;
    /**他店库存*/
    public List<DetailBean> detail = new ArrayList<>();

    public static class DetailBean {
        /**店编*/
        public String storeCode;
        /**库存*/
        public String stock;
    }
}
