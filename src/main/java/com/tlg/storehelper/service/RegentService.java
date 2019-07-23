package com.tlg.storehelper.service;

import com.tlg.storehelper.entity.main.RegentUser;
import com.tlg.storehelper.pojo.GoodsBarcodeEntity;
import com.tlg.storehelper.pojo.SimpleEntity;

import java.util.List;

public interface RegentService {

    public List<RegentUser> getAllStoreUsers();

    public RegentUser getUserByAccount(String userAccount);

    public SimpleEntity<String> getAllSimpleGoodsBarcodes(String lastModDate);

    //public GoodsBarcodeEntity getLastestGoodsBarcodes(String lastModDate);

}
