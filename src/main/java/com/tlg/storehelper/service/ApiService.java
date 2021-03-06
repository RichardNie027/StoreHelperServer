package com.tlg.storehelper.service;

import com.tlg.storehelper.entity.ds1.ErpStore;
import com.tlg.storehelper.entity.ds1.SalesSelling;
import com.tlg.storehelper.pojo.*;

public interface ApiService {

    /////////////////////////////////////////////////////////////
    ////////////////////       Login     ////////////////////////
    /////////////////////////////////////////////////////////////
    SimpleListMapResponseVo<String> loginValidation(String username, String password);

    /////////////////////////////////////////////////////////////
    ////////////////////       Store     ////////////////////////
    /////////////////////////////////////////////////////////////

    SimpleListResponseVo<ErpStore> getStoreList();

    /////////////////////////////////////////////////////////////
    ////////////////////       Goods     ////////////////////////
    /////////////////////////////////////////////////////////////
    BaseResponseVo getGoodsNeedUpdate(String lastModDate);
    GoodsInfoResponseVo getGoodsList(String lastModDate);
    SimpleListResponseVo<GoodsPopularityVo> getGoodsPopularity(String storeCode);
    //GoodsBarcodeEntity getGoodsBarcodeList(String lastModDate);
    SimpleListResponseVo<String> getGoodsClassList(String brandKey);
    SimplePageListResponseVo<String> getGoodsNoListByClass(String brandCodes, String yearCode, String seasonCode, String classCode, String priceCode, int page, Integer pageSize);

    /////////////////////////////////////////////////////////////
    ////////////////////    Inventory    ////////////////////////
    /////////////////////////////////////////////////////////////
    BaseResponseVo uploadInventory(InventoryEntity inventoryEntity);

    /////////////////////////////////////////////////////////////
    ///////////////////    Collocation    ///////////////////////
    /////////////////////////////////////////////////////////////
    CollocationVo getCollocation(String goodsNo, String storeCodes, String dim);
    SimplePageListResponseVo<CollocationPairVo> getPairCollocation(String storeCodes, String dimension, String salesCode, int page, int pageSize);

    /////////////////////////////////////////////////////////////
    ///////////////////    Sales Lady     ///////////////////////
    /////////////////////////////////////////////////////////////
    SimpleListResponseVo<String> getSalesList(String storeCode);

    /////////////////////////////////////////////////////////////
    ///////////////////    Selling    ///////////////////////
    /////////////////////////////////////////////////////////////
    SimplePageListResponseVo<GoodsSimpleVo> getBestSelling(String storeCodes, String dimension, String salesCode, int floorNumber, String sort, int page, int pageSize);
    SimplePageListResponseVo<SalesSelling> getBestSalesSelling(String storeCodes, String dimension, String sort, int page, int pageSize);

    /////////////////////////////////////////////////////////////
    ///////////////////   StoreSelling    ///////////////////////
    /////////////////////////////////////////////////////////////
    SellingVo getSelling(String dimension, String goodsNo, boolean includeSameStyle);

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
