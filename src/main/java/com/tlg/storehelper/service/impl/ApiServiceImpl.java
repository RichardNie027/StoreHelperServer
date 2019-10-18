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
    public SimpleListMapResponseVo<String> loginValidation(String username, String password) {
        SimpleListMapResponseVo<String> responseVo = new SimpleListMapResponseVo();
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
                responseVo.list.add(user.userAccount);
            else {
                for(ErpUser eachUser: erpService.getAllStoreUsers()) {
                    if(eachUser.userAccount.length() == 3)
                        responseVo.list.add(eachUser.userAccount);
                }
            }
            String token = businessService.registerLoginAndGetToken(username);
            responseVo.map.put("token", token);
            responseVo.setSuccessfulMessage("登录成功");
        } else {
            responseVo.code = 1005;
            responseVo.msg = "用户名密码错误";
        }
        return responseVo;
    }

    @Override
    public BaseResponseVo getGoodsBarcodeNeedRefresh(String lastModDate) {
        BaseResponseVo responseVo = new BaseResponseVo();
        if(erpService.getGoodsBarcodeNeedRefresh(lastModDate))
            responseVo.setSuccessfulMessage("1");
        else
            responseVo.setSuccessfulMessage("0");
        return responseVo;
    }

    @Override
    public GoodsInfoResponseVo getGoodsList(String lastModDate) {
        GoodsInfoResponseVo responseVo = new GoodsInfoResponseVo();
        String freshLastModDate = erpService.getGoodsBarcodeLastModDate();
        if(freshLastModDate.compareTo(lastModDate) > 0) {
            responseVo.lastModDate = freshLastModDate;
            responseVo.goodsList = erpService.getAllSimpleGoods(lastModDate);
            responseVo.goodsBarcodeList = erpService.getAllSimpleGoodsBarcodes(lastModDate);
        } else
            responseVo.lastModDate = "";
        if(responseVo.goodsList.isEmpty() && responseVo.goodsBarcodeList.isEmpty())
            responseVo.setSuccessfulMessage("商品资料已经是最新");
        else
            responseVo.setSuccessfulMessage("商品条码获取成功");
        return responseVo;
    }

    @Override
    public SimpleListResponseVo<GoodsPopularityVo> getGoodsPopularity(String storeCode) {
        SimpleListResponseVo<GoodsPopularityVo> responseVo = new SimpleListResponseVo<GoodsPopularityVo>();
        responseVo.list = erpService.getGoodsPopularity(storeCode);
        responseVo.setSuccessfulMessage("商品热度获取成功");
        return responseVo;
    }

    @Override
    public BaseResponseVo uploadInventory(InventoryEntity inventoryEntity) {
        String result = businessService.uploadInventory(inventoryEntity);
        BaseResponseVo responseVo = new BaseResponseVo();
        if(result.equals(""))
            responseVo.setSuccessfulMessage("上传成功");
        else {
            responseVo.code = 2005;
            responseVo.msg = "盘点单保存失败";
        }
        return responseVo;
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
    public SimplePageListResponseVo<GoodsSimpleVo> getBestSelling(String storeCode, String dimension, int page) {
        int recordCount = erpService.getBestSellingCount(storeCode, dimension);
        int pageSize = 10;
        SimplePageListResponseVo<GoodsSimpleVo> responseVo = new SimplePageListResponseVo<GoodsSimpleVo>(page, (int)Math.ceil((double)recordCount/pageSize), pageSize, recordCount);
        List<BestSelling> list = erpService.getBestSelling(storeCode, dimension, pageSize, page);
        for(BestSelling bestSelling: list) {
            responseVo.list.add(new GoodsSimpleVo(bestSelling.goodsNo, "", bestSelling.price, String.valueOf(bestSelling.quantity), bestSelling.pic));
        }
        responseVo.setSuccessfulMessage("畅销款获取成功");
        return responseVo;
    }

    @Override
    public SimpleListMapResponseVo<StockVo> getStoreStock(String storeCode, String goodsNo) {
        SimpleListMapResponseVo<StockVo> responseVo = new SimpleListMapResponseVo();
        responseVo.map.put("goodsNo", goodsNo);
        Goods goods = erpService.getGoods(goodsNo);
        if(goods!=null)
            responseVo.map.put("goodsName", goods.goodsName);
        else
            responseVo.map.put("goodsName", "");
        responseVo.list.addAll(erpService.getRunsaPosStock(storeCode, goodsNo));

        responseVo.setSuccessfulMessage("库存获取成功");
        return responseVo;
    }

    public SimpleListResponseVo<MembershipVo> getMembership(String membershipId, String storeCode) {
        SimpleListResponseVo<MembershipVo> responseVo = new SimpleListResponseVo();
        List<MembershipVo> membershipList = erpService.getMembership(membershipId);
        if(membershipList == null || membershipList.size() == 0) {
            responseVo.code = 2001;
            responseVo.msg = "会员未识别";
            return responseVo;
        }
        responseVo.list.addAll(membershipList);
        responseVo.setSuccessfulMessage("会员信息获取成功");
        return responseVo;
    }

    @Override
    public SimplePageListResponseVo<ShopHistoryVo> getMembershipShopHistory(String membershipId, String storeCode, int page) {
        int recordCount = erpService.getShopHistoryCount(membershipId);
        int pageSize = 10;
        List<ShopHistoryVo> list = erpService.getShopHistory(membershipId, pageSize, page);
        SimplePageListResponseVo<ShopHistoryVo> responseVo = new SimplePageListResponseVo<ShopHistoryVo>(page, (int)Math.ceil((double)recordCount/pageSize), pageSize, recordCount);
        responseVo.list.addAll(list);
        responseVo.setSuccessfulMessage("会员信息获取成功");
        return responseVo;
    }

    /*
    @Override
    public GoodsBarcodeEntity getGoodsBarcodeList(String lastModDate) {
        GoodsBarcodeEntity entity = erpService.getLastestGoodsBarcodes(lastModDate);
        entity.setSuccessfulMessage("商品取得成功");

        return entity;
    }*/
}
