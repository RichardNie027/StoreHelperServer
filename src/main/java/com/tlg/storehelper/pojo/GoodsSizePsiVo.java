package com.tlg.storehelper.pojo;

import java.io.Serializable;
import java.util.*;

public class GoodsSizePsiVo implements Serializable {
    /**尺码*/
    public String size;
    /**各店铺进销存*/
    public Map<String,PsiVo> psiMap = new TreeMap<String,PsiVo>();

    public GoodsSizePsiVo(String size) {
        this.size = size;
    }
}
