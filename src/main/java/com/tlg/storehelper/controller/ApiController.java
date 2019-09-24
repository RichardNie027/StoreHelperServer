package com.tlg.storehelper.controller;

import com.tlg.storehelper.pojo.*;
import com.tlg.storehelper.service.ApiService;
import com.tlg.storehelper.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
public class ApiController {

    @Autowired
    private ApiService apiService;
    @Autowired
    private BusinessService businessService;

    /**
    //测试POS销售和实时库存POST接受
    @RequestMapping(value = "/pre_api/uploadPosData", method = RequestMethod.POST)
    public SimpleEntity<String> test(@RequestBody TestBean testBean){
        SimpleEntity<String> se = new SimpleEntity<String>();
        se.setSuccessfulMessage("OK");
        return se;
    }

    public static class TestBean {
        public List<TestBean1> salesList = new ArrayList<>();
        public List<TestBean2> stockList = new ArrayList<>();
    }
    public static class TestBean1 {
        public String cusno;
        public String nos;
        public String names;
        public String salescode;
        public String colthno;
        public String color;
        public int price;
        public int proprice;
        public int nb;
        public String intime;
    }
    public static class TestBean2 {
        public String dbno;
        public String colthno;
        public String color;
        public int nb;
    }
    */

    @RequestMapping(value = "/pre_api/login", method = RequestMethod.POST)
    public SimpleEntity<String> loginValidation(@RequestBody LoginBean loginBean){
        return apiService.loginValidation(loginBean.username, loginBean.password);
    }

    public static class LoginBean {
        public String username;
        public String password;
        public LoginBean() {}
    }

    @RequestMapping("/api/getGoodsBarcodeList")
    public SimpleEntity<String> getGoodsBarcodeList(String lastModDate){
        return apiService.getGoodsBarcodeList(lastModDate);
    }

    @RequestMapping(value = "/api/uploadInventory", method = RequestMethod.POST)
    public BaseResponseEntity uploadInventory(@RequestBody InventoryEntity inventoryEntity){
        return apiService.uploadInventory(inventoryEntity);
    }

    @RequestMapping("/api/getCollocation")
    public CollocationEntity getCollocation(String goodsNo){
        return apiService.getCollocation(goodsNo);
    }

    @RequestMapping("/api/getBestSelling")
    public SimpleListPageEntity<GoodsSimpleVo> getBestSelling(String storeCode, String dim, int page){
        return apiService.getBestSelling(storeCode, dim, page);
    }

    @RequestMapping("/api/getStoreStock")
    public SimpleEntity<StockVo> getStoreStock(String storeCode, String goodsNo){
        return apiService.getStoreStock(storeCode, goodsNo);
    }

    @RequestMapping("/api/getMembershipShopHistory")
    public ShopHistoryEntity getMembershipShopHistory(String membershipId, String storeCode){
        return apiService.getMembershipShopHistory(membershipId, storeCode);
    }

    @RequestMapping("/api/getMembershipShopHistoryDetail")
    public SimpleListPageEntity<ShopHistoryDetailVo> getMembershipShopHistoryDetail(String membershipId, String storeCode, int page){
        return apiService.getMembershipShopHistoryDetail(membershipId, storeCode, page);
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
