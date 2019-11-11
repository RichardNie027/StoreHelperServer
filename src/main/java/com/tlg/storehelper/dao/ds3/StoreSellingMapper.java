package com.tlg.storehelper.dao.ds3;

import com.tlg.storehelper.entity.ds3.StoreSelling;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface StoreSellingMapper {

    @Select(value = "exec OA.dbo.sp_selling_by_goods "
                    +"@dim=#{dim,mode=IN,jdbcType=VARCHAR},"
                    +"@goodsNo=#{goodsNo,mode=IN,jdbcType=VARCHAR},"
                    +"@includeSameStyle=#{includeSameStyle,mode=IN,jdbcType=BIT}")
    List<StoreSelling> selectStoreSelling(String dim, String goodsNo, boolean includeSameStyle);
}
