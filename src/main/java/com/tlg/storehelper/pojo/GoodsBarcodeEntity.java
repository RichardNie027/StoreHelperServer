package com.tlg.storehelper.pojo;

import java.util.ArrayList;
import java.util.List;

public class GoodsBarcodeEntity extends BaseResponseEntity {

    public List<ResultBean> result = new ArrayList<>();

    public String lastModDate = "";

    public static class ResultBean {
        public String id;
        public String brand;
        public String goodsNo;
        public String goodsName;
        public String sizeDesc;
        public String barcode;
    }
}