package com.tlg.storehelper.dao.ds1;

import com.tlg.storehelper.entity.ds1.ErpStore;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/***
 * 店铺
 */
@CacheNamespace(readWrite = false, flushInterval = 3600000)
public interface ErpStoreMapper {

    /** return List<ErpStore> */
    @Select("select substring(dbno,2,3) as storeCode, db_level.levelKey as storeLevel, class2 as storeLevelText from db left join db_level on db.class2=db_level.levelValue where kinds1=0 and closeif=0 and fittime > getdate() and substring(dbno,2,3)<>'101' order by substring(dbno,2,1),db_level.levelKey")
    List<ErpStore> selectAllStore();

}
