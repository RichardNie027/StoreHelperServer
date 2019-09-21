package com.tlg.storehelper.service;

import com.tlg.storehelper.entity.ds1.ErpUser;
import com.tlg.storehelper.entity.ds1.Goods;
import com.tlg.storehelper.entity.ds3.BestSelling;
import com.tlg.storehelper.entity.ds3.Collocation;
import com.tlg.storehelper.pojo.SimpleEntity;

import java.util.List;

public interface ErpService {

    List<ErpUser> getAllStoreUsers();

    ErpUser getUserByAccount(String userAccount);

    SimpleEntity<String> getAllSimpleGoodsBarcodes(String lastModDate);

    //GoodsBarcodeEntity getLastestGoodsBarcodes(String lastModDate);

    Goods getGoods(String goodsNo);

    boolean queryGoodsPicture(String goodsNo);

    String getGoodsPictureName(String goodsNo);

    List<Goods> getAllGoods();

    List<Collocation> getCollocation(String goodsNo);

    int getBestSellingCount(String storeCode, String dimension);

    List<BestSelling> getBestSelling(String storeCode, String dimension, int recordPerPage, int page);

}
