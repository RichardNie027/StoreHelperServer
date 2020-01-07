package com.tlg.storehelper.dao.ds3;

import com.tlg.storehelper.pojo.GoodsPopularityVo;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@CacheNamespace(readWrite = false, flushInterval = 600000)
public interface GoodsPopularityMapper {

    String TABLE = "OA.dbo.RunsaProductPopularity";

    @Select("SELECT colthno as goodsNo,popularity FROM " + TABLE + " WHERE popularity>-10000 AND storeHeaders like #{storeCode1} ORDER BY popularity DESC")
    List<GoodsPopularityVo> selectPopularities(String storeCode1);
}
