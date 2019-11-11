package com.tlg.storehelper.dao.ds1;

import com.tlg.storehelper.entity.ds1.Goods;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface GoodsMapper {

    //商品货号、商品款码、商品颜色值、商品名称、商品吊牌价、商品品牌、关联的店铺编码首字符、（创建日期 yyyy-MM-dd）
    @Select("SELECT t.colthno+'|'+rtrim(colthnob)+'|'+rtrim(colthname2)+'|'+STR_REPLACE(t.colthname,'|','')+'|'+convert(VARCHAR,convert(INTEGER,t.sprice))+'|'+a.classno+'|'+ltrim(b.bz)+'|'+STR_REPLACE(convert(VARCHAR(18),a.gxrq,102),'.','-') as GOODSINFO FROM dbo.coloth_t t LEFT JOIN cltypep a ON t.colthno=a.colthno LEFT JOIN cltypeb b ON a.typeno='001' AND a.classno=b.classno WHERE DATEDIFF(hour,convert(datetime, #{lastModDate}),t.gxrq) > 0 AND a.typeno='001' AND b.bz<>'' ORDER BY t.colthno")   //货号|款码|颜色值|名称|牌价|品牌|店码组|更新日期的混码
    List<String> selectAllSimpleGoods(String lastModDate);

    @Select("SELECT colthno AS goodsNo, colthname AS goodsName, rtrim(colthnob) AS styleNo, rtrim(colthname2) AS colorDesc, substring(talka,len(talka)-charindex('\\',reverse(talka))+2,1000) AS pic, ceiling(sprice) AS price FROM dbo.coloth_t WHERE colthno=#{goodsNo}")
    Goods selectGoods(String goodsNo);

    @Select("SELECT top 1 case when left(upper(talka),4)='\\\\OA' then '\\\\192.168.1.8'+substring(talka,5,1000) else '' end AS pic FROM dbo.coloth_t WHERE colthno=#{goodsNo}")
    String selectGoodsPictureName(String goodsNo);

    @Select("SELECT '00-'+gg_no AS size FROM dbo.coloth_t a LEFT JOIN gg b ON a.sizekind=b.sizekind WHERE colthno=#{goodsNo} HAVING '00-'+gg_no IN (select color_nos from coloth where colthno=#{goodsNo}) order by b.gg_print")
    List<String> selectGoodsSize(String goodsNo);

    @Select("select colthno from coloth_t where (isnull(colthnob,'')<>'' and colthnob in (select isnull(colthnob,'') from coloth_t where colthno=#{goodsNo})) or colthno=#{goodsNo}")
    List<String> selectGoodsNoInSameStyle(String goodsNo);
}
