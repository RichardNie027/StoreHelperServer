package com.tlg.storehelper.dao;

import com.tlg.storehelper.entity.RegentUser;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RegentUserMapper {

    @Select("SELECT userid,useraccount,password,type,passtype,enabled FROM regentuser where type=2 and enabled=1 order by useraccount")
    public List<RegentUser> selectAllStoreUsers();

    @Select("SELECT userid,useraccount,password,type,passtype,enabled FROM regentuser WHERE userid=#{userId}")
    public RegentUser selectUser(String userId);

    @Select("SELECT userid,useraccount,password,type,passtype,enabled FROM regentuser WHERE useraccount=#{userAccount}")
    public RegentUser selectUserByAccount(String userAccount);
}
