package com.tlg.storehelper.service;

import com.tlg.storehelper.pojo.GoodsBarcodeEntity;
import com.tlg.storehelper.pojo.SimpleEntity;
import com.tlg.storehelper.pojo.SimpleListEntity;

public interface ApiService {

    /////////////////////////////////////////////////////////////
    ////////////////////       Login     ////////////////////////
    /////////////////////////////////////////////////////////////
    public SimpleListEntity<String> loginValidation(String username, String password);


    /////////////////////////////////////////////////////////////
    ////////////////////       Goods     ////////////////////////
    /////////////////////////////////////////////////////////////
    public SimpleEntity<String> getGoodsBarcodeList(String lastModDate);
    //public GoodsBarcodeEntity getGoodsBarcodeList(String lastModDate);


}
