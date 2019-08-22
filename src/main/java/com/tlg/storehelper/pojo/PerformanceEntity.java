package com.tlg.storehelper.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PerformanceEntity implements Serializable {

    /**营业员编号*/
    public String personCode;
    /**营业员*/
    public String person;
    /**销售数量*/
    public int quantity;
    /**销售额*/
    public int salesAmount;
    /**提成*/
    public int allowance;

}
