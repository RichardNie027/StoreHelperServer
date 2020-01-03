package com.tlg.storehelper.service;

import com.tlg.storehelper.controller.ApiController;
import com.tlg.storehelper.entity.ds1.*;
import com.tlg.storehelper.entity.ds3.RunsaPosTransfer;
import com.tlg.storehelper.entity.ds1.StoreSelling;
import com.tlg.storehelper.pojo.*;

import java.util.List;

public interface ErpService {

    List<ErpUser> getAllStoreUsers();

    ErpUser getUserByAccount(String userAccount);

    List<ErpStore> getAllStore();

    boolean getGoodsNeedUpdate(String lastModDate);

    List<String> getAllSimpleGoodsBarcodes(String lastModDate);

    //GoodsBarcodeEntity getLastestGoodsBarcodes(String lastModDate);
    List<String> getAllSimpleGoods(String lastModDate);

    List<String> getAllGoodsNoInSameStyle(String goodsNo);

    Goods getGoods(String goodsNo);

    boolean queryGoodsPicture(String goodsNo);

    String getGoodsPictureName(String goodsNo);

    /**返回商品热度*/
    List<GoodsPopularityVo> getGoodsPopularity(String storeCode);

    /**货号的推荐款*/
    List<Selling> getCollocation(String goodsNo, String storeCodes, String dimension);

    /**
     * 商品成对搭配统计
     * @param result 返回结果集
     * @param storeCodes 单字符=品牌首字母；逗号分隔的店编字符串=多店；null/empty=无返回
     * @param dimension 维 WEEK/MONTH/2WEEK/2MONTH/3WEEK/3MONTH
     * @param salesCode 导购工号，可null
     * @return 总记录数
     */
    int getPairCollocation(List<PairCollocation> result, String storeCodes, String dimension, String salesCode, int pageSize, int page);

    /**
     * 畅销数量的记录数
     * @param storeCodes 单字符=品牌首字母；逗号分隔的店编字符串=多店；null/empty=无返回
     * @param dimension 维 WEEK/MONTH/2WEEK/2MONTH/3WEEK/3MONTH
     * @param salesCode 导购工号，可为null/emtpy
     * @param floorNumber 最小的销量统计
     */
    int getBestSellingCount(String storeCodes, String dimension, String salesCode, int floorNumber);

    /**
     * 畅销清单
     * @param storeCodes 单字符=品牌首字母；逗号分隔的店编字符串=多店；empty=无返回
     * @param dimension 维 WEEK/MONTH/2WEEK/2MONTH/3WEEK/3MONTH
     * @param salesCode 导购工号，可为null/empty
     * @param floorNumber 最小的销量统计
     */
    List<Selling> getBestSelling(String storeCodes, String dimension, String salesCode, int floorNumber, int pageSize, int page);

    /**
     * 导购列表
     * @param storeCode
     * @return
     */
    List<String> getSalesList(String storeCode);

    /**
     * 导购销售排名的记录数
     * @param storeCodes
     * @param dimension
     * @return
     */
    int getBestSalesSellingCount(String storeCodes, String dimension);

    /**
     * 导购销售排名
     * @param storeCodes
     * @param dimension
     * @param pageSize
     * @param page
     * @return
     */
    List<SalesSelling> getBestSalesSelling(String storeCodes, String dimension, int pageSize, int page);

    /**返回每个货号的总库存*/
    List<KV<String,Integer>> getTotalStock(List<String> goodsNoList, boolean includeSameStyle);

    /**返回货号周/月的销量*/
    List<StoreSelling> getSelling(String dimension, String goodsNo, boolean includeSameStyle);

    /**会员信息*/
    List<MembershipVo> getMembership(String membershipId);

    /**会员消费记录的记录数*/
    int getShopHistoryCount(String membershipId);

    /**会员消费记录*/
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
