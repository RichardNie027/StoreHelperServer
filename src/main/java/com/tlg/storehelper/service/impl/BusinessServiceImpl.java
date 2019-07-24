package com.tlg.storehelper.service.impl;

import com.nec.lib.utils.DateUtil;
import com.nec.lib.utils.XxteaUtil;
import com.tlg.storehelper.dao.second.InventoryDetailMapper;
import com.tlg.storehelper.dao.second.InventoryMapper;
import com.tlg.storehelper.dao.second.LoginMgrMapper;
import com.tlg.storehelper.entity.second.Inventory;
import com.tlg.storehelper.entity.second.InventoryDetail;
import com.tlg.storehelper.entity.second.LoginMgr;
import com.tlg.storehelper.service.BusinessService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BusinessServiceImpl implements BusinessService {

    public static Map<String, String> loginUsers = new HashMap();

    @Autowired
    private InventoryMapper inventoryMapper;
    @Autowired
    private InventoryDetailMapper inventoryDetailMapper;
    @Autowired
    private LoginMgrMapper loginMgrMapper;

    @Override
    public Inventory getInventoryById(long id) {
        return inventoryMapper.selectById(id);
    }

    @Override
    public InventoryDetail getInventoryDetailById(long id) {
        return inventoryDetailMapper.selectById(id);
    }

    @Override
    public List<InventoryDetail> getInventoryDetailsByParentId(long pid) {
        return inventoryDetailMapper.selectByParentId(pid);
    }

    @Override
    public String registerLoginAndGetToken(String username) {
        String newTokenKey = RandomStringUtils.randomAlphanumeric(6);
        String loginTime = DateUtil.toStr(new Date());
        LoginMgr loginMgr = loginMgrMapper.selectById(username);
        if(loginMgr != null) {
            loginMgr.login_time = loginTime;
            loginMgr.token_key = newTokenKey;
            loginMgrMapper.update(loginMgr);
        } else {
            loginMgr = new LoginMgr();
            loginMgr.username = username;
            loginMgr.login_time = loginTime;
            loginMgr.token_key = newTokenKey;
            loginMgrMapper.insert(loginMgr);
        }
        try {
            return XxteaUtil.encryptBase64String("store_helper_" + username, "UTF-8", newTokenKey);
        } catch (Exception e) {}
        return newTokenKey;
    }

    @Override
    public String getToken(String username) {
        LoginMgr loginMgr = loginMgrMapper.selectById(username);
        String tokenKey = "";
        if(loginMgr != null) {
            tokenKey = loginMgr.token_key;
            try {
                return XxteaUtil.encryptBase64String("store_helper_" + username, "UTF-8", tokenKey);
            } catch (Exception e) {}
        }
        return tokenKey;
    }

    @Override
    public int checkUserToken(String username, String token) {
        if(username==null || username.isEmpty())
            return -1;
        if(token==null || token.isEmpty())
            return -2;
        String token1 = loginUsers.get(username);
        if(token1 == null || token1.isEmpty()) {
            String token2 = getToken(username);
            if(token2.isEmpty())
                return -1;  //用户名无效
            if(!token2.equals(token))
                return -2;  //令牌无效
            loginUsers.put(username, token2);
        }
        return 1;   //全部通过
    }


}
