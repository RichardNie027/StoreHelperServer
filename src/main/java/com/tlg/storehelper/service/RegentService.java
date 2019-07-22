package com.tlg.storehelper.service;

import com.tlg.storehelper.entity.RegentUser;
import com.tlg.storehelper.pojo.GoodsBarcodeEntity;
import com.tlg.storehelper.pojo.SimpleListEntity;

import java.util.List;

public interface RegentService {

    public List<RegentUser> getAllStoreUsers();

    public RegentUser getUserByAccount(String userAccount);

    public SimpleListEntity<String> getAllSimpleGoodsBarcodes();

    public GoodsBarcodeEntity getLastestGoodsBarcodes(String lastModDate);

}
