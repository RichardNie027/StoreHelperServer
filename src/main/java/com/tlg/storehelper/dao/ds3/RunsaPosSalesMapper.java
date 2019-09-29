package com.tlg.storehelper.dao.ds3;

import com.tlg.storehelper.entity.ds3.RunsaPosSales;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RunsaPosSalesMapper {

    String TABLE = "OA.dbo.RunsaPosSales";

    @Options(useGeneratedKeys = false)
    @Insert("INSERT INTO " + TABLE + " (cusno, nos, nos_rec, colthno, color, price, proprice, nb, intime, names, salescode) VALUES (#{cusno}, #{nos}, #{nos_rec}, #{colthno}, #{color}, #{price}, #{proprice}, #{nb}, #{intime}, #{names}, #{salescode})")
    int insert(RunsaPosSales runsaPosSales);

    @Delete("DELETE FROM " + TABLE + " WHERE cusno=#{storeCode}")
    int deleteByStoreCode(String storeCode);

    @Select("SELECT * FROM " + TABLE + " WHERE cusno=#{storeCode}")
    List<RunsaPosSales> selectByStoreCode(String storeCode);

    @Select("SELECT * FROM " + TABLE + " WHERE cusno=#{storeCode} and colthno=#{goodsNo}")
    List<RunsaPosSales> selectByStoreCodeAndSKC(String storeCode, String goodsNo);

    @Select("SELECT * FROM " + TABLE + " WHERE cusno=#{storeCode} and colthno=#{goodsNo} and color=#{size}")
    List<RunsaPosSales> selectByStoreCodeAndSKU(String storeCode, String goodsNo, String size);
}
