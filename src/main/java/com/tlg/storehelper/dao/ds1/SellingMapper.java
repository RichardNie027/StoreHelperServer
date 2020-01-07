package com.tlg.storehelper.dao.ds1;

import com.tlg.storehelper.entity.ds1.PairCollocation;
import com.tlg.storehelper.entity.ds1.SalesSelling;
import com.tlg.storehelper.entity.ds1.Selling;
import com.tlg.storehelper.entity.ds1.StoreSelling;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@CacheNamespace(readWrite = true, flushInterval = 60000)
public interface SellingMapper {

    /**按货号查询所有店铺的销售*/
    @Select("select substring(a.cusno,2,3) as storeCode, sum(b.nb) as quantity from u2sale a left join u2saleb b on a.nos = b.nos where b.colthno=#{goodsNo} and a.outdate>=#{startDate8} and b.nb>0 group by a.cusno having sum(b.nb)>0 order by sum(b.nb) desc")
    List<StoreSelling> selectStoreSellingByGoodsNo(String goodsNo, String startDate8);

    /**按款号查询所有店铺的销售*/
    @Select("select substring(a.cusno,2,3) as storeCode, sum(b.nb) as quantity from u2sale a left join u2saleb b on a.nos = b.nos left join coloth_t t on b.colthno=t.colthno where t.colthnob=#{styleNo} and a.outdate>=#{startDate8} and b.nb>0 group by a.cusno having sum(b.nb)>0 order by sum(b.nb) desc")
    List<StoreSelling> selectStoreSellingByStyleNo(String styleNo, String startDate8);

    /** 导购列表 */
    @Select("select codes+' '+names from zg_sales where substring(dbno,2,3)=#{storeCode} order by codes")
    List<String> selectSalesList(String storeCode);

    /** 销售排行（畅销款）——总数；brand 和 storeCodes 二选一，不选设null*/
    @Select("<script> SELECT COUNT(count(0)) AS num FROM dbo.u2saleb a LEFT JOIN coloth_t b ON a.colthno=b.colthno" +
            "  where nos in (select distinct nos from dbo.u2sale where " +
            " <if test='brand!=null and brand!=\"\"'>"+
            "  substring(cusno,2,3) like #{brand}+'__' and "+
            " </if>"+
            " <if test='storeCodes!=null and storeCodes.size &gt; 0'>"+
            "  substring(cusno,2,3) in " +
            "<foreach item='item' index='index' collection='storeCodes' open='(' separator=',' close=')'> " +
            " #{item}"+
            "</foreach> and "+
            " </if>"+
            "  outdate &gt;= #{startDate})" +
            " <if test='salesCode!=null and salesCode!=\"\"'>"+
            " and a.salescode=#{salesCode}"+
            " </if>"+
            "  group by b.colthnob"+
            "  having sum(nb)>=#{floorNumber}"+
            "</script>")
    int selectBestSellingCount(String startDate, String brand, @Param("storeCodes")List<String> storeCodes, String salesCode, int floorNumber);

    //    @Select("SELECT TOP #{pageSize} dim,brand,goodsNo,t2.sprice as price,quantity FROM " + TABLE + " t1 LEFT JOIN OA.dbo.RUNSA_Coloth_t t2 ON t1.goodsNo=t2.colthno WHERE dim=#{dim} AND brand=#{brand} AND goodsNo NOT IN (SELECT TOP #{start} goodsNo FROM " + TABLE + " WHERE dim=#{dim} AND brand=#{brand} ORDER BY quantity DESC,goodsNo) ORDER BY quantity DESC,goodsNo")
    /** 销售排行（畅销款）——分页结果，start起始序号为pageSize * page */
    @Select("exec dbo.select_by_page @sql=#{sql,mode=IN,jdbcType=VARCHAR},@start=#{start,mode=IN,jdbcType=INTEGER},@pageSize=#{pageSize,mode=IN,jdbcType=INTEGER}")
    List<Selling> selectBestSelling(String sql, int pageSize, int start);

    /** 导购销售排名——总数；brand 和 storeCodes 二选一，不选设null*/
    @Select("<script> SELECT COUNT(count(0)) AS num FROM dbo.u2saleb b LEFT JOIN dbo.u2sale a ON b.nos=a.nos" +
            " left join zg_sales s on b.salescode=s.codes"+
            " left join db on a.cusno=db.dbno"+
            " left join (select dbno,empid,count(0) as days from KQ_Schedule where "+
            " <if test='brand!=null and brand!=\"\"'>"+
            "  substring(dbno,2,3) like #{brand}+'__' and "+
            " </if>"+
            " <if test='storeCodes!=null and storeCodes.size &gt; 0'>"+
            "  substring(dbno,2,3) in " +
            "<foreach item='item' index='index' collection='storeCodes' open='(' separator=',' close=')'> " +
            " #{item}"+
            "</foreach> and "+
            " </if>"+
            " eday &gt;= #{startDate} and eday &lt; current_date() group by dbno,empid) sch on a.cusno=sch.dbno and b.salescode=sch.empid"+
            "  WHERE " +
            " <if test='brand!=null and brand!=\"\"'>"+
            "  substring(a.cusno,2,3) like #{brand}+'__' and "+
            " </if>"+
            " <if test='storeCodes!=null and storeCodes.size &gt; 0'>"+
            "  substring(a.cusno,2,3) in " +
            "<foreach item='item' index='index' collection='storeCodes' open='(' separator=',' close=')'> " +
            " #{item}"+
            "</foreach> and "+
            " </if>"+
            "  a.outdate &gt;= #{startDate}" +
            " group by b.salescode,a.cusno,s.names,db.class2"+
            " having sum(b.nb) &gt; 0 and max(sch.days) &gt; 0"+
            "</script>")
    int selectBestSalesSellingCount(String startDate, String brand, @Param("storeCodes")List<String> storeCodes);

    /** 导购销售排名——分页结果，start起始序号为pageSize * page */
    @Select("exec dbo.select_by_page @sql=#{sql,mode=IN,jdbcType=VARCHAR},@start=#{start,mode=IN,jdbcType=INTEGER},@pageSize=#{pageSize,mode=IN,jdbcType=INTEGER}")
    List<SalesSelling> selectBestSalesSelling(String sql, int pageSize, int start);

    /** 商品连带推荐（基于款号）——所有记录，startDate格式yyyy-MM-dd */
    @Select("<script> select min(c.colthno) as goodsNo,min(t2.sprice) as price,sum(c.nb) as quantity,sum(c.endprice*c.nb) as money"+
            " from u2sale a inner join u2saleb b on a.nos=b.nos and b.nb &gt; 0 inner join coloth_t t on b.colthno=t.colthno"+
            " left join u2saleb c on a.nos=c.nos and c.nb &gt; 0 inner join coloth_t t2 on c.colthno=t2.colthno"+
            " where t.colthnob=#{styleNo} and t2.colthnob &lt;&gt; #{styleNo} and a.outdate &gt;= #{startDate}"+
            " <if test='storeCodes!=null and storeCodes.size &gt; 0'>"+
            " and substring(a.cusno,2,3) in "+
            "<foreach item='item' index='index' collection='storeCodes' open='(' separator=',' close=')'> " +
            " #{item}"+
            "</foreach> "+
            " </if>"+
            " group by t2.colthnob having sum(c.nb) &gt; 1 order by sum(c.nb) desc"+
            "</script>")
    List<Selling> selectCollocationByStyleNo(String startDate, String styleNo, @Param("storeCodes")List<String> storeCodes);

    /** 商品成对搭配——所有记录；brand 和 storeCodes 二选一，不选设null，salesCode可为null */
    @Select("<script> select min(b.colthno) as goodsNo1, max(t1.sprice) as price1, min(c.colthno) as goodsNo2, max(t2.sprice) as price2, sum(case when b.nb &gt; c.nb then b.nb else c.nb end) as quantity"+
            " from u2sale a left join u2saleb b on a.nos = b.nos and b.nb>0"+
            " left join u2saleb c on a.nos = c.nos  and c.nb &gt; 0"+
            " left join dbo.coloth_t t1 on b.colthno=t1.colthno"+
            " left join dbo.coloth_t t2 on c.colthno=t2.colthno"+
            " where a.outdate &gt;= #{startDate}"+
            " <if test='brand!=null and brand!=\"\"'>"+
            " and a.cusno like '_'+#{brand}+'__' "+
            " </if>"+
            " <if test='storeCodes!=null and storeCodes.size &gt; 0'>"+
            " and substring(a.cusno,2,3) in "+
            "<foreach item='item' index='index' collection='storeCodes' open='(' separator=',' close=')'> " +
            " #{item} "+
            "</foreach> "+
            " </if>"+
            " <if test='salesCode!=null and salesCode!=\"\"'>"+
            " and b.salescode=#{salesCode}"+
            " </if>"+
            " and t1.colthnob &lt;&gt; '' and t1.colthnob &lt; t2.colthnob"+
            " group by t1.colthnob,t2.colthnob"+
            " order by quantity desc "+
            "</script>")
    List<PairCollocation> selectPairCollocation(String startDate, String brand, @Param("storeCodes")List<String> storeCodes, String salesCode);

}
