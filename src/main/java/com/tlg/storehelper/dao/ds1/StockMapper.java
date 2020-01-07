package com.tlg.storehelper.dao.ds1;

import com.tlg.storehelper.pojo.KV;
import org.apache.ibatis.annotations.*;

import java.util.List;

@CacheNamespace(readWrite = true, flushInterval = 60000)
public interface StockMapper {

    /**总仓SKU库存*/
    @Select("SELECT isnull(sum(rnb-tempoutnb),0) as quantity FROM coloth_f WHERE ckaccno='Z000' and colthno=#{goodsNo} and color_nos=#{size}")
    int selectWarehouseStockBySKU(String goodsNo, String size);

    /**总仓+店铺的同款商品的库存*/
    @Select("<script> SELECT t.colthno as k,isnull(g1.quantity,0)+isnull(g2.quantity,0) as v FROM coloth_t t left join ("+
            "  SELECT b.colthnob, sum(a.nb+a.tempnbout) as quantity FROM dba a left join coloth_t b on a.colthno=b.colthno GROUP BY b.colthnob HAVING isnull(b.colthnob,'')&lt;&gt;''"+
            ") g1 on t.colthnob=g1.colthnob left join ("+
            "  SELECT b.colthnob, sum(rnb-tempoutnb) as quantity FROM coloth_f a left join coloth_t b on a.colthno=b.colthno WHERE ckaccno='Z000' GROUP BY b.colthnob HAVING isnull(b.colthnob,'')&lt;&gt;''"+
            ") g2 on t.colthnob=g2.colthnob "+
            "<if test='list!=null and list.size>0'>"+
            "WHERE t.colthno in "+
            "<foreach item='item' index='index' collection='list' open='(' separator=',' close=')'> "+
            " #{item} "+
            "</foreach> "+
            "</if>"+
            "<if test='list==null or list.size==0'>"+
            "WHERE t.colthno in ('')"+
            "</if>"+
            "</script>")
    List<KV<String,Integer>> selectTotalStockByStyle(List<String> goodsNos);

}
