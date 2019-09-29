package com.tlg.storehelper.service;

import com.tlg.storehelper.entity.ds2.Inventory;
import com.tlg.storehelper.entity.ds2.InventoryDetail;
import com.tlg.storehelper.pojo.InventoryEntity;

import java.util.List;

public interface BusinessService {

    Inventory getInventoryById(String id);
    InventoryDetail getInventoryDetailById(String id);
    List<InventoryDetail> getInventoryDetailsByParentId(String pid);

    String uploadInventory(InventoryEntity inventoryEntity);


    boolean checkUserExist(String jobNumber, String id6);
    String getStoreDynamicPwd(String storeCode);
    String getStoreDynamicPwd(String storeCode, String ip);

    String registerLoginAndGetToken(String username);
    String getToken(String username);

    /**
     * @return 1:全部通过；-1:用户名无效；-2:令牌无效
     */
    int checkUserToken(String username, String token);
}
