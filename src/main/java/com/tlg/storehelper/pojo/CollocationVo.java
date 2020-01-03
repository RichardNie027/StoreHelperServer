package com.tlg.storehelper.pojo;

import java.util.ArrayList;
import java.util.List;

public class CollocationVo extends BaseResponseVo {

    /**商品*/
    public GoodsSimpleVo goods;

    /**连带商品*/
    public List<GoodsSimpleVo> detail = new ArrayList<>();
}
