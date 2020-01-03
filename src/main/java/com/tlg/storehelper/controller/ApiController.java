package com.tlg.storehelper.controller;

import com.tlg.storehelper.entity.ds1.ErpStore;
import com.tlg.storehelper.entity.ds1.SalesSelling;
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

    @RequestMapping("/api/getStoreAll")
    public SimpleListResponseVo<ErpStore> getGoodsList(){
        return apiService.getStoreList();
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
    public CollocationVo getCollocation(String goodsNo, String storeCodes, String dim){
        dim = dim == null || dim.isEmpty() ? "45" : dim;
        return apiService.getCollocation(goodsNo, storeCodes, dim);
    }

    @RequestMapping("/api/getPairCollocation")
    public SimplePageListResponseVo<CollocationPairVo> getPairCollocation(String storeCodes, String dim, String salesCode, int page, Integer pageSize){
        dim = dim == null || dim.isEmpty() ? "45" : dim;
        return apiService.getPairCollocation(storeCodes, dim, salesCode, page, pageSize);
    }

    @RequestMapping("/api/getSalesList")
    public SimpleListResponseVo<String> getSalesList(String storeCode){
        return apiService.getSalesList(storeCode);
    }

    @RequestMapping("/api/getBestSelling")
    public SimplePageListResponseVo<GoodsSimpleVo> getBestSelling(String storeCodes, String dim, String salesCode, int floorNumber, int page, Integer pageSize){
        return apiService.getBestSelling(storeCodes, dim, salesCode, floorNumber, page, pageSize==null||pageSize<10 ? 10:pageSize);
    }

    @RequestMapping("/api/getBestSalesSelling")
    public SimplePageListResponseVo<SalesSelling> getBestSalesSelling(String storeCodes, String dim, int page, Integer pageSize){
        return apiService.getBestSalesSelling(storeCodes, dim, page, pageSize==null||pageSize<10 ? 10:pageSize);
    }

    @RequestMapping("/api/getSelling")
    public SellingVo getSelling(String dim, String goodsNo, boolean includeSameStyle){
        return apiService.getSelling(dim, goodsNo, includeSameStyle);
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
