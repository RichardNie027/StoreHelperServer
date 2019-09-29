package com.tlg.storehelper.service.impl;

import com.nec.lib.utils.StringUtil;
import com.tlg.storehelper.entity.ds1.ErpUser;
import com.tlg.storehelper.entity.ds1.Goods;
import com.tlg.storehelper.entity.ds1.Membership;
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
    public SimpleListMapEntity<String> loginValidation(String username, String password) {
        SimpleListMapEntity<String> entity = new SimpleListMapEntity();
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
                entity.list.add(user.userAccount);
            else {
                for(ErpUser eachUser: erpService.getAllStoreUsers()) {
                    if(eachUser.userAccount.length() == 3)
                        entity.list.add(eachUser.userAccount);
                }
            }
            String token = businessService.registerLoginAndGetToken(username);
            entity.map.put("token", token);
            entity.setSuccessfulMessage("登录成功");
        } else {
            entity.code = 1005;
            entity.msg = "用户名密码错误";
        }
        return entity;
    }

    @Override
    public SimpleListMapEntity<String> getGoodsBarcodeList(String lastModDate) {
        SimpleListMapEntity<String> entity = erpService.getAllSimpleGoodsBarcodes(lastModDate);
        if(entity.list.size() == 0) {
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
    public SimplePageListEntity<GoodsSimpleVo> getBestSelling(String storeCode, String dimension, int page) {
        int recordCount = erpService.getBestSellingCount(storeCode, dimension);
        int pageSize = 10;
        SimplePageListEntity<GoodsSimpleVo> entity = new SimplePageListEntity<GoodsSimpleVo>(page, (int)Math.ceil((double)recordCount/pageSize), pageSize, recordCount);
        List<BestSelling> list = erpService.getBestSelling(storeCode, dimension, pageSize, page);
        for(BestSelling bestSelling: list) {
            entity.list.add(new GoodsSimpleVo(bestSelling.goodsNo, "", bestSelling.price, String.valueOf(bestSelling.quantity), bestSelling.pic));
        }
        entity.setSuccessfulMessage("畅销款获取成功");
        return entity;
    }

    @Override
    public SimpleListMapEntity<StockVo> getStoreStock(String storeCode, String goodsNo) {
        SimpleListMapEntity<StockVo> entity = new SimpleListMapEntity();
        entity.map.put("goodsNo", goodsNo);
        Goods goods = erpService.getGoods(goodsNo);
        if(goods!=null)
            entity.map.put("goodsName", goods.goodsName);
        else
            entity.map.put("goodsName", "");
        entity.list.addAll(erpService.getRunsaPosStock(storeCode, goodsNo));

        entity.setSuccessfulMessage("库存获取成功");
        return entity;
    }

    public SimpleListEntity<MembershipVo> getMembership(String membershipId, String storeCode) {
        SimpleListEntity<MembershipVo> result = new SimpleListEntity();
        List<MembershipVo> membershipList = erpService.getMembership(membershipId);
        if(membershipList == null || membershipList.size() == 0) {
            result.code = 2001;
            result.msg = "会员未识别";
            return result;
        }
        result.list.addAll(membershipList);
        result.setSuccessfulMessage("会员信息获取成功");
        return result;
    }

    @Override
    public SimplePageListEntity<ShopHistoryVo> getMembershipShopHistory(String membershipId, String storeCode, int page) {
        int recordCount = erpService.getShopHistoryCount(membershipId);
        int pageSize = 10;
        List<ShopHistoryVo> list = erpService.getShopHistory(membershipId, pageSize, page);
        SimplePageListEntity<ShopHistoryVo> entity = new SimplePageListEntity<ShopHistoryVo>(page, (int)Math.ceil((double)recordCount/pageSize), pageSize, recordCount);
        entity.list.addAll(list);
        entity.setSuccessfulMessage("会员信息获取成功");
        return entity;
    }

    /*
    @Override
    public GoodsBarcodeEntity getGoodsBarcodeList(String lastModDate) {
        GoodsBarcodeEntity entity = erpService.getLastestGoodsBarcodes(lastModDate);
        entity.setSuccessfulMessage("商品取得成功");

        return entity;
    }*/
}
