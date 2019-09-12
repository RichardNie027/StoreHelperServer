package com.tlg.storehelper.dao.ds1;

import com.tlg.storehelper.entity.ds1.Goods;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface GoodsMapper {

    @Select("SELECT colthno AS goodsNo, colthname AS goodsName, ceiling(sprice) AS price FROM dbo.coloth_t")
    List<Goods> selectAllGoods();

    @Select("SELECT colthno AS goodsNo, colthname AS goodsName, substring(talka,len(talka)-charindex('\\',reverse(talka))+2,1000) AS pic, ceiling(sprice) AS price FROM dbo.coloth_t WHERE colthno=#{goodsNo}")
    Goods selectGoods(String goodsNo);

    @Select("SELECT top 1 case when left(upper(talka),4)='\\\\OA' then '\\\\192.168.1.8'+substring(talka,5,1000) else '' end AS pic FROM dbo.coloth_t WHERE colthno=#{goodsNo}")
    String selectGoodsPictureName(String goodsNo);

}
