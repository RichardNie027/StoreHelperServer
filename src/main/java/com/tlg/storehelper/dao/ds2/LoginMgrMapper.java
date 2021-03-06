package com.tlg.storehelper.dao.ds2;

import com.tlg.storehelper.entity.ds2.LoginMgr;
import org.apache.ibatis.annotations.*;

public interface LoginMgrMapper {

    String TABLE = "StoreHelper.dbo.LoginMgr";

    @Select("SELECT * FROM " + TABLE + " WHERE username=#{username}")
    LoginMgr selectById(String username);

    @Options(useGeneratedKeys = false, keyProperty = "username")
    @Insert("INSERT INTO " + TABLE + " (username, tokenKey, loginTime) VALUES (#{username}, #{tokenKey}, #{loginTime})")
    int insert(LoginMgr loginMgr);

    @Delete("DELETE FROM " + TABLE + " WHERE username=#{username}")
    int delete(String username);

    @Update("UPDATE " + TABLE + " SET tokenKey=#{tokenKey}, loginTime=#{loginTime} WHERE username=#{username}")
    int update(LoginMgr loginMgr);

}
