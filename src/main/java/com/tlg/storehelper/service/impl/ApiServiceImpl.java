package com.tlg.storehelper.service.impl;

import com.nec.lib.utils.StringUtil;
import com.tlg.storehelper.entity.ds1.ErpUser;
import com.tlg.storehelper.entity.ds1.Goods;
import com.tlg.storehelper.entity.ds3.BestSelling;
import com.tlg.storehelper.entity.ds3.Collocation;
import com.tlg.storehelper.pojo.*;
import com.tlg.storehelper.service.ApiService;
import com.tlg.storehelper.service.BusinessService;
import com.tlg.storehelper.service.ErpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApiServiceImpl implements ApiService {

    @Autowired
    private ErpService erpService;
    @Autowired
    private BusinessService businessService;

    @Override
    public SimpleEntity<String> loginValidation(String username, String password) {
        SimpleEntity<String> entity = new SimpleEntity();
        ErpUser user = null;
        if(username!=null && password!=null) {
            try {
                //password = XxteaUtil.encryptBase64String(password, "UTF-8", "Passwd-Regent");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(username.length() == 6 && StringUtil.isNumeric(username)) {  //员工编号
                if(businessService.checkUserExist(username, password)) {
                    user = new ErpUser();
                    user.userAccount = username;
                    user.username = username;
                    user.userId = username;
                    user.enabled = 1;
                    user.password = password;
                    user.type = 1;
                }
            } else {  //店铺账号
                String pwd = businessService.getStoreDynamicPwd(username);
                if(pwd != null)
                    user = erpService.getUserByAccount(username);
                if(user != null)
                    user.password = pwd;
            }
        }
        if(user != null && user.password.equals(password)) {
            if(user.type == 2)
                entity.resultList.add(user.userAccount);
            else {
                for(ErpUser eachUser: erpService.getAllStoreUsers()) {
                    if(eachUser.userAccount.length() == 3)
                        entity.resultList.add(eachUser.userAccount);
                }
            }
            String token = businessService.registerLoginAndGetToken(username);
            entity.resultMap.put("token", token);
            entity.setSuccessfulMessage("登录成功");
        } else {
            entity.code = 1005;
            entity.msg = "用户名密码错误";
        }
        return entity;
    }

    @Override
    public SimpleEntity<String> getGoodsBarcodeList(String lastModDate) {
        SimpleEntity<String> entity = erpService.getAllSimpleGoodsBarcodes(lastModDate);
        if(entity.resultList.size() == 0) {
            entity.code = 200;
            entity.msg = "商品资料已经是最新";
        } else
            entity.setSuccessfulMessage("商品条码取得成功");

        return entity;
    }

    @Override
    public BaseResponseEntity uploadInventory(InventoryEntity inventoryEntity) {
        String result = businessService.uploadInventory(inventoryEntity);
        BaseResponseEntity entity = new BaseResponseEntity();
        if(result.equals(""))
            entity.setSuccessfulMessage("上传成功");
        else {
            entity.code = 2005;
            entity.msg = "盘点单保存失败";
        }
        return entity;
    }

    @Override
    public CollocationEntity getCollocation(String goodsNo) {
        final String[] stockInfo = new String[]{"无","少","有","多"};
        Goods goods = erpService.getGoods(goodsNo);
        CollocationEntity collocationEntity = new CollocationEntity();
        if(goods == null) {
            collocationEntity.setCode(2003);
            collocationEntity.setMsg("货号/条码不存在");
            return collocationEntity;
        }
        collocationEntity.goodsNo = goodsNo;
        collocationEntity.goodsName = goods.goodsName;
        collocationEntity.price = goods.price;
        collocationEntity.pic = goods.pic;

        List<Collocation> list = erpService.getCollocation(goodsNo);
        for(Collocation collocation: list) {
            CollocationEntity.DetailBean detailBean = new CollocationEntity.DetailBean();
            detailBean.goodsNo = collocation.goodsNo;
            detailBean.info = "[" + collocation.frequency + "] " + (collocation.stock<4 ? stockInfo[collocation.stock] : "stockInfo[3]");
            detailBean.frequency = collocation.frequency;
            detailBean.pic = collocation.pic;
            collocationEntity.detail.add(detailBean);
        }
        collocationEntity.setSuccessfulMessage("搭配获取成功");
        return collocationEntity;
    }

    @Override
    public SimpleListPageEntity<GoodsSimpleVo> getBestSelling(String storeCode, String dimension, int page) {
        int recordCount = erpService.getBestSellingCount(storeCode, dimension);
        int recordPerPage = 10;
        SimpleListPageEntity<GoodsSimpleVo> simpleListPageEntity = new SimpleListPageEntity<GoodsSimpleVo>(page, (int)Math.ceil((double)recordCount/recordPerPage), recordPerPage, recordCount);
        List<BestSelling> list = erpService.getBestSelling(storeCode, dimension, recordPerPage, page);
        for(BestSelling bestSelling: list) {
            simpleListPageEntity.result.add(new GoodsSimpleVo(bestSelling.goodsNo, "", bestSelling.price, String.valueOf(bestSelling.quantity), bestSelling.pic));
        }
        simpleListPageEntity.setSuccessfulMessage("畅销款获取成功");
        return simpleListPageEntity;
    }

    @Override
    public SimpleEntity<StockVo> getStoreStock(String storeCode, String goodsNo) {
        SimpleEntity<StockVo> simpleEntity = new SimpleEntity();
        simpleEntity.resultMap.put("goodsNo", "12345678");
        simpleEntity.resultMap.put("goodsName", "ABCDEFG");

        StockVo vo1 = new StockVo("M", 20, 5,80);
        vo1.storeList.add("M04");vo1.storeList.add("M35");vo1.storeList.add("M42");
        simpleEntity.resultList.add(vo1);
        StockVo vo2 = new StockVo("XL", 30, 6,90);
        vo2.storeList.add("M02");vo2.storeList.add("M04");vo2.storeList.add("M42");vo2.storeList.add("M32");vo2.storeList.add("M75");vo2.storeList.add("M32");
        simpleEntity.resultList.add(vo2);
        simpleEntity.setSuccessfulMessage("库存获取成功");
        return simpleEntity;
    }

    public ShopHistoryEntity getMembershipShopHistory(String membershipId, String storeCode) {
        ShopHistoryEntity shopHistoryEntity = new ShopHistoryEntity();
        shopHistoryEntity.membershipCardId = "123456";
        shopHistoryEntity.name = "张三";
        shopHistoryEntity.mobile = "19901234567";
        shopHistoryEntity.yearExpenditure = 16890;
        shopHistoryEntity.totalExpenditure = 68900;
        shopHistoryEntity.setSuccessfulMessage("会员信息获取成功");
        return shopHistoryEntity;
    }

    @Override
    public SimpleListPageEntity<ShopHistoryDetailVo> getMembershipShopHistoryDetail(String membershipId, String storeCode, int page) {
        int recordCount = 30;
        int recordPerPage = 10;
        SimpleListPageEntity<ShopHistoryDetailVo> simpleListPageEntity = new SimpleListPageEntity<ShopHistoryDetailVo>(page, (int)Math.ceil((double)recordCount/recordPerPage), recordPerPage, recordCount);
        simpleListPageEntity.setSuccessfulMessage("会员信息获取成功");

        for(int i=page*10+1; i<=(page+1)*10; i++) {
            ShopHistoryDetailVo shopVo = new ShopHistoryDetailVo();
            shopVo.shopDate = "2019-01-31";
            shopVo.salesListCode = "20190100"+i;
            shopVo.quantity = i;
            shopVo.amount = 5400;
            simpleListPageEntity.result.add(shopVo);
            for(int j=1; j<=i; j++) {
                ShopHistoryDetailVo.ShopItemVo shopItemVo = new ShopHistoryDetailVo.ShopItemVo();
                shopItemVo.goodsNo = "LCDP04VBY344B11";
                shopItemVo.size = "XL";
                shopItemVo.goodsName = "时尚外套-" + i + "-" + j;
                shopItemVo.quantity = j;
                shopItemVo.price = 1000 * j;
                shopItemVo.discount = 0.9f;
                shopItemVo.realPrice = 900 * j;
                shopItemVo.amount = 900 * j;
                shopItemVo.sales = "张某某";
                shopVo.shopItemList.add(shopItemVo);
            }
        }
        return simpleListPageEntity;
    }

    /*
    @Override
    public GoodsBarcodeEntity getGoodsBarcodeList(String lastModDate) {
        GoodsBarcodeEntity entity = erpService.getLastestGoodsBarcodes(lastModDate);
        entity.setSuccessfulMessage("商品取得成功");

        return entity;
    }*/
}
