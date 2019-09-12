package com.tlg.storehelper.dao.ds2;

import com.tlg.storehelper.entity.ds2.InventoryDetail;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface InventoryDetailMapper {

    String TABLE = "StoreHelper.dbo.InventoryDetail";

    @Select("SELECT * FROM " + TABLE + " WHERE id=#{id}")
    InventoryDetail selectById(String id);

    @Select("SELECT * FROM " + TABLE + " WHERE pid=#{pid}")
    List<InventoryDetail> selectByParentId(String pid);

    @Options(useGeneratedKeys = false, keyProperty = "id")
    @Insert("INSERT INTO " + TABLE + " (id, pid, idx, binCoding, barcode, quantity) VALUES (#{id}, #{pid}, #{idx}, #{binCoding}, #{barcode}, #{quantity})")
    int insert(InventoryDetail inventoryDetail);

    @Delete("DELETE FROM " + TABLE + " WHERE id=#{id}")
    int delete(String id);

    @Delete("DELETE FROM " + TABLE + " WHERE pid=#{pid}")
    int deleteByPid(String pid);

    @Update("UPDATE " + TABLE + " SET pid=#{pid}, binCoding=#{binCoding}, barcode=#{barcode}, quantity=#{quantity} WHERE id=#{id}")
    int update(InventoryDetail inventoryDetail);

}
