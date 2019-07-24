package com.tlg.storehelper.service;

import com.tlg.storehelper.entity.second.Inventory;
import com.tlg.storehelper.entity.second.InventoryDetail;

import java.util.List;

public interface BusinessService {

    Inventory getInventoryById(long id);
    InventoryDetail getInventoryDetailById(long id);
    List<InventoryDetail> getInventoryDetailsByParentId(long pid);

    String registerLoginAndGetToken(String username);
    String getToken(String username);

    /**
     * @return 1:全部通过；-1:用户名无效；-2:令牌无效
     */
    int checkUserToken(String username, String token);
}
