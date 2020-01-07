package com.tlg.storehelper.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SellingVo extends BaseResponseVo {

    /**商品编号*/
    public String goodsNo;
    /**商品名称*/
    public String goodsName;
    /**吊牌价*/
    public int price;
    /**同款货号s*/
    public String sibling;
    /**包含同款*/
    public boolean hasSibling;

    public List<DetailBean> detail = new ArrayList<>();

    public static class DetailBean {
        /**
         * 店编
         */
        public String storeCode;
        /**
         * 销售件数
         */
        public int quantity;

        public DetailBean(String storeCode, int quantity) {
            this.storeCode = storeCode;
            this.quantity = quantity;
        }
    }
}
