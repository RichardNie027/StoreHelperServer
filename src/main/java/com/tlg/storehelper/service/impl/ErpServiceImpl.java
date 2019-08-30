package com.tlg.storehelper.service.impl;

import com.tlg.storehelper.dao.main.GoodsBarcodeMapper;
import com.tlg.storehelper.dao.main.ErpUserMapper;
import com.tlg.storehelper.entity.main.ErpUser;
import com.tlg.storehelper.pojo.SimpleEntity;
import com.tlg.storehelper.service.ErpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ErpServiceImpl implements ErpService {

    @Autowired
    private ErpUserMapper erpUserMapper;
    @Autowired
    private GoodsBarcodeMapper goodsBarcodeMapper;

    @Override
    public List<ErpUser> getAllStoreUsers() {
        return erpUserMapper.selectAllStoreUsers();
    }

    @Override
    public ErpUser getUserByAccount(String userAccount) {
        return erpUserMapper.selectUserByAccount(userAccount);
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
