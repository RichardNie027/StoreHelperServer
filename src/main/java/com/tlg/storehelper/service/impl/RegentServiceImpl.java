package com.tlg.storehelper.service.impl;

import com.tlg.storehelper.dao.GoodsBarcodeMapper;
import com.tlg.storehelper.dao.RegentUserMapper;
import com.tlg.storehelper.entity.GoodsBarcode;
import com.tlg.storehelper.entity.RegentUser;
import com.tlg.storehelper.pojo.GoodsBarcodeEntity;
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
    public SimpleListEntity<String> getAllSimpleGoodsBarcodes() {
        SimpleListEntity<String> entity = new SimpleListEntity();
        entity.result = goodsBarcodeMapper.selectAllSimpleGoodsBarcodes();
        return entity;
    }

    @Override
    public GoodsBarcodeEntity getLastestGoodsBarcodes(String lastModDate) {
        //PropertyUtilsBean bean = new PropertyUtilsBean();
        GoodsBarcodeEntity entity = new GoodsBarcodeEntity();
        List<GoodsBarcode> list = goodsBarcodeMapper.selectLastestGoodsBarcodes(lastModDate);
        Date maxModDate = new Date(0);
        for(GoodsBarcode goodsBarcode: list) {
            GoodsBarcodeEntity.ResultBean bean = new GoodsBarcodeEntity.ResultBean();
            bean.id =  goodsBarcode.getId();
            bean.brand = goodsBarcode.getBrand();
            bean.goodsNo = goodsBarcode.getGoodsNo();
            bean.goodsName = goodsBarcode.getGoodsName();
            bean.sizeDesc = goodsBarcode.getSizeDesc();
            bean.barcode = goodsBarcode.getBarcode();
            maxModDate = maxModDate.after(goodsBarcode.getLastModDate()) ? maxModDate : goodsBarcode.getLastModDate();
            entity.result.add(bean);
        }
        entity.lastModDate = new SimpleDateFormat("yyyyMMddHHmmss").format(maxModDate);
        return entity;
    }
}
