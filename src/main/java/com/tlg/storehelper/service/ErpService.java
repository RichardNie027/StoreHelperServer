package com.tlg.storehelper.service;

import com.tlg.storehelper.controller.ApiController;
import com.tlg.storehelper.entity.ds1.ErpUser;
import com.tlg.storehelper.entity.ds1.Goods;
import com.tlg.storehelper.entity.ds1.Membership;
import com.tlg.storehelper.entity.ds3.RunsaPosTransfer;
import com.tlg.storehelper.entity.ds3.BestSelling;
import com.tlg.storehelper.entity.ds3.Collocation;
import com.tlg.storehelper.pojo.MembershipVo;
import com.tlg.storehelper.pojo.ShopHistoryVo;
import com.tlg.storehelper.pojo.SimpleListMapEntity;
import com.tlg.storehelper.pojo.StockVo;

import java.util.List;

public interface ErpService {

    List<ErpUser> getAllStoreUsers();

    ErpUser getUserByAccount(String userAccount);

    SimpleListMapEntity<String> getAllSimpleGoodsBarcodes(String lastModDate);

    //GoodsBarcodeEntity getLastestGoodsBarcodes(String lastModDate);

    Goods getGoods(String goodsNo);

    boolean queryGoodsPicture(String goodsNo);

    String getGoodsPictureName(String goodsNo);

    List<Goods> getAllGoods();

    List<Collocation> getCollocation(String goodsNo);

    int getBestSellingCount(String storeCode, String dimension);

    List<BestSelling> getBestSelling(String storeCode, String dimension, int pageSize, int page);

    List<MembershipVo> getMembership(String membershipId);

    int getShopHistoryCount(String membershipId);

    List<ShopHistoryVo> getShopHistory(String membershipId,  int pageSize, int page);

    /**保存浪沙Pos数据*/
    void saveRunsaPosData(ApiController.RunsaPosDataBean posDataBean);

    List<StockVo> getRunsaPosStock(String storeCode, String goodsNo);

    /**验证浪沙调拨单是否未生成微销*/
    boolean unusedTransferList(String deliveryStoreCode, String receivedStoreCode, String listNo);

    /**取浪沙调拨单*/
    RunsaPosTransfer getTransferList(String listNo);

}
