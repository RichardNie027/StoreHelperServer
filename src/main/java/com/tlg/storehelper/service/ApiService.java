package com.tlg.storehelper.service;

import com.tlg.storehelper.pojo.*;

public interface ApiService {

    /////////////////////////////////////////////////////////////
    ////////////////////       Login     ////////////////////////
    /////////////////////////////////////////////////////////////
    SimpleListMapResponseVo<String> loginValidation(String username, String password);


    /////////////////////////////////////////////////////////////
    ////////////////////       Goods     ////////////////////////
    /////////////////////////////////////////////////////////////
    BaseResponseVo getGoodsNeedUpdate(String lastModDate);
    GoodsInfoResponseVo getGoodsList(String lastModDate);
    SimpleListResponseVo<GoodsPopularityVo> getGoodsPopularity(String storeCode);
    //GoodsBarcodeEntity getGoodsBarcodeList(String lastModDate);

    /////////////////////////////////////////////////////////////
    ////////////////////    Inventory    ////////////////////////
    /////////////////////////////////////////////////////////////
    BaseResponseVo uploadInventory(InventoryEntity inventoryEntity);

    /////////////////////////////////////////////////////////////
    ///////////////////    Collocation    ///////////////////////
    /////////////////////////////////////////////////////////////
    CollocationEntity getCollocation(String goodsNo);

    /////////////////////////////////////////////////////////////
    ///////////////////    BestSelling    ///////////////////////
    /////////////////////////////////////////////////////////////
    SimplePageListResponseVo<GoodsSimpleVo> getBestSelling(String storeCode, String dimension, int page, int pageSize);

    /////////////////////////////////////////////////////////////
    ///////////////////   StoreSelling    ///////////////////////
    /////////////////////////////////////////////////////////////
    SellingVo getStoreSelling(String dimension, String goodsNo, boolean includeSameStyle);

    /////////////////////////////////////////////////////////////
    ///////////////////        PSI        ///////////////////////
    /////////////////////////////////////////////////////////////
    SimpleListResponseVo<GoodsPsiVo> getStorePsi(String storeCode, String goodsNo);

    /////////////////////////////////////////////////////////////
    ///////////////   Membership Shop History ///////////////////
    /////////////////////////////////////////////////////////////
    SimpleListResponseVo<MembershipVo> getMembership(String membershipId, String storeCode);
    SimplePageListResponseVo<ShopHistoryVo> getMembershipShopHistory(String membershipId, String storeCode, int page);


}
