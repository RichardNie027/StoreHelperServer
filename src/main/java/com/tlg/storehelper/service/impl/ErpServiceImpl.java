package com.tlg.storehelper.service.impl;

import com.google.gson.Gson;
import com.nec.lib.utils.NetUtil;
import com.tlg.storehelper.dao.ds1.GoodsBarcodeMapper;
import com.tlg.storehelper.dao.ds1.ErpUserMapper;
import com.tlg.storehelper.dao.ds1.GoodsMapper;
import com.tlg.storehelper.dao.ds3.BestSellingMapper;
import com.tlg.storehelper.dao.ds3.CollocationMapper;
import com.tlg.storehelper.entity.ds1.ErpUser;
import com.tlg.storehelper.entity.ds1.Goods;
import com.tlg.storehelper.entity.ds3.BestSelling;
import com.tlg.storehelper.entity.ds3.Collocation;
import com.tlg.storehelper.pojo.BaseResponseEntity;
import com.tlg.storehelper.pojo.SimpleEntity;
import com.tlg.storehelper.service.ErpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ErpServiceImpl implements ErpService {

    @Autowired
    private ErpUserMapper erpUserMapper;
    @Autowired
    private GoodsBarcodeMapper goodsBarcodeMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private CollocationMapper collocationMapper;
    @Autowired
    private BestSellingMapper bestSellingMapper;

    @Value("${properties.erp_service.resource_url}")
    private String ResourceUrl;

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
            entity.resultList = goodsBarcodeMapper.selectAllSimpleGoodsBarcodes(lastModDate.substring(0,8));
            entity.resultMap.put("lastModDate", freshLastModDate);
        }
        return entity;
    }

    @Override
    public Goods getGoods(String goodsNo) {
        return goodsMapper.selectGoods(goodsNo);
    }

    @Override
    public boolean queryGoodsPicture(String goodsNo) {
        String resp = NetUtil.request(ResourceUrl + goodsNo, null, "GET", "json");
        BaseResponseEntity entity = new Gson().fromJson(resp, BaseResponseEntity.class);
        return entity != null && entity.code == 200;
    }

    @Override
    public String getGoodsPictureName(String goodsNo) {
        return goodsMapper.selectGoodsPictureName(goodsNo);
    }

    @Override
    public List<Goods> getAllGoods() {
        return goodsMapper.selectAllGoods();
    }

    @Override
    public List<Collocation> getCollocation(String goodsNo) {
        return collocationMapper.selectCollocation(goodsNo);
    }

    @Override
    public int getBestSellingCount(String storeCode, String dimension) {
        return bestSellingMapper.selectBestSellingCount(dimension, storeCode.substring(0,1));
    }

    @Override
    public List<BestSelling> getBestSelling(String storeCode, String dimension, int recordPerPage, int page) {
        return bestSellingMapper.selectBestSelling(dimension, storeCode.substring(0,1), recordPerPage, recordPerPage * page);
    }

    private String getGoodsBarcodeLastModDate() {
        String lastModDate = goodsBarcodeMapper.selectLastModDate();
        return lastModDate.trim().equals("") ? "20000101000000" : lastModDate;
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
