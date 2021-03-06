package com.tlg.storehelper.dao.ds3;

import com.tlg.storehelper.entity.ds3.RunsaPosTransfer;
import org.apache.ibatis.annotations.*;

import java.util.List;

@CacheNamespace(readWrite = false, flushInterval = 60000)
public interface RunsaPosTransferMapper {

    String TABLE = "OA.dbo.RunsaPosTransfer";

    @Options(useGeneratedKeys = false)
    @Insert("INSERT INTO " + TABLE + " (nos, cusno, incusno, outdate, flags, nos_rec, colthno, color, price, nb, outnb) VALUES (#{nos}, #{cusno}, #{incusno}, #{outdate}, #{flags}, #{nos_rec}, #{colthno}, #{color}, #{price}, #{nb}, #{outnb})")
    int insert(RunsaPosTransfer runsaPosTransfer);

    @Delete("DELETE FROM " + TABLE + " WHERE cusno=#{cusno}")
    int deleteByDeliveryStoreCode(String cusno);

    @Select("SELECT * FROM " + TABLE + " WHERE cusno=#{cusno}")
    List<RunsaPosTransfer> selectByDeliveryStoreCode(String cusno);

}
