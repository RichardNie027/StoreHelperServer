package com.tlg.storehelper.service.impl;

import com.google.gson.Gson;
import com.nec.lib.utils.BeanUtil;
import com.nec.lib.utils.DateUtil;
import com.nec.lib.utils.NetUtil;
import com.tlg.storehelper.controller.ApiController;
import com.tlg.storehelper.dao.ds1.*;
import com.tlg.storehelper.dao.ds2.SimpleMapper;
import com.tlg.storehelper.dao.ds3.*;
import com.tlg.storehelper.entity.ds1.ErpUser;
import com.tlg.storehelper.entity.ds1.Goods;
import com.tlg.storehelper.entity.ds1.Membership;
import com.tlg.storehelper.entity.ds3.*;
import com.tlg.storehelper.pojo.*;
import com.tlg.storehelper.service.ErpService;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ErpServiceImpl implements ErpService {

    @Autowired
    private ErpUserMapper erpUserMapper;
    @Autowired
    private GoodsBarcodeMapper goodsBarcodeMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private MembershipMapper membershipMapper;
    @Autowired
    private ShopHistoryMapper shopHistoryMapper;
    @Autowired
    private CollocationMapper collocationMapper;
    @Autowired
    private BestSellingMapper bestSellingMapper;
    @Autowired
    private StoreSellingMapper storeSellingMapper;
    @Autowired
    private ErpSimpleMapper erpSimpleMapper;
    @Autowired
    private SimpleMapper simpleMapper;
    @Autowired
    private RunsaPosSalesMapper runsaPosSalesMapper;
    @Autowired
    private RunsaPosStockMapper runsaPosStockMapper;
    @Autowired
    private RunsaPosTransferMapper runsaPosTransferMapper;
    @Autowired
    private GoodsPopularityMapper goodsPopularityMapper;

    @Value("${properties.erp_service.resource_url}")
    private String ResourceUrl;

    @Override
    public List<ErpUser> getAllStoreUsers() {
        return erpUserMapper.selectAllStoreUsers();
    }

    @Override
    public ErpUser getUserByAccount(String userAccount) {
        return erpUserMapper.selectUserByAccount(userAccount);
    }

    @Override
    public boolean getGoodsNeedUpdate(String lastModDate) {
        String freshLastModDate = getGoodsBarcodeLastModDate();
        return freshLastModDate.compareTo(lastModDate) > 0;
    }

    @Override
    public List<String> getAllSimpleGoodsBarcodes(String lastModDate) {
        return goodsBarcodeMapper.selectAllSimpleGoodsBarcodes(lastModDate.substring(0,8));
    }

    @Override
    public List<String> getAllSimpleGoods(String lastModDate) {
        return goodsMapper.selectAllSimpleGoods(lastModDate.substring(0,8));
    }

    @Override
    public List<String> getAllGoodsNoInSameStyle(String goodsNo) {
        return goodsMapper.selectGoodsNoInSameStyle(goodsNo);
    }

    @Override
    public Goods getGoods(String goodsNo) {
        return goodsMapper.selectGoods(goodsNo);
    }

    @Override
    public boolean queryGoodsPicture(String goodsNo) {
        String resp = NetUtil.request(ResourceUrl + goodsNo, null, "GET", "json");
        BaseResponseVo responseVo = new Gson().fromJson(resp, BaseResponseVo.class);
        return responseVo != null && responseVo.code == 200;
    }

    @Override
    public String getGoodsPictureName(String goodsNo) {
        return goodsMapper.selectGoodsPictureName(goodsNo);
    }

    @Override
    public List<GoodsPopularityVo> getGoodsPopularity(String storeCode) {
        return goodsPopularityMapper.selectPopularities("%"+storeCode.substring(0,1)+"%");
    }

    @Override
    public List<Collocation> getCollocation(String goodsNo) {
        return collocationMapper.selectCollocation(goodsNo);
    }

    @Override
    public int getBestSellingCount(String storeCode, String dimension) {
        return bestSellingMapper.selectBestSellingCount(dimension, storeCode.substring(0,1));
    }

    @Override
    public List<BestSelling> getBestSelling(String storeCode, String dimension, int pageSize, int page) {
        return bestSellingMapper.selectBestSelling(dimension, storeCode.substring(0,1), pageSize, pageSize * page);
    }

    @Override
    public List<StoreSelling> getStoreSelling(String dimension, String goodsNo, boolean includeSameStyle) {
        return storeSellingMapper.selectStoreSelling(dimension, goodsNo, includeSameStyle);
    }

    @Override
    public List<MembershipVo> getMembership(String membershipId) {
        if(membershipId == null || membershipId.trim().length() < 8)
            return null;
        List<Membership> list = membershipMapper.selectMembership("%"+membershipId+"%");
        List<MembershipVo> result = new ArrayList<>();
        list.forEach(x->{
            MembershipVo vo = new MembershipVo();
            BeanUtil.copyPropertiesInclude(x,vo,new String[]{"membershipCardId","membershipName","mobile"});
            String dateFrom = DateUtil.toStr(DateUtils.addDays(new Date(), -365), "yyyyMMdd");
            vo.perExpenditure = shopHistoryMapper.selectPerExpenditure(vo.membershipCardId);
            vo.yearExpenditure = shopHistoryMapper.selectPeriodExpenditure(vo.membershipCardId, dateFrom, "20991231");
            vo.totalExpenditure = shopHistoryMapper.selectInitialExpenditure(vo.membershipCardId, 2019) + shopHistoryMapper.selectPeriodExpenditure(vo.membershipCardId, "20190101", "20991231");
            result.add(vo);
        });
        return result;
    }

    @Override
    public int getShopHistoryCount(String membershipCardId) {
        return shopHistoryMapper.selectShopHistoryCount(membershipCardId);
    }

    @Override
    public List<ShopHistoryVo> getShopHistory(String membershipCardId, int pageSize, int page) {
        List<ShopHistoryVo> result = shopHistoryMapper.selectShopHistory("SELECT nos as listNo, outdate as shopDate, nos as salesListCode, nb as quantity, now_real as amount FROM dbo.u2sale WHERE codes='"+membershipCardId+"' ORDER BY outdate desc", page*pageSize, pageSize);
        List<String> list = new ArrayList<>();
        list.add("");
        result.stream().forEach(x->list.add(x.listNo));
        List<ShopHistoryItemVo> itemList = shopHistoryMapper.selectShopHistoryItems(list);
        result.stream().forEach(y->{
            y.shopHistoryItemList.addAll(itemList.stream().filter(z->z.listNo.equals(y.listNo)).collect(Collectors.toList()));
        });
        return  result;
    }

    @Override
    public void saveRunsaPosData(ApiController.RunsaPosDataBean posDataBean) {
        String storeCode = "";
        if(posDataBean.stockList.size() > 0)
            storeCode = posDataBean.stockList.stream().findFirst().get().dbno;
        if(storeCode.isEmpty() && posDataBean.salesList.size() > 0)
            storeCode = posDataBean.salesList.stream().findFirst().get().cusno;
        if(storeCode.isEmpty() && posDataBean.transferList.size() > 0)
            storeCode = posDataBean.transferList.stream().findFirst().get().cusno;
        if(!storeCode.isEmpty()) {
            runsaPosSalesMapper.deleteByStoreCode(storeCode);
            runsaPosStockMapper.deleteByStoreCode(storeCode);
            runsaPosTransferMapper.deleteByDeliveryStoreCode(storeCode);
            posDataBean.salesList.forEach(x -> runsaPosSalesMapper.insert(x));
            posDataBean.stockList.forEach(x -> runsaPosStockMapper.insert(x));
            posDataBean.transferList.forEach(x -> runsaPosTransferMapper.insert(x));
        }
    }

    @Override
    public List<GoodsSizePsiVo> getRunsaPosStock(String storeCode, String goodsNo) {
        List<GoodsSizePsiVo> result = new ArrayList<>();
        List<String> sizeList = goodsMapper.selectGoodsSize(goodsNo);
        sizeList.stream().forEach(size-> {
            GoodsSizePsiVo vo = new GoodsSizePsiVo(size);
            //全部店铺库存
            List<RunsaPosStock> stockList = runsaPosStockMapper.selectBySKU(goodsNo, size);
            stockList.forEach(y-> {
                vo.psiMap.put(y.dbno, new PsiVo(0,0,0, 0, y.nb));
            });
            //总仓库存
            int _stock = erpSimpleMapper.selectWarehouseStockBySKU(goodsNo, size);
            if(_stock > 0) {
                vo.psiMap.put("总仓", new PsiVo(0,0,0, 0, _stock));
            }
            //全部店铺销售
            List<RunsaPosSales> salesList = runsaPosSalesMapper.selectBySKU(goodsNo, size);
            salesList.forEach(y-> {
                if(vo.psiMap.containsKey(y.cusno)) {
                    PsiVo psiVo = vo.psiMap.get(y.cusno);
                    psiVo.s = y.nb;
                    psiVo.sa = y.nb * y.proprice;
                } else {
                    vo.psiMap.put(y.cusno, new PsiVo(0, 0, y.nb, y.nb * y.proprice, 0));
                }
            });
            result.add(vo);
        });
        return result;
    }

    @Override
    public boolean unusedTransferList(String fromStoreCode, String toStoreCode, String listNo) {
        return false;
    }

    @Override
    public RunsaPosTransfer getTransferList(String listNo) {
        return null;
    }

    @Override
    public String getGoodsBarcodeLastModDate() {
        String lastModDate = goodsBarcodeMapper.selectLastModDate();
        return lastModDate.trim().equals("") ? "20000101000000" : lastModDate;
    }

    /*
    @Deprecated
    @Override
    public GoodsBarcodeEntity getLastestGoodsBarcodes(String lastModDate) {
        //PropertyUtilsBean bean = new PropertyUtilsBean();
        GoodsBarcodeEntity entity = new GoodsBarcodeEntity();
        List<GoodsBarcode> list = goodsBarcodeMapper.selectLastestGoodsBarcodes(lastModDate);
        Date maxModDate = new Date(0);
        for(GoodsBarcode goodsBarcode: list) {
            GoodsBarcodeEntity.ResultBean bean = new GoodsBarcodeEntity.ResultBean();
            bean.id =  goodsBarcode.id;
            bean.brand = goodsBarcode.brand;
            bean.goodsNo = goodsBarcode.goodsNo;
            bean.goodsName = goodsBarcode.goodsName;
            bean.sizeDesc = goodsBarcode.sizeDesc;
            bean.barcode = goodsBarcode.barcode;
            maxModDate = maxModDate.after(goodsBarcode.lastModDate) ? maxModDate : goodsBarcode.lastModDate;
            entity.list.add(bean);
        }
        entity.lastModDate = new SimpleDateFormat("yyyyMMddHHmmss").format(maxModDate);
        return entity;
    }*/
}
