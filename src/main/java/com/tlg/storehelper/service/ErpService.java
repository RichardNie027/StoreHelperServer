package com.tlg.storehelper.service;

import com.tlg.storehelper.entity.main.ErpUser;
import com.tlg.storehelper.pojo.SimpleEntity;

import java.util.List;

public interface ErpService {

    public List<ErpUser> getAllStoreUsers();

    public ErpUser getUserByAccount(String userAccount);

    public SimpleEntity<String> getAllSimpleGoodsBarcodes(String lastModDate);

    //public GoodsBarcodeEntity getLastestGoodsBarcodes(String lastModDate);

}
