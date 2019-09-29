package com.tlg.storehelper.dao.ds1;

import com.tlg.storehelper.entity.ds1.Goods;
import com.tlg.storehelper.entity.ds1.Membership;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface MembershipMapper {

    @Select("SELECT TOP 10 pmcode as membershipCardId, names as membershipName, telephone as mobile FROM pmmasc WHERE pmcode like #{membershipId} OR telephone like #{membershipId}")
    List<Membership> selectMembership(String membershipId);

}
