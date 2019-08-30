package com.tlg.storehelper.dao.main;

import com.tlg.storehelper.entity.main.GoodsBarcode;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface GoodsBarcodeMapper {

    /** 已废除，改用selectAllSimpleGoodsBarcodes */
    /**
    @Select("SELECT A.ID, NVL(D.BRAND,' ') BRAND,NVL(C.GOODSNO,' ') GOODSNO, NVL(C.GOODSNAME,' ') GOODSNAME, B.SIZEDESC, A.BARCODE, A.LASTMODDATE " +
            "FROM REGENTD.GOODSBARCODE A LEFT JOIN REGENTD.SIZECATEGORY B ON A.SIZEID=B.ID " +
            "LEFT JOIN GOODS C ON A.GOODSID=C.GOODSID " +
            "LEFT JOIN " +
            "(SELECT ID,NAME BRAND FROM GoodsCategory WHERE depth=1 AND itemid='4745FD287704476CB37D05025665751B') D " +
            "ON C.STORYCATEGORYID=D.ID " +
            "WHERE A.LASTMODDATE>to_date(#{lastModDate},'yyyymmddhh24miss')")  //Regent
    @Deprecated
    public List<GoodsBarcode> selectLastestGoodsBarcodes(String lastModDate);*/

//    @Select("SELECT BARCODE FROM REGENTD.GOODSBARCODE ORDER BY BARCODE")  //Regent
    @Select("SELECT words as BARCODE FROM dbo.coloth WHERE words<>'' ORDER BY words")
    public List<String> selectAllSimpleGoodsBarcodes();

//    @Select("SELECT nvl(to_char(max(LASTMODDATE), 'yyyy-mm-dd hh24:mi:ss'), ' ') LASTMODDATE FROM REGENTD.GOODSBARCODE")  //Regent
    @Select("SELECT substring(convert(char(8),max(gxrq),112),1,4)+'-'+substring(convert(char(8),max(gxrq),112),5,2)+'-'+substring(convert(char(8),max(gxrq),112),7,2)+''+convert(char(8),max(gxrq),108) as LASTMODDATE FROM dbo.coloth")
    public String selectLastModDate();
}
