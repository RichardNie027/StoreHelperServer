package com.tlg.storehelper.dao.second;

import com.tlg.storehelper.entity.second.Inventory;
import org.apache.ibatis.annotations.*;

public interface InventoryMapper {

    String TABLE = "StoreHelper.dbo.Inventory";

    @Select("SELECT * FROM " + TABLE + " WHERE id=#{id}")
    Inventory selectById(String id);

    @Options(useGeneratedKeys = false, keyProperty = "id")
    @Insert("INSERT INTO " + TABLE + " (id, storeCode, listDate, idx, username, listNo, status, createTime, lastTime) VALUES (#{id}, #{storeCode}, #{listDate}, #{idx}, #{username}, #{listNo}, #{status}, #{createTime}, #{lastTime})")
    int insert(Inventory inventory);

    @Delete("DELETE FROM " + TABLE + " WHERE id=#{id}")
    int delete(String id);

    @Update("UPDATE " + TABLE + " SET storeCode=#{storeCode}, listDate=#{listDate}, idx=#{idx}, username=#{username}, listNo=#{listNo}, status=#{status}, createTime=#{createTime}, lastTime=#{lastTime} WHERE id=#{id}")
    int update(Inventory inventory);
}
