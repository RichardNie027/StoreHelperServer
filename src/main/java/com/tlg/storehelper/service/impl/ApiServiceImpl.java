package com.tlg.storehelper.service.impl;

import com.nec.lib.utils.XxteaUtil;
import com.tlg.storehelper.entity.main.RegentUser;
import com.tlg.storehelper.pojo.BaseResponseEntity;
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

    /*
    @Override
    public GoodsBarcodeEntity getGoodsBarcodeList(String lastModDate) {
        GoodsBarcodeEntity entity = regentService.getLastestGoodsBarcodes(lastModDate);
        entity.setSuccessfulMessage("商品取得成功");

        return entity;
    }*/
}
