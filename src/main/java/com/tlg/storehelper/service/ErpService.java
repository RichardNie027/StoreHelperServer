package com.tlg.storehelper.service;

import com.tlg.storehelper.controller.ApiController;
import com.tlg.storehelper.entity.ds1.ErpUser;
import com.tlg.storehelper.entity.ds1.Goods;
import com.tlg.storehelper.entity.ds3.RunsaPosTransfer;
import com.tlg.storehelper.entity.ds3.BestSelling;
import com.tlg.storehelper.entity.ds3.Collocation;
import com.tlg.storehelper.entity.ds3.StoreSelling;
import com.tlg.storehelper.pojo.*;

import java.util.List;

public interface ErpService {

    List<ErpUser> getAllStoreUsers();

    ErpUser getUserByAccount(String userAccount);

    boolean getGoodsNeedUpdate(String lastModDate);

    List<String> getAllSimpleGoodsBarcodes(String lastModDate);

    //GoodsBarcodeEntity getLastestGoodsBarcodes(String lastModDate);
    List<String> getAllSimpleGoods(String lastModDate);

    List<String> getAllGoodsNoInSameStyle(String goodsNo);

    Goods getGoods(String goodsNo);

    boolean queryGoodsPicture(String goodsNo);

    String getGoodsPictureName(String goodsNo);

    List<GoodsPopularityVo> getGoodsPopularity(String storeCode);

    List<Collocation> getCollocation(String goodsNo);

    int getBestSellingCount(String storeCode, String dimension);

    List<BestSelling> getBestSelling(String storeCode, String dimension, int pageSize, int page);

    List<StoreSelling> getStoreSelling(String dimension, String goodsNo, boolean includeSameStyle);

    List<MembershipVo> getMembership(String membershipId);

    int getShopHistoryCount(String membershipId);

    List<ShopHistoryVo> getShopHistory(String membershipId,  int pageSize, int page);

    /**保存浪沙Pos数据*/
    void saveRunsaPosData(ApiController.RunsaPosDataBean posDataBean);

    List<GoodsSizePsiVo> getRunsaPosStock(String storeCode, String goodsNo);

    /**验证浪沙调拨单是否未生成微销*/
    boolean unusedTransferList(String deliveryStoreCode, String receivedStoreCode, String listNo);

    /**取浪沙调拨单*/
    RunsaPosTransfer getTransferList(String listNo);

    /**条码最新修改时间*/
    String getGoodsBarcodeLastModDate();
}
