package com.tlg.storehelper.dao.third;

import com.tlg.storehelper.entity.third.BestSelling;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface BestSellingMapper {

    String TABLE = "OA.dbo.v_best_selling";

    @Select("SELECT COUNT(*) AS num FROM " + TABLE + " WHERE dim=#{dim} AND brand=#{brand}")
    int selectBestSellingCount(String dim, String brand);

//    @Select("SELECT TOP #{recordPerPage} dim,brand,goodsNo,t2.sprice as price,quantity FROM " + TABLE + " t1 LEFT JOIN OA.dbo.RUNSA_Coloth_t t2 ON t1.goodsNo=t2.colthno WHERE dim=#{dim} AND brand=#{brand} AND goodsNo NOT IN (SELECT TOP #{rowNumberFrom} goodsNo FROM " + TABLE + " WHERE dim=#{dim} AND brand=#{brand} ORDER BY quantity DESC,goodsNo) ORDER BY quantity DESC,goodsNo")
    @Select(value = "exec OA.dbo.sp_best_selling "
                    +"@dim=#{dim,mode=IN,jdbcType=VARCHAR},"
                    +"@brand=#{brand,mode=IN,jdbcType=VARCHAR},"
                    +"@recordPerPage=#{recordPerPage,mode=IN,jdbcType=INTEGER},"
                    +"@rowNumberFrom=#{rowNumberFrom,mode=IN,jdbcType=INTEGER}")
    List<BestSelling> selectBestSelling(String dim, String brand, int recordPerPage,int rowNumberFrom);
}
