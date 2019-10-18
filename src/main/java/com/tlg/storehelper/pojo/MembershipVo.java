package com.tlg.storehelper.pojo;

import java.io.Serializable;

public class MembershipVo implements Serializable {
    /**卡号*/
    public String membershipCardId;
    /**姓名*/
    public String membershipName;
    /**手机号*/
    public String mobile;
    /**单次消费*/
    public int perExpenditure;
    /**年度消费*/
    public int yearExpenditure;
    /**总消费额*/
    public int totalExpenditure;

}
