package com.tlg.storehelper.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleListMapResponseVo<T> extends BaseResponseVo {

    public List<T> list = new ArrayList<>();

    public Map<String, Object> map = new HashMap<>();
}