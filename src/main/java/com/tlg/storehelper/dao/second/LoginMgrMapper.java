package com.tlg.storehelper.dao.second;

import com.tlg.storehelper.entity.second.LoginMgr;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface LoginMgrMapper {

    final String TABLE = "StoreHelper.dbo.LoginMgr";

    @Select("SELECT * FROM " + TABLE + " WHERE username=#{username}")
    LoginMgr selectById(String username);

    @Options(useGeneratedKeys = false, keyProperty = "username")
    @Insert("INSERT INTO " + TABLE + " (username, token_key, login_time) VALUES (#{username}, #{token_key}, #{login_time})")
    int insert(LoginMgr loginMgr);

    @Delete("DELETE FROM " + TABLE + " WHERE username=#{username}")
    int delete(String username);

    @Update("UPDATE " + TABLE + " SET token_key=#{token_key}, login_time=#{login_time} WHERE username=#{username}")
    int update(LoginMgr loginMgr);

}
