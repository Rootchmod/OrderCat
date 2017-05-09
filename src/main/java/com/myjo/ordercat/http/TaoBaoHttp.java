package com.myjo.ordercat.http;

import com.myjo.ordercat.config.OrderCatConfig;
import com.myjo.ordercat.domain.InventoryInfo;
import com.myjo.ordercat.domain.ItemsOnSale;
import com.myjo.ordercat.domain.PageResult;
import com.myjo.ordercat.domain.TradeStatus;
import com.myjo.ordercat.exception.OCException;
import com.myjo.ordercat.utils.OcListUtils;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.Item;
import com.taobao.api.domain.Sku;
import com.taobao.api.domain.Trade;
import com.taobao.api.request.*;
import com.taobao.api.response.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by lee5hx on 17/4/24.
 */
public class TaoBaoHttp {

    private static final Logger Logger = LogManager.getLogger(TianmaSportHttp.class);
    //private static final String URL = OrderCatConfig.getTaobaoApiUrl();

    //private static final String APP_KEY = OrderCatConfig.getTaobaoApiAppKey();
    //private static final String APP_SECRET = "7cb1d50fc70c7548b31d414c2adbae06";
    //private static final String SESSION_KEY = OrderCatConfig.getTaobaoApiSessionKey();
    //

    private static final Long CID = Long.parseLong("1282880675"); //麦巨自营
    //private static final Long CID = Long.parseLong("1316012271");//测试类目


    public TaoBaoHttp() {

    }

    public void itemcatsGetRequest() throws Exception {

        TaobaoClient client = new DefaultTaobaoClient(OrderCatConfig.getTaobaoApiUrl(), OrderCatConfig.getTaobaoApiAppKey(), OrderCatConfig.getTaobaoApiAppSecret());
        SellercatsListGetRequest req = new SellercatsListGetRequest();
        req.setNick("麦巨鞋类专营店");
        req.setFields("cid,name");
        SellercatsListGetResponse rsp = client.execute(req);
        System.out.println(rsp.getBody());
    }

    /**
     * taobao.items.onsale.get (获取当前会话用户出售中的商品列表)
     *
     * @return
     * @throws Exception
     */
    public PageResult<Item> getTaobaoItemsOnSale(long pageNo, long pageSize) throws Exception {


        PageResult<Item> pageResult = new PageResult<>();
        Logger.debug("getTaobaoItemsOnSale:" + CID);
        List<ItemsOnSale> list = new ArrayList<>();
        TaobaoClient client = new DefaultTaobaoClient(OrderCatConfig.getTaobaoApiUrl(), OrderCatConfig.getTaobaoApiAppKey(), OrderCatConfig.getTaobaoApiAppSecret());
        ItemsOnsaleGetRequest req = new ItemsOnsaleGetRequest();
        req.setFields("approve_status,num_iid,title,nick,type,cid,pic_url,num,props,valid_thru,list_time,price,has_discount,has_invoice,has_warranty,has_showcase,modified,delist_time,postage_id,seller_cids,outer_id,sold_quantity");
        //req.setQ("N97");
        //req.setCid(CID);
        req.setSellerCids(CID + "");
        req.setPageNo(pageNo);
//        req.setHasDiscount(true);
//        req.setHasShowcase(true);
        req.setOrderBy("list_time:desc");
//        req.setIsTaobao(true);
//        req.setIsEx(true);
        req.setPageSize(pageSize);
        //req.setStartModified(StringUtils.parseDateTime("2000-01-01 00:00:00"));
        //req.setEndModified(new Date());

        //req.setIsCspu(true);
//        req.setIsCombine(true);
        ItemsOnsaleGetResponse rsp = client.execute(req, OrderCatConfig.getTaobaoApiSessionKey());

        Logger.info(rsp.getBody());
        // List<Item> list1 = rsp.getItems();
//        JSONObject object = JSON.parseObject(rsp.getBody());

        //      JSONObject items_onsale_get_response = object.getJSONObject("items_onsale_get_response");
//        JSONObject items = items_onsale_get_response.getJSONObject("items");
//        JSONArray itemArray = items.getJSONArray("item");
//
//
//
//        JSONObject item;
//        ItemsOnSale itemsOnSale;
//        for (int i = 0; i < itemArray.size(); i++) {
//            itemsOnSale = new ItemsOnSale();
//            item = itemArray.getJSONObject(i);
//            itemsOnSale.setNumIid(item.getLong("num_iid"));
//            list.add(itemsOnSale);
//        }
        pageResult.setRows(rsp.getItems());
        pageResult.setTotal(rsp.getTotalResults());

        Logger.debug("getTaobaoItemsOnSale.pageResult.getRows().size:" + pageResult.getRows().size());
        Logger.debug("getTaobaoItemsOnSale.pageResult.getTotal():" + pageResult.getTotal());
        return pageResult;
    }


    //taobao.item.skus.get (根据商品ID列表获取SKU信息)

    public List<Item> getTaobaoItemsOnSale() throws Exception {
        long pageNo = 1l;
        long pageSize = 100l;
        List<Item> rtlist = new ArrayList<>();

        PageResult<Item> pageResult;
        do {
            pageResult = getTaobaoItemsOnSale(pageNo, pageSize);
            rtlist.addAll(pageResult.getRows());
            Logger.debug("Math.ceil((double)pageResult.getTotal() / pageSize):" + Math.ceil((double) pageResult.getTotal() / pageSize));
            //++pageNo;
        } while (Math.ceil((double) pageResult.getTotal() / pageSize) >= (++pageNo));

        return rtlist;
    }

    /**
     * taobao.item.skus.get (根据商品ID列表获取SKU信息)
     *
     * @param list
     * @return
     */
    public List<Sku> getTaoBaoItemSkus(List<Item> list) throws Exception {
        Logger.debug("getTaoBaoItemSkus:" + CID);
        List<Sku> rtlist = new ArrayList<>();
//        int[] indexes =
//                Stream.of(IntStream.range(-1, list.size())
//                        .filter(i -> i % 40 == 0), IntStream.of(list.size()))
//                        .flatMapToInt(s -> s).toArray();
//        List<List<Item>> subLists =
//                IntStream.range(0, indexes.length - 1)
//                        .mapToObj(i -> list.subList(indexes[i], indexes[i + 1]))
//                        .collect(Collectors.toList());

        List<List<Item>> subLists = OcListUtils.splitList(list, 40);
        for (List<Item> list1 : subLists) {
            rtlist.addAll(taoBaoItemSkus(list1));
        }
        //list.stream().limit()
        Logger.debug("getTaoBaoItemSkus.rt.size:" + rtlist.size());
        return rtlist;
    }

    private List<Sku> taoBaoItemSkus(List<Item> list) throws Exception {
        List<Sku> rtlist = new ArrayList<>();

        TaobaoClient client = new DefaultTaobaoClient(OrderCatConfig.getTaobaoApiUrl(), OrderCatConfig.getTaobaoApiAppKey(), OrderCatConfig.getTaobaoApiAppSecret());
        ItemSkusGetRequest req = new ItemSkusGetRequest();
        req.setFields("sku_id,id,num_iid,properties,quantity,price,created,modified,status,extra_id,memo,properties_name,sku_spec_id,with_hold_quantity,sku_delivery_time,change_prop,outer_id,barcode");
//        String commaSeparatedNumbers = numbers.stream()
//                .map(i -> i.toString())
//                .collect(Collectors.joining(", "));

        String NumIidStr = list.parallelStream()
                .map(itemsOnSale -> itemsOnSale.getNumIid().toString())
                .collect(Collectors.joining(","));
        Logger.debug("NumIidStr:" + NumIidStr);
        req.setNumIids(NumIidStr);
        //req.setNumIids(numIid.toString());
        ItemSkusGetResponse rsp = client.execute(req, OrderCatConfig.getTaobaoApiSessionKey());
        if (rsp.isSuccess() == true) {
            rtlist.addAll(rsp.getSkus());
        }

        return rtlist;
    }


    public void updateQuantityAndPriceTmall(Long itemId,
                                            List<InventoryInfo> subSkuIPriceList,
                                            List<Sku> skuList, Map<Long, InventoryInfo> csvListSukMap) throws Exception {
        List<List<Sku>> subLists = OcListUtils.splitList(skuList, 19);

        //过滤为空的SKU
        subSkuIPriceList = subSkuIPriceList
                .parallelStream()
                .filter(inventoryInfo -> inventoryInfo != null)
                .sorted((o1, o2) -> o2.getSalesPrice().compareTo(o1.getSalesPrice()))
                .collect(Collectors.toList());

        List<List<InventoryInfo>> subSkuIPriceLists =  OcListUtils.splitList(subSkuIPriceList, 19);

        for (List<Sku> list : subLists) {
            updateTmallItemQuantityUpdate(itemId, list, csvListSukMap);
        }

        String price = null;
        Optional<InventoryInfo> dd;
        for (List<InventoryInfo> list : subSkuIPriceLists) {

            dd = list
                    .parallelStream()
                    .filter(inventoryInfo -> inventoryInfo != null)
                    .min(
                            (p1, p2) -> {
                                return  p1.getSalesPrice().compareTo(p2.getSalesPrice());
                            }
                    );
            if (dd.isPresent()) {
                price = dd.get().getSalesPrice().toPlainString();
            }

            updateTmallItemPriceUpdate(itemId, list, csvListSukMap, price);
        }
    }


    /**
     * tmall.item.price.update (天猫商品/SKU价格更新接口)
     *
     * @throws Exception
     */
    public Long updateTmallItemPriceUpdate(Long itemId, List<InventoryInfo> list, Map<Long, InventoryInfo> csvListSukMap, String price) throws Exception {

        Long priceUpdateResult = 0l;
        TaobaoClient client = new DefaultTaobaoClient(OrderCatConfig.getTaobaoApiUrl(), OrderCatConfig.getTaobaoApiAppKey(), OrderCatConfig.getTaobaoApiAppSecret());
        TmallItemPriceUpdateRequest req = new TmallItemPriceUpdateRequest();
        req.setItemId(itemId);
        //req.setItemPrice(price);

        List<TmallItemPriceUpdateRequest.UpdateSkuPrice> list2 = new ArrayList<>();
        TmallItemPriceUpdateRequest.UpdateSkuPrice obj3;
        //InventoryInfo inventoryInfo;
        for (InventoryInfo inventoryInfo1 : list) {
                obj3 = new TmallItemPriceUpdateRequest.UpdateSkuPrice();
                obj3.setSkuId(inventoryInfo1.getSkuId());
                obj3.setPrice(inventoryInfo1.getSalesPrice().toPlainString());
                //Logger.info(String.format("skuid:[%d] - 价格[%s]",inventoryInfo1.getSkuId(),inventoryInfo1.getSalesPrice().toPlainString()));
                list2.add(obj3);

        }

        if (price != null ) {
            Logger.debug(String.format("[%d]-最小价格[%s]", itemId, price));
            req.setItemPrice(price);
        }
        req.setSkuPrices(list2);


        TmallItemPriceUpdateRequest.UpdateItemPriceOption obj4 = new TmallItemPriceUpdateRequest.UpdateItemPriceOption();
        obj4.setIgnoreFakeCredit(true);
        // obj4.setCurrencyType("CNY");
        req.setOptions(obj4);
        TmallItemPriceUpdateResponse rsp = client.execute(req, OrderCatConfig.getTaobaoApiSessionKey());
        Logger.debug((rsp.getPriceUpdateResult() + ":" + rsp.getBody()));

        if (rsp.isSuccess()) {
            priceUpdateResult = Long.valueOf(rsp.getPriceUpdateResult());
        } else {
            throw new OCException(rsp.getErrorCode() + ":" + rsp.getMsg());
        }
        return priceUpdateResult;
    }


    /**
     * tmall.item.quantity.update (天猫商品/SKU库存更新接口)
     *
     * @throws Exception
     */
    public Long updateTmallItemQuantityUpdate(Long itemId, List<Sku> skuList, Map<Long, InventoryInfo> csvListSukMap) throws Exception {

        Long quantityUpdateResult = 0l;

        TaobaoClient client = new DefaultTaobaoClient(OrderCatConfig.getTaobaoApiUrl(), OrderCatConfig.getTaobaoApiAppKey(), OrderCatConfig.getTaobaoApiAppSecret());
        TmallItemQuantityUpdateRequest req = new TmallItemQuantityUpdateRequest();
        req.setItemId(itemId);
        //req.setItemQuantity(12L);
        List<TmallItemQuantityUpdateRequest.UpdateSkuQuantity> list2 = new ArrayList();

        TmallItemQuantityUpdateRequest.UpdateSkuQuantity obj3;
        InventoryInfo inventoryInfo;
        for (Sku sku : skuList) {
            inventoryInfo = csvListSukMap.get(sku.getSkuId());
            obj3 = new TmallItemQuantityUpdateRequest.UpdateSkuQuantity();
            obj3.setSkuId(sku.getSkuId());
//            if (sku.getSkuId().longValue() == 3467906683477L) {
//                System.out.println(sku.getSkuId());
//
//            }

            if (inventoryInfo != null) {
                obj3.setQuantity(Long.valueOf(inventoryInfo.getNum2()));
            } else {
                obj3.setQuantity(0L);
            }
            list2.add(obj3);
        }
        req.setSkuQuantities(list2);
        TmallItemQuantityUpdateRequest.UpdateItemQuantityOption obj4 = new TmallItemQuantityUpdateRequest.UpdateItemQuantityOption();
        //obj4.setOuterBizKey("qewq113123123");
        obj4.setType(1L);
        req.setOptions(obj4);
        TmallItemQuantityUpdateResponse rsp = client.execute(req, OrderCatConfig.getTaobaoApiSessionKey());

        Logger.debug(rsp.getQuantityUpdateResult() + ":" + rsp.getBody());

        if (rsp.isSuccess()) {
            quantityUpdateResult = Long.valueOf(rsp.getQuantityUpdateResult());
        } else {
            throw new OCException(rsp.getErrorCode() + ":" + rsp.getMsg());
        }

        return quantityUpdateResult;
    }

    public void test() throws Exception {



    }

    //taobao.trades.sold.get (查询卖家已卖出的交易数据（根据创建时间）)
    //    WAIT_BUYER_PAY：等待买家付款
    //    WAIT_SELLER_SEND_GOODS：等待卖家发货
    //    SELLER_CONSIGNED_PART：卖家部分发货
    //    WAIT_BUYER_CONFIRM_GOODS：等待买家确认收货
    //    TRADE_BUYER_SIGNED：买家已签收（货到付款专用）
    //    TRADE_FINISHED：交易成功
    //    TRADE_CLOSED：交易关闭
    //    TRADE_CLOSED_BY_TAOBAO：交易被淘宝关闭
    //    TRADE_NO_CREATE_PAY：没有创建外部交易（支付宝交易）
    //    WAIT_PRE_AUTH_CONFIRM：余额宝0元购合约中
    //    PAY_PENDING：外卡支付付款确认中
    //    ALL_WAIT_PAY：所有买家未付款的交易（包含：WAIT_BUYER_PAY、TRADE_NO_CREATE_PAY）
    //    ALL_CLOSED：所有关闭的交易（包含：TRADE_CLOSED、TRADE_CLOSED_BY_TAOBAO）


    public List<Trade> getSoldTrades(Date begin, Date end, TradeStatus status) throws Exception {
        long pageNo = 1l;
        long pageSize = 100l;
        List<Trade> rtlist = new ArrayList<>();

        PageResult<Trade> pageResult;
        do {
            pageResult = getSoldTrades(begin, end, status, pageNo, pageSize);
            rtlist.addAll(pageResult.getRows());
            Logger.debug("Math.ceil((double)pageResult.getTotal() / pageSize):" + Math.ceil((double) pageResult.getTotal() / pageSize));
            //++pageNo;
        } while (Math.ceil((double) pageResult.getTotal() / pageSize) >= (++pageNo));

        return rtlist;
    }


    public PageResult<Trade> getSoldTrades(Date begin, Date end, TradeStatus status, Long pageNo, Long pageSize) throws Exception {
        PageResult<Trade> pr = new PageResult();
        TaobaoClient client = new DefaultTaobaoClient(OrderCatConfig.getTaobaoApiUrl(), OrderCatConfig.getTaobaoApiAppKey(), OrderCatConfig.getTaobaoApiAppSecret());
        TradesSoldGetRequest req = new TradesSoldGetRequest();
        req.setFields("num_iid,title,sku_id,pay_time,total_fee,end_time,buyer_nick,outer_iid,num,status");
        req.setStartCreated(begin);
        req.setEndCreated(end);
        req.setStatus(status.toString());

        // req.setType("game_equipment");
        //req.setExtType("service");
        //req.setRateStatus("TRADE_FINISHED");
        //req.setTag("time_card");
        req.setPageNo(pageNo);
        req.setPageSize(pageSize);
        //req.setUseHasNext(true);
        TradesSoldGetResponse rsp = client.execute(req, OrderCatConfig.getTaobaoApiSessionKey());
        if (rsp.isSuccess()) {
            pr.setRows(rsp.getTrades());
            pr.setTotal(rsp.getTotalResults());
        }
        return pr;
    }

}
