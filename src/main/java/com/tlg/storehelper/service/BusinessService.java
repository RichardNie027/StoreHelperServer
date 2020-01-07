package com.tlg.storehelper.service;

import com.tlg.storehelper.entity.ds2.Inventory;
import com.tlg.storehelper.entity.ds2.InventoryDetail;
import com.tlg.storehelper.pojo.BaseResponseVo;
import com.tlg.storehelper.pojo.InventoryEntity;
import com.tlg.storehelper.pojo.SimpleListResponseVo;

import java.util.List;
import java.util.Map;

public interface BusinessService {

    Inventory getInventoryById(String id);
    InventoryDetail getInventoryDetailById(String id);
    List<InventoryDetail> getInventoryDetailsByParentId(String pid);

    String uploadInventory(InventoryEntity inventoryEntity);

    /**
     * 取得品牌展示的图片数量
     * @param brandKey 品牌首字母
     * @param
     * @return KEY(type类别分组):0品牌介绍 1本季热卖搭配 2本机流行趋势 3面料知识 VALUE(picVer * 1000 + pic's count)
     */
    Map<String, Integer> getBrandPicsCountByVer(String brandKey);
    boolean queryBrandPicture(String brandKey, String type, long idx);

    String getBrandHtml(String brandKey, String type);

    /**
     * 检查用户名密码正确，返回检查结果及有权限的店编、权限码
     * @param jobNumber
     * @param id6
     * @return A|B|C, 用户名密码正确A=1 不正确A=0, B=SQL IN 子句或 LIKE 子句, C=2位一节的权限码
     * C：他店（单品精准库存K0，单品精准销量X0，客单数X1，客单件X2，单品精准销售金额X5，客单价X6）
     */
    String checkUserRights(String jobNumber, String id6);
    String getStoreDynamicPwd(String storeCode);
    String getStoreDynamicPwd(String storeCode, String ip);

    String registerLoginAndGetToken(String username);
    String getToken(String username);

    /**
     * @return 1:全部通过；-1:用户名无效；-2:令牌无效
     */
    int checkUserToken(String username, String token);
}
