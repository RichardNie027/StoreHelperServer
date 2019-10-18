package com.tlg.storehelper.pojo;

import java.util.ArrayList;
import java.util.List;

public class SimpleListResponseVo<T> extends BaseResponseVo {

    public List<T> list = new ArrayList<>();

}