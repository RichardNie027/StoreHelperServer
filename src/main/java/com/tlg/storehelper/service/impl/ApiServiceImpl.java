package com.tlg.storehelper.service.impl;

import com.nec.lib.utils.ArrayUtil;
import com.nec.lib.utils.StringUtil;
import com.tlg.storehelper.entity.ds1.*;
import com.tlg.storehelper.pojo.*;
import com.tlg.storehelper.service.ApiService;
import com.tlg.storehelper.service.BusinessService;
import com.tlg.storehelper.service.ErpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApiServiceImpl implements ApiService {

    @Autowired
    private ErpService erpService;
    @Autowired
    private BusinessService businessService;
    @Value("${properties.api_service.defalut_goods_class}")
    private String DefaultGoodsClass;

    @Override
    public SimpleListMapResponseVo<String> loginValidation(String username, String password) {
        String baseAppRights = "X0X1X2";    //基本权限
        SimpleListMapResponseVo<String> responseVo = new SimpleListMapResponseVo();
        responseVo.map.put("minStockRatio", 2.0);   //正常库存下限，低于为库存少   vs 同款库存数/店数/同款货号数
        responseVo.map.put("maxStockRatio", 9.0);   //正常库存上限，高于为库存多   vs 同款库存数/店数/同款货号数
        ErpUser user = null;
        String storeCondition = ""; //授权店铺（店编与门店用户名相同）
        if(username!=null && password!=null) {
            try {
                //password = XxteaUtil.encryptBase64String(password, "UTF-8", "Passwd-Regent");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(username.length() == 6 && StringUtil.isNumeric(username)) {  //员工编号
                String[] checkResults = businessService.checkUserRights(username, password).split("\\|");//分组的最后字符串为空，数组成员2个
                //A|B|C, 用户名密码正确A=1 不正确A=0, B=SQL IN 子句或 LIKE 子句, C=2位一节的权限码
                //C：他店（单品精准库存K0，单品精准销量X0，客单数X1，客单件X2，单品精准销售金额X5，客单价X6）
                if(checkResults[0].equals("1")) {
                    user = new ErpUser();
                    user.userAccount = username;
                    user.username = username;
                    user.userId = username;
                    user.enabled = 1;
                    user.password = password;
                    user.type = 1;
                    storeCondition = checkResults[1];
                    responseVo.map.put("appRights", baseAppRights+(checkResults.length<3?"":checkResults[2]));
                }
            } else {  //店铺账号
                String pwd = businessService.getStoreDynamicPwd(username);
                if(pwd != null)
                    user = erpService.getUserByAccount(username);
                if(user != null)
                    user.password = pwd;
            }
        }
        if(user != null && user.password.equals(password)) {
            if(storeCondition.isEmpty()) {
                responseVo.code = 1005;
                responseVo.msg = "用户无授权门店";
                return responseVo;
            }
            if(user.type == 2) {
                responseVo.list.add(user.userAccount);
                responseVo.map.put("appRights", baseAppRights);    //默认基本权限：它店单品精准销量/客单数/客单件
            } else {
                for(ErpUser eachUser: erpService.getStoreUsersInAuthorization(storeCondition)) {
                    if(eachUser.userAccount.length() == 3)
                        responseVo.list.add(eachUser.userAccount);
                }
            }
            String token = businessService.registerLoginAndGetToken(username);
            responseVo.map.put("token", token);
            responseVo.setSuccessfulMessage("登录成功");
        } else {
            responseVo.code = 1005;
            responseVo.msg = "用户名密码错误";
        }
        return responseVo;
    }

    @Override
    public SimpleListResponseVo<ErpStore> getStoreList() {
        SimpleListResponseVo<ErpStore> responseVo = new SimpleListResponseVo<>();
        responseVo.list.addAll(erpService.getAllStore());
        if(responseVo.list.size() > 0)
            responseVo.setSuccessfulMessage("门店清单获取成功");
        else
            responseVo.setCodeAndMessage(2003, "未取得门店清单");
        return responseVo;
    }

    @Override
    public BaseResponseVo getGoodsNeedUpdate(String lastModDate) {
        BaseResponseVo responseVo = new BaseResponseVo();
        if(erpService.getGoodsNeedUpdate(lastModDate))
            responseVo.setSuccessfulMessage("1");
        else
            responseVo.setSuccessfulMessage("0");
        return responseVo;
    }

    @Override
    public GoodsInfoResponseVo getGoodsList(String lastModDate) {
        GoodsInfoResponseVo responseVo = new GoodsInfoResponseVo();
        String freshLastModDate = erpService.getGoodsBarcodeLastModDate();
        if(freshLastModDate.compareTo(lastModDate) > 0) {
            responseVo.lastModDate = freshLastModDate;
            responseVo.goodsList = erpService.getAllSimpleGoods(lastModDate);
            responseVo.goodsBarcodeList = erpService.getAllSimpleGoodsBarcodes(lastModDate);
        } else
            responseVo.lastModDate = "";
        if(responseVo.goodsList.isEmpty() && responseVo.goodsBarcodeList.isEmpty())
            responseVo.setSuccessfulMessage("商品资料已经是最新");
        else
            responseVo.setSuccessfulMessage("商品条码获取成功");
        return responseVo;
    }

    @Override
    public SimpleListResponseVo<GoodsPopularityVo> getGoodsPopularity(String storeCode) {
        SimpleListResponseVo<GoodsPopularityVo> responseVo = new SimpleListResponseVo<GoodsPopularityVo>();
        responseVo.list = erpService.getGoodsPopularity(storeCode);
        responseVo.setSuccessfulMessage("商品热度获取成功");
        return responseVo;
    }

    /**品牌首字关联的所有品牌类别、年份、季节、大类、价格带*/
    @Override
    public SimpleListResponseVo<String> getGoodsClassList(String brandKey) {
        SimpleListResponseVo<String> responseVo = new SimpleListResponseVo<String>();
        responseVo.list = erpService.getGoodsClassList(brandKey);
        responseVo.list.add(DefaultGoodsClass); //预设分类默认(DEFAULT作为与季节等分类并列的类别)，DEFAULT|005303,005306,005307
        responseVo.setSuccessfulMessage("商品分类获取成功");
        return responseVo;
    }

    @Override
    public SimplePageListResponseVo<String> getGoodsNoListByClass(String brandCodes, String yearCode, String seasonCode, String classCode, String priceCode, int page, Integer pageSize) {
        List<String> resultList = new ArrayList<>();
        int recordCount = erpService.getGoodsNoListByClass(resultList,brandCodes, yearCode, seasonCode, classCode, priceCode, pageSize, page);
        SimplePageListResponseVo<String> responseVo = new SimplePageListResponseVo<String>(page, (int)Math.ceil((double)recordCount/pageSize), pageSize, recordCount);
        responseVo.list.addAll(resultList);
        responseVo.setSuccessfulMessage("商品编号集合获取成功");
        return responseVo;
    }

    @Override
    public BaseResponseVo uploadInventory(InventoryEntity inventoryEntity) {
        String result = businessService.uploadInventory(inventoryEntity);
        BaseResponseVo responseVo = new BaseResponseVo();
        if(result.equals(""))
            responseVo.setSuccessfulMessage("上传成功");
        else {
            responseVo.code = 2005;
            responseVo.msg = "盘点单保存失败";
        }
        return responseVo;
    }

    @Override
    public CollocationVo getCollocation(String goodsNo, String storeCodes, String dim) {
        Goods goods = erpService.getGoods(goodsNo);
        CollocationVo collocationVo = new CollocationVo();
        if(goods == null) {
            collocationVo.setCode(2003);
            collocationVo.setMsg("货号/条码不存在");
            return collocationVo;
        }
        collocationVo.goods = new GoodsSimpleVo(goodsNo,goods.goodsName,goods.price,0,0,"", 1, "");

        List<Selling> list = erpService.getCollocation(goodsNo, storeCodes, dim);
        List<String> goodsNoList = list.stream().limit(24).map(selling -> selling.goodsNo).collect(Collectors.toList());
        goodsNoList.add(goodsNo);
        List<KV<String,Integer>> stockList = erpService.getTotalStock(goodsNoList, true);
        List<KV<String,Integer>> numberList = erpService.getGoodsNumberInSameStyle(goodsNoList);
        KV<String,Integer> kv0 = numberList.stream().filter(kv->kv.k.equals(goodsNo)).findFirst().get();
        if(kv0 != null)
            collocationVo.goods.sameStyleCount = kv0.v;
        list.stream().limit(24).forEach(selling -> {
            KV<String,Integer> vo1 = stockList.stream().filter(kv->kv.k.equalsIgnoreCase(selling.goodsNo)).findFirst().get();
            KV<String,Integer> vo2 = numberList.stream().filter(kv->kv.k.equalsIgnoreCase(selling.goodsNo)).findFirst().get();
            GoodsSimpleVo detailBean = new GoodsSimpleVo(selling.goodsNo,"",selling.price,selling.quantity,vo1!=null?vo1.v:0,"", vo2.v, "");
            collocationVo.detail.add(detailBean);
        });
        collocationVo.setSuccessfulMessage("搭配获取成功");
        return collocationVo;
    }

    @Override
    public SimplePageListResponseVo<CollocationPairVo> getPairCollocation(String storeCodes, String dimension, String salesCode, int page, int pageSize) {
        List<PairCollocation> list = new ArrayList<>();
        int recordCount = erpService.getPairCollocation(list, storeCodes, dimension, salesCode, pageSize, page);
        SimplePageListResponseVo<CollocationPairVo> responseVo = new SimplePageListResponseVo<CollocationPairVo>(page, (int)Math.ceil((double)recordCount/pageSize), pageSize, recordCount);
        List<String> goodsNoList = list.stream().map(pair -> pair.goodsNo1).collect(Collectors.toList());
        List<String> goodsNoList2 = list.stream().map(pair -> pair.goodsNo2).collect(Collectors.toList());
        goodsNoList.addAll(goodsNoList2);
        List<String> goodsNoListAll = goodsNoList.parallelStream().distinct().collect(Collectors.toList());
        List<KV<String,Integer>> stockList = erpService.getTotalStock(goodsNoListAll, true);
        List<KV<String,Integer>> numberList = erpService.getGoodsNumberInSameStyle(goodsNoList);
        list.stream().forEach(pair -> {
            CollocationPairVo pairVo = new CollocationPairVo();
            KV<String,Integer> vo1 = stockList.stream().filter(kv->kv.k.equalsIgnoreCase(pair.goodsNo1)).findFirst().get();
            KV<String,Integer> vo2 = stockList.stream().filter(kv->kv.k.equalsIgnoreCase(pair.goodsNo2)).findFirst().get();
            KV<String,Integer> vo1b = numberList.stream().filter(kv->kv.k.equalsIgnoreCase(pair.goodsNo1)).findFirst().get();
            KV<String,Integer> vo2b = numberList.stream().filter(kv->kv.k.equalsIgnoreCase(pair.goodsNo2)).findFirst().get();
            pairVo.goods1 = new GoodsSimpleVo(pair.goodsNo1, "", pair.price1, 0, vo1!=null?vo1.v:0, "", vo1b!=null?vo1b.v:0, "");
            pairVo.goods2 = new GoodsSimpleVo(pair.goodsNo2, "", pair.price2, 0, vo2!=null?vo2.v:0, "", vo2b!=null?vo2b.v:0, "");
            pairVo.frequency = pair.quantity;
            responseVo.list.add(pairVo);
        });
        responseVo.setSuccessfulMessage("盟店搭配获取成功");
        return responseVo;
    }

    @Override
    public SimplePageListResponseVo<GoodsSimpleVo> getBestSelling(String storeCodes, String dimension, String salesCode, int floorNumber, String sort, int page, int pageSize) {
        List<Selling> list = new ArrayList<>();
        int recordCount = erpService.getBestSelling(list, storeCodes, dimension, salesCode, floorNumber, sort, pageSize, page);
        SimplePageListResponseVo<GoodsSimpleVo> responseVo = new SimplePageListResponseVo<GoodsSimpleVo>(page, (int)Math.ceil((double)recordCount/pageSize), pageSize, recordCount);
        if(list == null) {
            responseVo.setCodeAndMessage(404, "内部错误");
            return responseVo;
        }
        List<String> goodsNoList = new ArrayList<>();
        list.forEach(selling -> {
            goodsNoList.add(selling.goodsNo);
        });
        List<KV<String, Integer>> stockList = erpService.getTotalStock(goodsNoList, true);
        List<KV<String,Integer>> numberList = erpService.getGoodsNumberInSameStyle(goodsNoList);
        list.stream().forEach(selling -> {
            KV<String,Integer> vo1 = stockList.stream().filter(kv->kv.k.equalsIgnoreCase(selling.goodsNo)).findFirst().get();
            KV<String,Integer> vo2 = numberList.stream().filter(kv->kv.k.equalsIgnoreCase(selling.goodsNo)).findFirst().get();
            responseVo.list.add(new GoodsSimpleVo(selling.goodsNo, "", selling.price, selling.quantity, vo1!=null?vo1.v:0, "", vo2!=null?vo2.v:0,""));
        });
        responseVo.setSuccessfulMessage("畅销款获取成功");
        return responseVo;
    }

    @Override
    public SimpleListResponseVo<String> getSalesList(String storeCode) {
        SimpleListResponseVo<String> responseVo = new SimpleListResponseVo<String>();
        responseVo.list = erpService.getSalesList(storeCode);
        responseVo.setSuccessfulMessage("导购列表获取成功");
        return responseVo;
    }

    @Override
    public SimplePageListResponseVo<SalesSelling> getBestSalesSelling(String storeCodes, String dimension, String sort, int page, int pageSize) {
        List<SalesSelling> list = new ArrayList<>();
        int recordCount = erpService.getBestSalesSelling(list, storeCodes, dimension, sort, pageSize, page);
        SimplePageListResponseVo<SalesSelling> responseVo = new SimplePageListResponseVo<>(page, (int)Math.ceil((double)recordCount/pageSize), pageSize, recordCount);
        responseVo.list.addAll(list);
        responseVo.setSuccessfulMessage("导购销售排名获取成功");
        return responseVo;
    }

    @Override
    public SellingVo getSelling(String dimension, String goodsNo, boolean includeSameStyle) {
        SellingVo responseVo = new SellingVo();
        Goods goods = erpService.getGoods(goodsNo);
        if(goods == null) {
            responseVo.setCode(2003);
            responseVo.setMsg("货号/条码不存在");
            return responseVo;
        }
        List<String> goodsNoList = erpService.getAllGoodsNoInSameStyle(goodsNo);
        String sibling = "";
        for(String no : goodsNoList) {
            if(sibling.isEmpty())
                sibling = sibling + no;
            else
                sibling = sibling + "|" + no;
        }
        responseVo.goodsNo = goodsNo;
        responseVo.goodsName = goods.goodsName;
        responseVo.price = goods.price;
        responseVo.sibling = sibling;
        responseVo.hasSibling = goodsNoList.size() > 1;

        erpService.getSelling(dimension, goodsNo, includeSameStyle).forEach(x->{
            SellingVo.DetailBean detailBean = new SellingVo.DetailBean(x.storeCode, x.quantity);
            responseVo.detail.add(detailBean);
        });
        responseVo.setSuccessfulMessage("销售获取成功");
        return responseVo;
    }

    @Override
    public SimpleListResponseVo<GoodsPsiVo> getStorePsi(String storeCode, String goodsNo) {
        SimpleListResponseVo<GoodsPsiVo> responseVo = new SimpleListResponseVo();
        //根据goodsNo找同款的所有goodsNo
        erpService.getAllGoodsNoInSameStyle(goodsNo).forEach(x->{
            GoodsPsiVo goodsPsiVo;
            Goods goods = erpService.getGoods(x);
            if(goods!=null)
                goodsPsiVo = new GoodsPsiVo(x, goods.goodsName, goods.styleNo, goods.colorDesc);
            else
                goodsPsiVo = new GoodsPsiVo(x, "", "", "");
            List<GoodsSizePsiVo> goodsSizePsiVoList = erpService.getRunsaPosStock(storeCode, x);
            int goodsSizePsiCount = 0;  //所有店铺无库存无销售，则排除
            for (GoodsSizePsiVo goodsSizePsiVo_:goodsSizePsiVoList) {
                goodsSizePsiCount += goodsSizePsiVo_.psiMap.size();
            }
            if(goodsSizePsiCount == 0)
                return;
            goodsPsiVo.goodsSizePsiList.addAll(goodsSizePsiVoList);
            if(x.equalsIgnoreCase(goodsNo))
                responseVo.list.add(0, goodsPsiVo);
            else
                responseVo.list.add(goodsPsiVo);
        });
        responseVo.setSuccessfulMessage("进销存获取成功");
        return responseVo;
    }

    public SimpleListResponseVo<MembershipVo> getMembership(String membershipId, String storeCode) {
        SimpleListResponseVo<MembershipVo> responseVo = new SimpleListResponseVo();
        List<MembershipVo> membershipList = erpService.getMembership(membershipId);
        if(membershipList == null || membershipList.size() == 0) {
            responseVo.code = 2001;
            responseVo.msg = "会员未识别";
            return responseVo;
        }
        responseVo.list.addAll(membershipList);
        responseVo.setSuccessfulMessage("会员信息获取成功");
        return responseVo;
    }

    @Override
    public SimplePageListResponseVo<ShopHistoryVo> getMembershipShopHistory(String membershipId, String storeCode, int page) {
        List<ShopHistoryVo> list = new ArrayList<>();
        int pageSize = 10;
        int recordCount = erpService.getShopHistory(list, membershipId, pageSize, page);
        SimplePageListResponseVo<ShopHistoryVo> responseVo = new SimplePageListResponseVo<ShopHistoryVo>(page, (int)Math.ceil((double)recordCount/pageSize), pageSize, recordCount);
        responseVo.list.addAll(list);
        responseVo.setSuccessfulMessage("会员信息获取成功");
        return responseVo;
    }

    /*
    @Override
    public GoodsBarcodeEntity getGoodsBarcodeList(String lastModDate) {
        GoodsBarcodeEntity entity = erpService.getLastestGoodsBarcodes(lastModDate);
        entity.setSuccessfulMessage("商品取得成功");

        return entity;
    }*/
}
