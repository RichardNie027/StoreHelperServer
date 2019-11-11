package com.tlg.storehelper.controller;

import com.tlg.storehelper.entity.ds3.RunsaPosSales;
import com.tlg.storehelper.entity.ds3.RunsaPosStock;
import com.tlg.storehelper.entity.ds3.RunsaPosTransfer;
import com.tlg.storehelper.pojo.*;
import com.tlg.storehelper.service.ApiService;
import com.tlg.storehelper.service.BusinessService;
import com.tlg.storehelper.service.ErpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
public class ApiController {

    @Autowired
    private ApiService apiService;
    @Autowired
    private BusinessService businessService;
    @Autowired
    private ErpService erpService;

    //接收浪沙POS销售、实时库存、调拨单
    @RequestMapping(value = "/pre_api/uploadPosData", method = RequestMethod.POST)
    public BaseResponseVo uploadPosData(@RequestBody RunsaPosDataBean dataBean){
        erpService.saveRunsaPosData(dataBean);
        BaseResponseVo responseVo = new SimpleListMapResponseVo<String>();
        responseVo.setSuccessfulMessage("OK");
        return responseVo;
    }

    public static class RunsaPosDataBean {
        public List<RunsaPosSales> salesList = new ArrayList<>();
        public List<RunsaPosStock> stockList = new ArrayList<>();
        public List<RunsaPosTransfer> transferList = new ArrayList<>();
    }

    ////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////

    @RequestMapping(value = "/pre_api/login", method = RequestMethod.POST)
    public SimpleListMapResponseVo<String> loginValidation(@RequestBody LoginBean loginBean){
        return apiService.loginValidation(loginBean.username, loginBean.password);
    }

    private static class LoginBean {
        public String username;
        public String password;
        public LoginBean() {}
    }

    @RequestMapping("/api/getGoodsNeedUpdate")
    public BaseResponseVo getGoodsNeedUpdate(String lastModDate){
        return apiService.getGoodsNeedUpdate(lastModDate);
    }

    @RequestMapping("/api/getGoodsList")
    public GoodsInfoResponseVo getGoodsList(String lastModDate){
        return apiService.getGoodsList(lastModDate);
    }

    @RequestMapping("/api/getGoodsPopularity")
    public SimpleListResponseVo<GoodsPopularityVo> getGoodsPopularity(String storeCode){
        return apiService.getGoodsPopularity(storeCode);
    }

    @RequestMapping(value = "/api/uploadInventory", method = RequestMethod.POST)
    public BaseResponseVo uploadInventory(@RequestBody InventoryEntity inventoryEntity){
        return apiService.uploadInventory(inventoryEntity);
    }

    @RequestMapping("/api/getCollocation")
    public CollocationEntity getCollocation(String goodsNo){
        return apiService.getCollocation(goodsNo);
    }

    @RequestMapping("/api/getBestSelling")
    public SimplePageListResponseVo<GoodsSimpleVo> getBestSelling(String storeCode, String dim, int page, Integer pageSize){
        return apiService.getBestSelling(storeCode, dim, page, pageSize==null||pageSize<10 ? 10:pageSize);
    }

    @RequestMapping("/api/getStoreSelling")
    public SellingVo getStoreStock(String dim, String goodsNo, boolean includeSameStyle){
        return apiService.getStoreSelling(dim, goodsNo, includeSameStyle);
    }

    @RequestMapping("/api/getStorePsi")
    public SimpleListResponseVo<GoodsPsiVo> getStorePsi(String storeCode, String goodsNo){
        return apiService.getStorePsi(storeCode, goodsNo);
    }

    @RequestMapping("/api/getMembership")
    public SimpleListResponseVo<MembershipVo> getMembership(String membershipId, String storeCode){
        return apiService.getMembership(membershipId, storeCode);
    }

    @RequestMapping("/api/getMembershipShopHistory")
    public SimplePageListResponseVo<ShopHistoryVo> getMembershipShopHistory(String membershipId, String storeCode, int page){
        return apiService.getMembershipShopHistory(membershipId, storeCode, page);
    }

    /*
    @RequestMapping("/api/getGoodsList")
    public GoodsBarcodeEntity getGoodsBarcodeList(HttpServletRequest request, @RequestParam(value = "lastModDate", required = false) String lastModDate){
        String baseDateString = "20000101000000";
        lastModDate = lastModDate == null || lastModDate.isEmpty() ? baseDateString : lastModDate;
        try {
            new SimpleDateFormat ("yyyyMMddHHmmss").parse(lastModDate);
        } catch (ParseException e) {
            lastModDate = baseDateString;
        }
        return apiService.getGoodsBarcodeList(lastModDate);
    }*/

}
