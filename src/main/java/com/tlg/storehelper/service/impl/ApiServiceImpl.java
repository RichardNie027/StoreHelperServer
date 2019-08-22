package com.tlg.storehelper.service.impl;

import com.nec.lib.utils.StringUtil;
import com.nec.lib.utils.XxteaUtil;
import com.tlg.storehelper.entity.main.RegentUser;
import com.tlg.storehelper.pojo.BaseResponseEntity;
import com.tlg.storehelper.pojo.CollocationEntity;
import com.tlg.storehelper.pojo.InventoryEntity;
import com.tlg.storehelper.pojo.SimpleEntity;
import com.tlg.storehelper.service.ApiService;
import com.tlg.storehelper.service.BusinessService;
import com.tlg.storehelper.service.RegentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApiServiceImpl implements ApiService {

    @Autowired
    private RegentService regentService;
    @Autowired
    private BusinessService businessService;

    @Override
    public SimpleEntity<String> loginValidation(String username, String password) {
        SimpleEntity<String> entity = new SimpleEntity();
        RegentUser user = null;
        if(username!=null && password!=null) {
            try {
                password = XxteaUtil.encryptBase64String(password, "UTF-8", "Passwd-Regent");
            } catch (Exception e) {
                e.printStackTrace();
            }
            user = regentService.getUserByAccount(username);
        }
        if(user != null && user.password.equals(password)) {
            if(user.type == 2)
                entity.result_list.add(user.userAccount);
            else {
                for(RegentUser eachUser: regentService.getAllStoreUsers()) {
                    entity.result_list.add(eachUser.userAccount);
                }
            }
            String token = businessService.registerLoginAndGetToken(username);
            entity.result_map.put("token", token);
            entity.setSuccessfulMessage("登录成功");
        } else {
            entity.code = 1005;
            entity.msg = "用户名密码错误";
        }
        return entity;
    }

    @Override
    public SimpleEntity<String> getGoodsBarcodeList(String lastModDate) {
        SimpleEntity<String> entity = regentService.getAllSimpleGoodsBarcodes(lastModDate);
        if(entity.result_list.size() == 0) {
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
        String[] goodsNos = {"9B81809","9B81812","9B81882","9B81912","9B83506","LC1DK02KPR327U1"};
        boolean goodsNoIsOk = false;
        OUTER:
        for(int i : new int[]{0,1,2,3,4}) {
            for(String gno: goodsNos) {
                String truncatedGoodsNo = StringUtil.truncateRight(goodsNo, i);
                if(truncatedGoodsNo.equalsIgnoreCase(gno)) {
                    goodsNo = truncatedGoodsNo;
                    goodsNoIsOk = true;
                    break OUTER;
                }
            }
        }

        CollocationEntity collocationEntity = new CollocationEntity();
        if(!goodsNoIsOk) {
            collocationEntity.setCode(2003);
            collocationEntity.setMsg("货号/条码不存在");
            return collocationEntity;
        }
        collocationEntity.goodsNo = goodsNo;
        collocationEntity.goodsName = "测试商品-时尚女衣";
        collocationEntity.price = 3888;
        collocationEntity.pic = goodsNo+".jpg";
        for(int i=0; i<6; i++) {
            CollocationEntity.DetailBean detailBean = new CollocationEntity.DetailBean();
            detailBean.goodsNo = goodsNos[i];
            detailBean.frequency = 30 + i;
            detailBean.pic = detailBean.goodsNo + ".jpg";
            collocationEntity.detail.add(detailBean);
        }
        collocationEntity.setSuccessfulMessage("搭配获取成功");
        return collocationEntity;
    }

    /*
    @Override
    public GoodsBarcodeEntity getGoodsBarcodeList(String lastModDate) {
        GoodsBarcodeEntity entity = regentService.getLastestGoodsBarcodes(lastModDate);
        entity.setSuccessfulMessage("商品取得成功");

        return entity;
    }*/
}
