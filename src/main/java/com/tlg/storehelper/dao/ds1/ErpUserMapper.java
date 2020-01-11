package com.tlg.storehelper.dao.ds1;

import com.tlg.storehelper.entity.ds1.ErpUser;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * ERP用户
 */
@CacheNamespace(readWrite = false, flushInterval = 60000)
public interface ErpUserMapper {

//    @Select("SELECT userid,useraccount,password,type,enabled FROM regentuser where type=2 and enabled=1 order by useraccount")    //Regent
    @Select("<script> SELECT * FROM (" +
            "SELECT substring(codes,2,3) as userId,substring(codes,2,3) as userAccount,names as username,'' as password,2 as type,1 as enabled FROM dbo.password WHERE (codes like 'A___' OR codes like 'B___') AND substring(privilege,7,3) in " +
            "(SELECT substring(dbno,2,3) FROM dbo.db WHERE closeif=0 AND dbno not like '_Z__' AND class3 &lt;&gt; '经销店' and substring(dbno,2,3) &lt;&gt; '101' " +
            " <if test='storeLike!=null and storeLike!=\"\"'>"+
            "  and substring(dbno,2,3) like #{storeLike} "+
            " </if>"+
            " <if test='storeCodes!=null and storeCodes.size>0'>"+
            "  and substring(dbno,2,3) in " +
            "<foreach item='item' index='index' collection='storeCodes' open='(' separator=',' close=')'> " +
            " #{item}"+
            "</foreach> "+
            " </if>"+
            ")" +
            ") T1 ORDER BY userId </script>")
    List<ErpUser> selectStoreUsersInAuthorization(String storeLike, @Param("storeCodes")List<String> storeCodes);

//    @Select("SELECT userid,useraccount,password,type,enabled FROM regentuser WHERE userid=#{userId}")    //Regent
    @Select("SELECT * FROM (" +
            "SELECT substring(codes,2,3) as userId,substring(codes,2,3) as userAccount,names as username,'' as password,2 as type,1 as enabled FROM dbo.password WHERE (codes like 'A___' OR codes like 'B___') AND substring(privilege,7,3) in " +
            "(SELECT substring(dbno,2,3) FROM dbo.db WHERE closeif=0 AND dbno not like '_Z__' AND class3 <> '经销店' and substring(dbno,2,3)<>'101')" +
//            " UNION " +
//            "SELECT codes as userId,codes as userAccount,names as username,'' as password,1 as type,1 as enabled FROM dbo.password WHERE codes like 'CW____' OR codes like 'JJ____'" +
            ") T1 WHERE userId=#{userId}")
    ErpUser selectUser(String userId);

//    @Select("SELECT userid,useraccount,password,type,enabled FROM regentuser WHERE useraccount=#{userAccount}")    //Regent
    @Select("SELECT * FROM (" +
            "SELECT substring(codes,2,3) as userId,substring(codes,2,3) as userAccount,names as username,'' as password,2 as type,1 as enabled FROM dbo.password WHERE (codes like 'A___' OR codes like 'B___') AND substring(privilege,7,3) in " +
            "(SELECT substring(dbno,2,3) FROM dbo.db WHERE closeif=0 AND dbno not like '_Z__' AND class3 <> '经销店' and substring(dbno,2,3)<>'101')" +
//            " UNION " +
//            "SELECT codes as userId,codes as userAccount,names as username,'' as password,1 as type,1 as enabled FROM dbo.password WHERE codes like 'CW____' OR codes like 'JJ____'" +
            ") T1 WHERE userAccount=#{userAccount}")
    ErpUser selectUserByAccount(String userAccount);
}
