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

    @Select("SELECT '00-'+gg_no AS size FROM dbo.coloth_t a LEFT JOIN gg b ON a.sizekind=b.sizekind WHERE colthno=#{goodsNo} HAVING '00-'+gg_no IN (select color_nos from coloth where colthno=#{goodsNo}) order by b.gg_print")
    List<String> selectGoodsSize(String goodsNo);

}
