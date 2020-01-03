package com.tlg.storehelper.service.impl;

import com.google.gson.Gson;
import com.nec.lib.utils.BeanUtil;
import com.nec.lib.utils.DateUtil;
import com.nec.lib.utils.NetUtil;
import com.tlg.storehelper.controller.ApiController;
import com.tlg.storehelper.dao.ds1.*;
import com.tlg.storehelper.dao.ds2.SimpleMapper;
import com.tlg.storehelper.dao.ds3.*;
import com.tlg.storehelper.entity.ds1.*;
import com.tlg.storehelper.entity.ds3.*;
import com.tlg.storehelper.pojo.*;
import com.tlg.storehelper.service.ErpService;
import com.ulisesbocchio.jasyptspringboot.util.Collections;
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
    private ErpStoreMapper erpStoreMapper;
    @Autowired
    private GoodsBarcodeMapper goodsBarcodeMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private MembershipMapper membershipMapper;
    @Autowired
    private ShopHistoryMapper shopHistoryMapper;
    @Autowired
    private SellingMapper sellingMapper;
    @Autowired
    private StockMapper stockMapper;
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

    @Value("${properties.erp_service.goods_pic_url}")
    private String ResourceUrl;

    /**逗号分隔的店编字符串，转in('','') 或 like 'x__'*/
    private String delimiterStringToSQL(String storeCodes) {
        String sql = "";
        if(storeCodes.length() == 1)
            sql = " like '" + storeCodes + "__' ";
        else {
            sql = " in (";
            String codes = "";
            for(String item: storeCodes.split(",")) {
                if (!codes.equals(""))
                    codes = codes + ",";
                codes = codes + "'" + item + "'";
            }
            sql = sql + codes + ") ";
        }
        return sql;
    }

    /**逗号分隔的店编字符串，转为"长度为0"的List*/
    private  List<String> delimiterStringToList(String storeCodes) {
        List storeList = new ArrayList();
        if (storeCodes != null && storeCodes.length() > 1) {
            for (String item : storeCodes.split(","))
                storeList.add(item);
        }
        return storeList;
    }

    /**默认MONTH前的日期，格式默认：yyyyMMdd*/
    private String dimentionToDateStr(String dim) {
        return dimentionToDateStr(dim, "yyyyMMdd");
    }
    /**默认MONTH前的日期*/
    private String dimentionToDateStr(String dim, String dateFormat) {
        dim = dim.toUpperCase();
        int delta = Integer.MAX_VALUE;
        try {
            delta = Integer.parseInt(dim);
            delta = delta > 0 ? -delta : delta;
        } catch (NumberFormatException e) {
            delta = dim.equals("WEEK")?-7:dim.equals("2WEEK")?-14:dim.equals("3WEEK")?-21:Integer.MAX_VALUE;
        }
        if(delta != Integer.MAX_VALUE)
            return DateUtil.toStr(DateUtils.addDays(new Date(), delta), dateFormat);
        else {
            delta = dim.equals("MONTH")?-1:dim.equals("2MONTH")?-2:dim.equals("3MONTH")?-3:0;
            return DateUtil.toStr(DateUtils.addMonths(new Date(), delta), dateFormat);
        }
    }

    @Override
    public List<ErpUser> getAllStoreUsers() {
        return erpUserMapper.selectAllStoreUsers();
    }

    @Override
    public ErpUser getUserByAccount(String userAccount) {
        return erpUserMapper.selectUserByAccount(userAccount);
    }

    @Override
    public List<ErpStore> getAllStore() {
        return erpStoreMapper.selectAllStore();
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
    public List<Selling> getCollocation(String goodsNo, String storeCodes, String dimension) {
        String dateFrom = dimentionToDateStr(dimension);
        Goods goods = goodsMapper.selectGoods(goodsNo);
        List storeList = new ArrayList();
        storeCodes = storeCodes.length() == 1 ? "" : storeCodes;
        if (storeCodes != null && !storeCodes.isEmpty())
            for (String item : storeCodes.split(","))
                storeList.add(item);
        if (goods != null)
            return sellingMapper.selectCollocationByStyleNo(dateFrom, goods.styleNo, storeList);
        else
            return new ArrayList<>();
    }

    @Override
    public int getPairCollocation(List<PairCollocation> result, String storeCodes, String dimension, String salesCode, int pageSize, int page) {
        List<String> storeList = delimiterStringToList(storeCodes);
        String brand = storeList.size()==0 ? storeCodes : null;
        List<PairCollocation> list0 = sellingMapper.selectPairCollocation(dimentionToDateStr(dimension), brand, storeList, salesCode!=null && !salesCode.isEmpty() ? salesCode : null);
        List<PairCollocation> list = list0.stream().limit(60).collect(Collectors.toList());
        if(result == null)
            result = new ArrayList<PairCollocation>();
        int ceilingIdx = (page+1)*pageSize > list.size() ? list.size() : (page+1)*pageSize;
        for(int i = page*pageSize; i<ceilingIdx; i++)
            result.add(list.get(i));
        return list.size();
    }

    @Override
    public int getBestSellingCount(String storeCodes, String dimension, String salesCode, int floorNumber) {
        String dateFrom = dimentionToDateStr(dimension);
        if(storeCodes.length() == 1)
            return sellingMapper.selectBestSellingCount(dateFrom, storeCodes, null, salesCode, floorNumber);
        else {
            List storeList = new ArrayList();
            if (storeCodes != null && !storeCodes.isEmpty())
                for (String item : storeCodes.split(","))
                    storeList.add(item);
            return sellingMapper.selectBestSellingCount(dateFrom, null, storeList, salesCode, floorNumber);
        }
    }

    @Override
    public List<Selling> getBestSelling(String storeCodes, String dimension, String salesCode, int floorNumber, int pageSize, int page) {
        String subSQL1 = delimiterStringToSQL(storeCodes);
        String dateFrom = dimentionToDateStr(dimension);
        String sql = "select min(a.colthno) as goodsNo, max(b.sprice) as price, sum(a.nb) as quantity, sum(a.endprice*a.nb) as money from dbo.u2saleb a left join coloth_t b on a.colthno=b.colthno"+
                "  where nos in (select distinct nos from dbo.u2sale where substring(cusno,2,3) "+subSQL1+
                " and outdate >= '" + dateFrom + "') ";
        if(salesCode != null && !salesCode.isEmpty())
            sql = sql + " and a.salescode='" + salesCode + "' ";
        sql = sql + " group by b.colthnob  having sum(nb)>=" + floorNumber + " order by sum(nb) desc";
        return sellingMapper.selectBestSelling(sql, pageSize, pageSize * page);
    }

    @Override
    public List<String> getSalesList(String storeCode) {
        return sellingMapper.selectSalesList(storeCode);
    }

    @Override
    public int getBestSalesSellingCount(String storeCodes, String dimension) {
        String startDate = dimentionToDateStr(dimension);
        if(storeCodes.length() == 1)
            return sellingMapper.selectBestSalesSellingCount(startDate, storeCodes, null);
        else {
            List storeList = delimiterStringToList(storeCodes);
            return sellingMapper.selectBestSalesSellingCount(startDate, null, storeList);
        }
    }

    @Override
    public List<SalesSelling> getBestSalesSelling(String storeCodes, String dimension, int pageSize, int page) {
        String subSQL1 = delimiterStringToSQL(storeCodes);
        String dateFrom = dimentionToDateStr(dimension);
        String sql = "select b.salescode as salesCode,s.names as salesName,substring(a.cusno,2,3) as storeCode,db.class2 as storeLevel,count(distinct b.nos) as transactions,sum(b.nb) as quantity,sum(b.endprice*b.nb) as money,max(sch.days) as days"+
                " from dbo.u2saleb b left join dbo.u2sale a on b.nos=a.nos"+
                " left join zg_sales s on b.salescode=s.codes"+
                " left join db on a.cusno=db.dbno"+
                " left join (select dbno,empid,count(0) as days from KQ_Schedule where substring(dbno,2,3) "+subSQL1+" and eday >= '"+dateFrom+"' and eday < current_date() group by dbno,empid) sch on a.cusno=sch.dbno and b.salescode=sch.empid"+
                "  where substring(a.cusno,2,3) "+subSQL1+
                " and a.outdate >= '" + dateFrom + "'"+
                " group by b.salescode,a.cusno,s.names,db.class2 having sum(b.nb)>0 and max(sch.days) > 0 order by sum(b.endprice*b.nb)/max(sch.days) desc";
        return sellingMapper.selectBestSalesSelling(sql, pageSize, pageSize * page);
    }

    @Override
    public List<KV<String,Integer>> getTotalStock(List<String> goodsNoList, boolean includeSameStyle) {
        return stockMapper.selectTotalStockByStyle(goodsNoList);
    }

    @Override
    public List<StoreSelling> getSelling(String dimension, String goodsNo, boolean includeSameStyle) {
        String startDate = dimentionToDateStr(dimension);
        if (includeSameStyle) {
            Goods goods = goodsMapper.selectGoods(goodsNo);
            String styleNo = goods != null ? goods.styleNo : "_";
            return sellingMapper.selectStoreSellingByStyleNo(styleNo, startDate);
        } else
            return sellingMapper.selectStoreSellingByGoodsNo(goodsNo, startDate);
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
            int _stock = stockMapper.selectWarehouseStockBySKU(goodsNo, size);
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
