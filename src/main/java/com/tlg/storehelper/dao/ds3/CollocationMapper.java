package com.tlg.storehelper.dao.ds3;

import com.tlg.storehelper.entity.ds3.Collocation;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface CollocationMapper {

    String TABLE = "OA.dbo.RUNSA_SellColthnoLD";

    @Select("SELECT TOP 10 Colthno1 AS goodsNo,substring(t2.talka,len(t2.talka)-charindex('\\',reverse(t2.talka))+2,1000) AS pic,CASE Colthno1Kc WHEN '少' THEN 1 WHEN '有' THEN 2 WHEN '多' THEN 3 ELSE 0 END AS stock,LdCount AS frequency FROM " + TABLE + " t1 LEFT JOIN OA.dbo.RUNSA_Coloth_t t2 ON t1.Colthno1=t2.colthno WHERE t1.Colthno=#{goodsNo} ORDER BY LdCount DESC")
    List<Collocation> selectCollocation(String goodsNo);
}
