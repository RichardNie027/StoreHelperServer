package com.tlg.storehelper.dao.second;

import com.tlg.storehelper.entity.second.InventoryDetail;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface InventoryDetailMapper {

    final String TABLE = "StoreHelper.dbo.InventoryDetail";

    @Select("SELECT * FROM " + TABLE + " WHERE id=#{id}")
    public InventoryDetail selectById(long id);

    @Select("SELECT * FROM " + TABLE + " WHERE pid=#{pid}")
    public List<InventoryDetail> selectByParentId(long pid);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("INSERT INTO " + TABLE + " (pid, bin_coding, barcode, quantity) VALUES (#{pid}, #{bin_coding}, #{barcode}, #{quantity})")
    int insert(InventoryDetail inventoryDetail);

    @Delete("DELETE FROM " + TABLE + " WHERE id=#{id}")
    int delete(Long id);

    @Update("UPDATE " + TABLE + " SET pid=#{pid}, bin_coding=#{bin_coding}, barcode=#{barcode}, quantity=#{quantity} WHERE id=#{id}")
    int update(InventoryDetail inventoryDetail);

}
