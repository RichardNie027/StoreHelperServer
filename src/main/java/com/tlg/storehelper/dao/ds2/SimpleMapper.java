package com.tlg.storehelper.dao.ds2;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface SimpleMapper {

    @Select("SELECT COUNT(*) AS num FROM [Dingtalk].[dbo].[usersExt] WHERE UsersExt_JobNumber=#{jobNumber} AND Right(UsersExt_IdNumber,6)=#{id6} AND (UsersExt_JobName like '财务%' OR UsersExt_JobName like '信息%')")
    int checkUserExist(String jobNumber, String id6);

    @Select("SELECT TOP 1 pwd FROM [StoreHelper].[dbo].[DynamicPwd] WHERE storeCode=#{storeCode}")
    String selectStoreDynamicPwd(String storeCode);

    @Select("SELECT TOP 1 ip FROM [StoreHelper].[dbo].[DynamicPwd] WHERE storeCode=#{storeCode}")
    String selectStoreIp(String storeCode);

    @Update("UPDATE [StoreHelper].[dbo].[DynamicPwd] SET ip=#{ip} WHERE storeCode=#{storeCode}")
    int updateStoreDynamicPwd_Ip(String ip, String storeCode);

    @Select("SELECT html FROM [StoreHelper].[dbo].[H5Content] WHERE brandKey=#{brandKey} and type=#{type}")
    String selectHtml(String brandKey, String type);

    @Select("SELECT type+'|'+convert(varchar,picVer)+'|'+pics FROM [StoreHelper].[dbo].[H5Content] WHERE brandKey=#{brandKey} ORDER BY type")
    List<String> selectBrandPics(String brandKey);
}
