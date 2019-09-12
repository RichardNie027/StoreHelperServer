package com.tlg.storehelper.dao.second;

import org.apache.ibatis.annotations.Select;

public interface SimpleMapper {

    @Select("SELECT COUNT(*) AS num FROM [Dingtalk].[dbo].[usersExt] WHERE UsersExt_JobNumber=#{jobNumber} AND Right(UsersExt_IdNumber,6)=#{id6} AND (UsersExt_JobName like '财务%' OR UsersExt_JobName like '信息%')")
    int checkUserExist(String jobNumber, String id6);

    @Select("SELECT TOP 1 pwd FROM [StoreHelper].[dbo].[DynamicPwd] WHERE storeCode=#{storeCode}")
    String selectStoreDynamicPwd(String storeCode);

}
