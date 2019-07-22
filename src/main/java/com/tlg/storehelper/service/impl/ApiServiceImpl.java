package com.tlg.storehelper.service.impl;

import com.nec.lib.utils.XxteaUtil;
import com.tlg.storehelper.entity.RegentUser;
import com.tlg.storehelper.pojo.GoodsBarcodeEntity;
import com.tlg.storehelper.pojo.SimpleListEntity;
import com.tlg.storehelper.service.ApiService;
import com.tlg.storehelper.service.RegentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApiServiceImpl implements ApiService {

    @Autowired
    private RegentService regentService;

    @Override
    public SimpleListEntity<String> loginValidation(String username, String password) {
        SimpleListEntity<String> entity = new SimpleListEntity();
        RegentUser user = null;
        if(username!=null && password!=null) {
            try {
                password = XxteaUtil.encryptBase64String(password, "UTF-8", "Passwd-Regent");
            } catch (Exception e) {
                e.printStackTrace();
            }
            user = regentService.getUserByAccount(username);
        }
        if(user != null && user.getPassword().equals(password)) {
            if(user.getType() == 2)
                entity.result.add(user.getUserAccount());
            else {
                for(RegentUser eachUser: regentService.getAllStoreUsers()) {
                    entity.result.add(eachUser.getUserAccount());
                }
            }
            entity.setSuccessfulMessage("登录成功");
        } else {
            entity.code = 1005;
            entity.msg = "用户名密码错误";
        }
        return entity;
    }

    @Override
    public SimpleListEntity<String> getGoodsBarcodeList() {
        SimpleListEntity<String> entity = regentService.getAllSimpleGoodsBarcodes();
        entity.setSuccessfulMessage("商品条码取得成功");

        return entity;
    }

    @Override
    public GoodsBarcodeEntity getGoodsBarcodeList(String lastModDate) {
        GoodsBarcodeEntity entity = regentService.getLastestGoodsBarcodes(lastModDate);
        entity.setSuccessfulMessage("商品取得成功");

        return entity;
    }
}
