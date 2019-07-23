package com.tlg.storehelper.dao.second;

import com.tlg.storehelper.entity.second.Inventory;
import org.apache.ibatis.annotations.*;

public interface InventoryMapper {

    final String TABLE = "StoreHelper.dbo.Inventory";

    @Select("SELECT * FROM " + TABLE + " WHERE id=#{id}")
    Inventory selectById(long id);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("INSERT INTO " + TABLE + " (store_code, list_date, idx, username, list_no, create_time, last_time) VALUES (#{store_code}, #{list_date}, #{idx}, #{username}, #{list_no}, #{create_time}, #{last_time})")
    int insert(Inventory inventory);

    @Delete("DELETE FROM " + TABLE + " WHERE id=#{id}")
    int delete(Long id);

    @Update("UPDATE " + TABLE + " SET store_code=#{store_code}, list_date=#{list_date}, idx=#{idx}, username=#{username}, list_no=#{list_no}, create_time=#{create_time}, last_time=#{last_time} WHERE id=#{id}")
    int update(Inventory inventory);
}
