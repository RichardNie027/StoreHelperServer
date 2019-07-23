package com.tlg.storehelper.service.impl;

import com.tlg.storehelper.dao.main.GoodsBarcodeMapper;
import com.tlg.storehelper.dao.main.RegentUserMapper;
import com.tlg.storehelper.entity.main.GoodsBarcode;
import com.tlg.storehelper.entity.main.RegentUser;
import com.tlg.storehelper.pojo.GoodsBarcodeEntity;
import com.tlg.storehelper.pojo.SimpleEntity;
import com.tlg.storehelper.pojo.SimpleListEntity;
import com.tlg.storehelper.service.RegentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class RegentServiceImpl implements RegentService {

    @Autowired
    private RegentUserMapper regentUserMapper;
    @Autowired
    private GoodsBarcodeMapper goodsBarcodeMapper;

    @Override
    public List<RegentUser> getAllStoreUsers() {
        return regentUserMapper.selectAllStoreUsers();
    }

    @Override
    public RegentUser getUserByAccount(String userAccount) {
        return regentUserMapper.selectUserByAccount(userAccount);
    }

    @Override
    public SimpleEntity<String> getAllSimpleGoodsBarcodes(String lastModDate) {
        SimpleEntity<String> entity = new SimpleEntity();
        String freshLastModDate = getGoodsBarcodeLastModDate();
        if(freshLastModDate.compareTo(lastModDate) > 0) {
            entity.result_list = goodsBarcodeMapper.selectAllSimpleGoodsBarcodes();
            entity.result_map.put("lastModDate", freshLastModDate);
        }
        return entity;
    }

    private String getGoodsBarcodeLastModDate() {
        String lastModDate = goodsBarcodeMapper.selectLastModDate();
        return lastModDate.trim().equals("") ? "2000-01-01 00:00:00" : lastModDate;
    }

    /*
    @Deprecated
    @Override
    public GoodsBarcodeEntity getLastestGoodsBarcodes(String lastModDate) {
        //PropertyUtilsBean bean = new PropertyUtilsBean();
        GoodsBarcodeEntity entity = new GoodsBarcodeEntity();
        List<GoodsBarcode> list = goodsBarcodeMapper.selectLastestGoodsBarcodes(lastModDate);
        Date maxModDate = new Date(0);
        for(GoodsBarcode goodsBarcode: list) {
            GoodsBarcodeEntity.ResultBean bean = new GoodsBarcodeEntity.ResultBean();
            bean.id =  goodsBarcode.id;
            bean.brand = goodsBarcode.brand;
            bean.goodsNo = goodsBarcode.goodsNo;
            bean.goodsName = goodsBarcode.goodsName;
            bean.sizeDesc = goodsBarcode.sizeDesc;
            bean.barcode = goodsBarcode.barcode;
            maxModDate = maxModDate.after(goodsBarcode.lastModDate) ? maxModDate : goodsBarcode.lastModDate;
            entity.result.add(bean);
        }
        entity.lastModDate = new SimpleDateFormat("yyyyMMddHHmmss").format(maxModDate);
        return entity;
    }*/
}
