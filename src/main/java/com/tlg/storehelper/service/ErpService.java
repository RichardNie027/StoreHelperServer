package com.tlg.storehelper.service;

import com.tlg.storehelper.entity.main.ErpUser;
import com.tlg.storehelper.entity.main.Goods;
import com.tlg.storehelper.entity.third.BestSelling;
import com.tlg.storehelper.entity.third.Collocation;
import com.tlg.storehelper.pojo.SimpleEntity;

import java.util.List;

public interface ErpService {

    List<ErpUser> getAllStoreUsers();

    ErpUser getUserByAccount(String userAccount);

    SimpleEntity<String> getAllSimpleGoodsBarcodes(String lastModDate);

    //GoodsBarcodeEntity getLastestGoodsBarcodes(String lastModDate);

    Goods getGoods(String goodsNo);

    String getGoodsPictureName(String goodsNo);

    List<Goods> getAllGoods();

    List<Collocation> getCollocation(String goodsNo);

    int getBestSellingCount(String storeCode, String dimension);

    List<BestSelling> getBestSelling(String storeCode, String dimension, int recordPerPage, int page);

}
