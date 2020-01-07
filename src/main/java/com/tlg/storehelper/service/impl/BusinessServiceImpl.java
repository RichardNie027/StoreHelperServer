package com.tlg.storehelper.service.impl;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.nec.lib.utils.BeanUtil;
import com.nec.lib.utils.DateUtil;
import com.nec.lib.utils.NetUtil;
import com.nec.lib.utils.XxteaUtil;
import com.tlg.storehelper.dao.ds2.InventoryDetailMapper;
import com.tlg.storehelper.dao.ds2.InventoryMapper;
import com.tlg.storehelper.dao.ds2.LoginMgrMapper;
import com.tlg.storehelper.dao.ds2.SimpleMapper;
import com.tlg.storehelper.entity.ds2.Inventory;
import com.tlg.storehelper.entity.ds2.InventoryDetail;
import com.tlg.storehelper.entity.ds2.LoginMgr;
import com.tlg.storehelper.pojo.BaseResponseVo;
import com.tlg.storehelper.pojo.InventoryEntity;
import com.tlg.storehelper.pojo.SimpleListResponseVo;
import com.tlg.storehelper.service.BusinessService;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BusinessServiceImpl implements BusinessService {

    public static String TokenPrefix = "store_helper_";

    public static Map<String, String> loginUsers = new HashMap();

    @Autowired
    private InventoryMapper inventoryMapper;
    @Autowired
    private InventoryDetailMapper inventoryDetailMapper;
    @Autowired
    private LoginMgrMapper loginMgrMapper;
    @Autowired
    private SimpleMapper simpleMapper;

    @Value("${properties.business_service.brand_pic_url}")
    private String ResourceUrl;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Inventory getInventoryById(String id) {
        return inventoryMapper.selectById(id);
    }

    @Override
    public InventoryDetail getInventoryDetailById(String id) {
        return inventoryDetailMapper.selectById(id);
    }

    @Override
    public List<InventoryDetail> getInventoryDetailsByParentId(String pid) {
        return inventoryDetailMapper.selectByParentId(pid);
    }

    @Transactional
    @Override
    public String uploadInventory(InventoryEntity inventoryEntity) {
        if(inventoryMapper.selectById(inventoryEntity.id) != null) {
            inventoryDetailMapper.deleteByPid(inventoryEntity.id);
            inventoryMapper.delete(inventoryEntity.id);
        }
        try {
            Inventory inventory = new Inventory();
            BeanUtil.copyPropertiesExclude(inventoryEntity, inventory, new String[] {"detail"});
            inventoryMapper.insert(inventory);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "赋值过程出错";
        }
        for(InventoryEntity.DetailBean bean: inventoryEntity.detail) {
            InventoryDetail inventoryDetail = new InventoryDetail();
            try {
                BeanUtil.copyProperties(bean, inventoryDetail);
                inventoryDetailMapper.insert(inventoryDetail);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return "赋值过程出错";
            }
        }
        logger.info("上传盘点单完成");
        return "";
    }

    @Override
    public Map<String, Integer> getBrandPicsCountByVer(String brandKey) {
        Map<String, Integer> result = new LinkedTreeMap<>();
        List<String> list = simpleMapper.selectBrandPics(brandKey);
        if (list != null) {
            list.forEach(x->{
                String[] arr = x.split("\\|");
                int picVer = Integer.parseInt(arr[1]);
                int picCount = arr[2].split(",").length;
                result.put(arr[0], picVer * 1000 + picCount);
            });
        }
        return result;
    }

    @Override
    public boolean queryBrandPicture(String brandKey, String type, long idx) {
        String resp = NetUtil.request(ResourceUrl + brandKey + "/" + type + "/" + idx, null, "GET", "json");
        BaseResponseVo responseVo = new Gson().fromJson(resp, BaseResponseVo.class);
        return responseVo != null && responseVo.code == 200;
    }

    @Override
    public String getBrandHtml(String brandKey, String type) {
        return simpleMapper.selectHtml(brandKey, type);
    }

    @Override
    public String checkUserRights(String jobNumber, String id6) {
        return simpleMapper.checkUserRights(jobNumber, id6);
    }

    @Override
    public String getStoreDynamicPwd(String storeCode) {
        return simpleMapper.selectStoreDynamicPwd(storeCode);
    }

    @Override
    public String getStoreDynamicPwd(String storeCode, String ip) {
        if(ip != null && !ip.isEmpty())
            simpleMapper.updateStoreDynamicPwd_Ip(ip, storeCode);
        return simpleMapper.selectStoreDynamicPwd(storeCode);
    }


    @Override
    public String registerLoginAndGetToken(String username) {
        String newTokenKey = RandomStringUtils.randomAlphanumeric(6);
        String loginTime = DateUtil.toStr(new Date());
        LoginMgr loginMgr = loginMgrMapper.selectById(username);
        if(loginMgr != null) {
            loginMgr.loginTime = loginTime;
            loginMgr.tokenKey = newTokenKey;
            loginMgrMapper.update(loginMgr);
        } else {
            loginMgr = new LoginMgr();
            loginMgr.username = username;
            loginMgr.loginTime = loginTime;
            loginMgr.tokenKey = newTokenKey;
            loginMgrMapper.insert(loginMgr);
        }
        try {
            String token = XxteaUtil.encryptBase64String(TokenPrefix + username, "UTF-8", newTokenKey);
            loginUsers.put(username, token);
            return token;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return newTokenKey;
    }

    @Override
    public String getToken(String username) {
        LoginMgr loginMgr = loginMgrMapper.selectById(username);
        String tokenKey = "";
        if(loginMgr != null) {
            tokenKey = loginMgr.tokenKey;
            try {
                return XxteaUtil.encryptBase64String(TokenPrefix + username, "UTF-8", tokenKey);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
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
