package com.tlg.storehelper.pojo;

import java.util.ArrayList;
import java.util.List;

public class SimpleListEntity<T> extends BaseResponseEntity {

    public List<T> result = new ArrayList<>();

}