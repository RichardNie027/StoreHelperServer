package com.tlg.storehelper.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleEntity<T> extends BaseResponseEntity {

    public List<T> result_list = new ArrayList<>();

    public Map<String, Object> result_map = new HashMap<>();
}