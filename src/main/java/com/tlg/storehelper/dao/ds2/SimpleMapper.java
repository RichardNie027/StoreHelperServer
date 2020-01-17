package com.tlg.storehelper.dao.ds2;

import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@CacheNamespace(readWrite = false, flushInterval = 60000)
public interface SimpleMapper {

    //@Select("SELECT COUNT(*) AS num FROM [Dingtalk].[dbo].[usersExt] WHERE UsersExt_JobNumber=#{jobNumber} AND Right(UsersExt_IdNumber,6)=#{id6} AND (UsersExt_JobName like '财务%' OR UsersExt_JobName like '信息%' OR UsersExt_JobName='销管货品主管')")
    //AllUser: SELECT a.UsersExt_Name,a.UsersExt_JobNumber,substring(a.UsersExt_IdNumber,13,6) FROM [Dingtalk].[dbo].[usersExt] a LEFT JOIN UserAppRights b ON a.UsersExt_JobNumber=b.jobNumber WHERE (UsersExt_JobName like '财务%' OR UsersExt_JobName like '信息%' OR UsersExt_JobName='销管货品主管')
    @Select("SELECT TOP 1 CASE WHEN UsersExt_JobNumber IS NULL THEN '0' ELSE '1' END + '|' + isnull(b.storeCodes,'') + '|' + isnull(b.appRights,'') as appRights FROM (SELECT #{jobNumber} AS f0) t0 LEFT JOIN [Dingtalk].[dbo].[usersExt] a ON t0.f0=a.UsersExt_JobNumber AND UsersExt_JobNumber=t0.f0 AND Right(UsersExt_IdNumber,6)=#{id6} AND (UsersExt_JobName like '财务%' OR UsersExt_JobName like '信息%' OR UsersExt_JobName='销管货品主管') LEFT JOIN UserAppRights b ON a.UsersExt_JobNumber=b.jobNumber")
    String checkUserRights(String jobNumber, String id6);

    @Select("SELECT TOP 1 pwd FROM [StoreHelper].[dbo].[DynamicPwd] WHERE storeCode=#{storeCode}")
    String selectStoreDynamicPwd(String storeCode);

    @Select("SELECT TOP 1 ip FROM [StoreHelper].[dbo].[DynamicPwd] WHERE storeCode=#{storeCode}")
    String selectStoreIp(String storeCode);

    @Update("UPDATE [StoreHelper].[dbo].[DynamicPwd] SET ip=#{ip} WHERE storeCode=#{storeCode}")
    int updateStoreDynamicPwd_Ip(String ip, String storeCode);

//    @Select("SELECT html FROM [StoreHelper].[dbo].[H5Content] WHERE brandKey=#{brandKey} and type=#{type}")
//    String selectHtml(String brandKey, String type);
//
//    @Select("SELECT type+'|'+convert(varchar,picVer)+'|'+pics FROM [StoreHelper].[dbo].[H5Content] WHERE brandKey=#{brandKey} ORDER BY type")
//    List<String> selectBrandPics(String brandKey);

}
