package com.tlg.storehelper.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleEntity<T> extends BaseResponseEntity {

    public List<T> resultList = new ArrayList<>();

    public Map<String, Object> resultMap = new HashMap<>();
}