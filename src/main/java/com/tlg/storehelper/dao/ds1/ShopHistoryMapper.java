package com.tlg.storehelper.dao.ds1;

import com.tlg.storehelper.entity.ds1.Membership;
import com.tlg.storehelper.pojo.ShopHistoryItemVo;
import com.tlg.storehelper.pojo.ShopHistoryVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 消费记录
 */
public interface ShopHistoryMapper {

    @Select("SELECT COUNT(*) AS num FROM u2sale WHERE codes=#{membershipCardId}")
    int selectShopHistoryCount(String membershipCardId);

    @Select("exec dbo.select_by_page @sql=#{sql,mode=IN,jdbcType=VARCHAR},@start=#{start,mode=IN,jdbcType=INTEGER},@pageSize=#{pageSize,mode=IN,jdbcType=INTEGER}")
    List<ShopHistoryVo> selectShopHistory(String sql, int start, int pageSize);

    // “<>” 转义为 &lt;&gt;
    @Select("<script> select a.nos as listNo, a.colthno as goodsNo, a.color as size, b.colthname as goodsName, a.nb as quantity, a.price, ROUND(a.pers,1) as discount, a.proprice as realPrice, a.nb*a.proprice as amount, c.names+' '+a.salescode as sales from u2saleb a left join coloth_t b on a.colthno=b.colthno left join zg_sales c on a.salescode=c.codes where a.price &lt;&gt; 1 and a.nos in "+
            "<foreach item='item' index='index' collection='listNos' open='(' separator=',' close=')'> " +
            " #{item} "+
            "</foreach> "+
            "order by nos "+
            "</script>")
    List<ShopHistoryItemVo> selectShopHistoryItems(@Param("listNos") List<String> listNos);

    @Select("SELECT round(isnull(SUM(now_real),0)/case when count(*)=0 then 1 else count(*) end,0) AS money FROM dbo.u2sale WHERE codes=#{membershipCardId} AND nb>0")
    int selectPerExpenditure(String membershipCardId);

    @Select("SELECT isnull(SUM(now_real),0) AS money FROM dbo.u2sale WHERE codes=#{membershipCardId} AND outdate>=#{date8From} AND outdate<=#{date8To}")
    int selectPeriodExpenditure(String membershipCardId, String date8From, String date8To);

    @Select("SELECT isnull(SUM(points),0) AS money FROM dbo.pmmasc_initial_points WHERE pmcode=#{membershipCardId} AND year=#{year}")
    int selectInitialExpenditure(String membershipCardId, int year);
}
