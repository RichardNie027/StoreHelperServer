package com.tlg.storehelper.dao.ds3;

import com.tlg.storehelper.entity.ds3.RunsaPosStock;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@CacheNamespace(readWrite = false, flushInterval = 60000)
public interface RunsaPosStockMapper {

    String TABLE = "OA.dbo.RunsaPosStock";

    @Options(useGeneratedKeys = false)
    @Insert("INSERT INTO " + TABLE + " (dbno, colthno, color, nb) VALUES (#{dbno}, #{colthno}, #{color}, #{nb})")
    int insert(RunsaPosStock runsaPosStock);

    @Delete("DELETE FROM " + TABLE + " WHERE dbno=#{storeCode}")
    int deleteByStoreCode(String storeCode);

    @Select("SELECT * FROM " + TABLE + " WHERE dbno=#{storeCode}")
    List<RunsaPosStock> selectByStoreCode(String storeCode);

    @Select("SELECT * FROM " + TABLE + " WHERE dbno=#{storeCode} and colthno=#{goodsNo}")
    List<RunsaPosStock> selectByStoreCodeAndSKC(String storeCode, String goodsNo);

    @Select("SELECT * FROM " + TABLE + " WHERE colthno=#{goodsNo} and color=#{size}")
    List<RunsaPosStock> selectBySKU(String goodsNo, String size);

}
