package com.tlg.storehelper.service.impl;

import com.nec.lib.utils.XxteaUtil;
import com.tlg.storehelper.entity.main.ErpUser;
import com.tlg.storehelper.entity.main.Goods;
import com.tlg.storehelper.entity.third.BestSelling;
import com.tlg.storehelper.entity.third.Collocation;
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
                password = XxteaUtil.encryptBase64String(password, "UTF-8", "Passwd-Regent");
                password = "";
            } catch (Exception e) {
                e.printStackTrace();
            }
            user = erpService.getUserByAccount(username);
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

    /*
    @Override
    public GoodsBarcodeEntity getGoodsBarcodeList(String lastModDate) {
        GoodsBarcodeEntity entity = erpService.getLastestGoodsBarcodes(lastModDate);
        entity.setSuccessfulMessage("商品取得成功");

        return entity;
    }*/
}
