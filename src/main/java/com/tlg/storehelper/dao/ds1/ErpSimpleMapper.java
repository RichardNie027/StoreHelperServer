package com.tlg.storehelper.dao.ds1;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Map;

public interface ErpSimpleMapper {

    @Select("select isnull(sum(rnb-tempoutnb),0) as quantity from coloth_f where ckaccno='Z000' and colthno=#{goodsNo} and color_nos=#{size}")
    int selectWarehouseStockBySKU(String goodsNo, String size);

//    @Select("select color_nos as colorSize, isnull(rnb-tempoutnb,0) as quantity from coloth_f where ckaccno='Z000' and colthno=#{goodsNo}")
//    Map<String, Integer> selectWarehouseStockBySKC(String goodsNo);

}
