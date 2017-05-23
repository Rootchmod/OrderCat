package com.myjo.ordercat.http;

import com.myjo.ordercat.config.OrderCatConfig;
import com.myjo.ordercat.domain.*;
import com.myjo.ordercat.exception.OCException;
import com.myjo.ordercat.utils.OcDateTimeUtils;
import com.myjo.ordercat.utils.OcListUtils;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.*;
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

    private static final Logger Logger = LogManager.getLogger(TaoBaoHttp.class);
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


    public void updateTmallQuantityAndPrice(Long itemId,
                                            List<Sku> skuList,
                                            Map<String, InventoryInfo> csvListSukMap) throws Exception {


        //记录下需要更新价格商品
        List<TmallPriceUpdateInfo> tmallPriceUpdateInfos = new ArrayList<>();
        //库存更新
        List<List<Sku>> subLists = OcListUtils.splitList(skuList, 19);
        for (List<Sku> list : subLists) {
            updateTmallItemQuantityUpdate(itemId, list,csvListSukMap,tmallPriceUpdateInfos);//库存更新
        }

        //过滤为空的SKU
        tmallPriceUpdateInfos = tmallPriceUpdateInfos
                .parallelStream()
                .filter(t -> t != null)
                .sorted((o1, o2) -> o2.getSalesPrice().compareTo(o1.getSalesPrice()))
                .collect(Collectors.toList());

        List<List<TmallPriceUpdateInfo>> subTmallPriceUpdateInfos =  OcListUtils.splitList(tmallPriceUpdateInfos, 19);

        String price = null;
        Optional<TmallPriceUpdateInfo> dd;
        for (List<TmallPriceUpdateInfo> list : subTmallPriceUpdateInfos) {
            dd = list
                    .parallelStream()
                    .filter(t -> t != null)
                    .min(
                            (p1, p2) -> p1.getSalesPrice().compareTo(p2.getSalesPrice())
                    );
            if (dd.isPresent()) {
                price = dd.get().getSalesPrice().toPlainString();
            }
            updateTmallItemPriceUpdate(itemId, list, price);
        }
    }


    /**
     * tmall.item.price.update (天猫商品/SKU价格更新接口)
     *ERR_RULE_CATEGORY_CANNOT_BLANK
     * @throws Exception
     */
    public Long updateTmallItemPriceUpdate(Long itemId, List<TmallPriceUpdateInfo> list, String price) throws Exception {

        Long priceUpdateResult = 0l;
        TaobaoClient client = new DefaultTaobaoClient(OrderCatConfig.getTaobaoApiUrl(), OrderCatConfig.getTaobaoApiAppKey(), OrderCatConfig.getTaobaoApiAppSecret());
        TmallItemPriceUpdateRequest req = new TmallItemPriceUpdateRequest();
        req.setItemId(itemId);
        //req.setItemPrice(price);

//        if(itemId.longValue() == 549502452719L){
//            System.out.println(itemId);
//        }

        List<TmallItemPriceUpdateRequest.UpdateSkuPrice> list2 = new ArrayList<>();
        TmallItemPriceUpdateRequest.UpdateSkuPrice obj3;
        //InventoryInfo inventoryInfo;
        for (TmallPriceUpdateInfo t : list) {
                obj3 = new TmallItemPriceUpdateRequest.UpdateSkuPrice();
                obj3.setSkuId(t.getSkuId());
                obj3.setPrice(t.getSalesPrice().toPlainString());
                Logger.debug(String.format("skuid:[%d] - [%s] - 价格[%s]",
                        t.getSkuId(),
                        t.getOuterId(),
                        t.getSalesPrice().toPlainString()));
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
    public Long updateTmallItemQuantityUpdate(Long itemId, List<Sku> skuList, Map<String, InventoryInfo> csvListSukMap,List<TmallPriceUpdateInfo> tmallPriceUpdateInfos) throws Exception {

        Long quantityUpdateResult = 0l;

        TaobaoClient client = new DefaultTaobaoClient(OrderCatConfig.getTaobaoApiUrl(), OrderCatConfig.getTaobaoApiAppKey(), OrderCatConfig.getTaobaoApiAppSecret());
        TmallItemQuantityUpdateRequest req = new TmallItemQuantityUpdateRequest();
        req.setItemId(itemId);
        //req.setItemQuantity(12L);
        List<TmallItemQuantityUpdateRequest.UpdateSkuQuantity> list2 = new ArrayList();

        TmallItemQuantityUpdateRequest.UpdateSkuQuantity obj3;
        InventoryInfo inventoryInfo;
        TmallPriceUpdateInfo tmallPriceUpdateInfo;
        for (Sku sku : skuList) {
            inventoryInfo = csvListSukMap.get(sku.getOuterId());
            obj3 = new TmallItemQuantityUpdateRequest.UpdateSkuQuantity();
            obj3.setSkuId(sku.getSkuId());
//            if (sku.getSkuId().longValue() == 3421485762620L) {
//                System.out.println(sku.getSkuId());
//
//            }
            if (inventoryInfo != null) {
                obj3.setQuantity(Long.valueOf(inventoryInfo.getNum2()));
                //记录库存不为零的价格
                tmallPriceUpdateInfo = new TmallPriceUpdateInfo();
                tmallPriceUpdateInfo.setSalesPrice(inventoryInfo.getSalesPrice());
                tmallPriceUpdateInfo.setOuterId(sku.getOuterId());
                tmallPriceUpdateInfo.setSkuId(sku.getSkuId());
                tmallPriceUpdateInfos.add(tmallPriceUpdateInfo);
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

    /**
     * taobao.logistics.companies.get (查询物流公司信息)
     * @return
     * @throws Exception
     */
    public List<LogisticsCompany> getTaoBaoLogisticsCompanies() throws Exception {
        List<LogisticsCompany> list = new ArrayList<>();
        TaobaoClient client = new DefaultTaobaoClient(OrderCatConfig.getTaobaoApiUrl(), OrderCatConfig.getTaobaoApiAppKey(), OrderCatConfig.getTaobaoApiAppSecret());
        LogisticsCompaniesGetRequest req = new LogisticsCompaniesGetRequest();
        req.setFields("id,code,name,reg_mail_no");
        req.setIsRecommended(true);
        req.setOrderMode("offline");
        LogisticsCompaniesGetResponse rsp = client.execute(req);
        if(rsp.isSuccess()){
            list = rsp.getLogisticsCompanies();
        }
        return list;
    }





    public void test() throws Exception {



        TaobaoClient client = new DefaultTaobaoClient(OrderCatConfig.getTaobaoApiUrl(), OrderCatConfig.getTaobaoApiAppKey(), OrderCatConfig.getTaobaoApiAppSecret());
        TradesSoldGetRequest req = new TradesSoldGetRequest();
        req.setFields("tid,type,status,payment,orders,rx_audit_status");
        req.setStartCreated(OcDateTimeUtils.string2Date("2017-05-01 00:00:00"));
        req.setEndCreated(OcDateTimeUtils.string2Date("2017-05-20 00:00:00"));
        //req.setStatus("TRADE_CLOSED");
        //req.setBuyerNick("zhangsan");
        //req.setType("game_equipment");
        //req.setExtType("service");
        //req.setRateStatus("RATE_UNBUYER");
        //req.setTag("time_card");
        req.setPageNo(1L);
        req.setPageSize(40L);
        req.setUseHasNext(true);
        TradesSoldGetResponse rsp = client.execute(req, OrderCatConfig.getTaobaoApiSessionKey());
        System.out.println(rsp.getBody());




    }


    /**
     *  taobao.trades.sold.get (查询卖家已卖出的交易数据（根据创建时间）)
     *   WAIT_BUYER_PAY：等待买家付款
     *   WAIT_SELLER_SEND_GOODS：等待卖家发货
     *   SELLER_CONSIGNED_PART：卖家部分发货
     *   WAIT_BUYER_CONFIRM_GOODS：等待买家确认收货
     *   TRADE_BUYER_SIGNED：买家已签收（货到付款专用）
     *   TRADE_FINISHED：交易成功
     *   TRADE_CLOSED：交易关闭
     *   TRADE_CLOSED_BY_TAOBAO：交易被淘宝关闭
     *   TRADE_NO_CREATE_PAY：没有创建外部交易（支付宝交易）
     *   WAIT_PRE_AUTH_CONFIRM：余额宝0元购合约中
     *   PAY_PENDING：外卡支付付款确认中
     *   ALL_WAIT_PAY：所有买家未付款的交易（包含：WAIT_BUYER_PAY、TRADE_NO_CREATE_PAY）
     *   ALL_CLOSED：所有关闭的交易（包含：TRADE_CLOSED、TRADE_CLOSED_BY_TAOBAO）
     * @param begin
     * @param end
     * @param status
     * @return
     * @throws Exception
     */
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
        req.setFields("num_iid,title,sku_id,type,pay_time,total_fee,end_time,buyer_nick,outer_iid,num,status");
        req.setStartCreated(begin);
        req.setEndCreated(end);
        if(status != null ){
            req.setStatus(status.toString());
        }
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


    /**
     * taobao.refunds.receive.get (查询卖家收到的退款列表)
     * @param begin
     * @param end
     * @return
     * @throws Exception
     */
    public List<Refund> getReceiveRefunds(Date begin, Date end) throws Exception {
        long pageNo = 1l;
        long pageSize = 100l;
        List<Refund> rtlist = new ArrayList<>();

        PageResult<Refund> pageResult;
        do {
            pageResult = getReceiveRefunds(begin, end, pageNo, pageSize);
            rtlist.addAll(pageResult.getRows());
            Logger.debug("Math.ceil((double)pageResult.getTotal() / pageSize):" + Math.ceil((double) pageResult.getTotal() / pageSize));
            //++pageNo;
        } while (Math.ceil((double) pageResult.getTotal() / pageSize) >= (++pageNo));

        Refund temp;
        for(Refund refund:rtlist) {
            temp = getRefundById(refund.getRefundId());
            refund.setNumIid(temp.getNumIid());
            refund.setOuterId(temp.getOuterId());
        }
        return rtlist;
    }

    /**
     * taobao.refunds.receive.get (查询卖家收到的退款列表)
     * @param begin
     * @param end
     * @param pageNo
     * @param pageSize
     * @return
     * @throws Exception
     */
    public PageResult<Refund> getReceiveRefunds(Date begin, Date end,Long pageNo, Long pageSize) throws Exception{
        PageResult<Refund> pr = new PageResult();
        TaobaoClient client = new DefaultTaobaoClient(OrderCatConfig.getTaobaoApiUrl(), OrderCatConfig.getTaobaoApiAppKey(), OrderCatConfig.getTaobaoApiAppSecret());
        RefundsReceiveGetRequest req = new RefundsReceiveGetRequest();
        req.setFields("refund_id, tid, title, numIid,buyer_nick, seller_nick, total_fee, status, created, refund_fee, oid, good_status, company_name, sid, payment, reason, desc, has_good_return, modified, order_status,refund_phase");
        req.setStatus("SUCCESS");
//        req.setBuyerNick("hz0799");
//        req.setType("fixed");
        req.setStartModified(begin);
        req.setEndModified(end);
        req.setPageNo(pageNo);
        req.setPageSize(pageSize);
        req.setUseHasNext(false);
        RefundsReceiveGetResponse rsp = client.execute(req, OrderCatConfig.getTaobaoApiSessionKey());
        if(rsp.isSuccess()){
            pr.setRows(rsp.getRefunds());
            pr.setTotal(rsp.getTotalResults());
        }
        return pr;
    }

    /**
     * taobao.refund.get (获取单笔退款详情)
     * @param refundId
     * @return
     * @throws Exception
     */
    public Refund getRefundById(long refundId) throws Exception{
        Refund refund = null;
        TaobaoClient client = new DefaultTaobaoClient(OrderCatConfig.getTaobaoApiUrl(), OrderCatConfig.getTaobaoApiAppKey(), OrderCatConfig.getTaobaoApiAppSecret());
        RefundGetRequest req = new RefundGetRequest();
        req.setFields("title,address,num_iid,outer_id,good_return_time,created");
        req.setRefundId(refundId);
        RefundGetResponse rsp = client.execute(req, OrderCatConfig.getTaobaoApiSessionKey());
        if(rsp.isSuccess()){
            refund = rsp.getRefund();
        }
        return refund;
    }

    /**
     * taobao.fenxiao.orders.get (查询采购单信息)
     * @param tcOrderId
     * @return
     * @throws Exception
     */
    public List<PurchaseOrder> getFenxiaoOrdersByTcOrderId(long tcOrderId) throws Exception{
        List<PurchaseOrder> list = new ArrayList<>();
        TaobaoClient client = new DefaultTaobaoClient(OrderCatConfig.getTaobaoApiUrl(), OrderCatConfig.getTaobaoApiAppKey(), OrderCatConfig.getTaobaoApiAppSecret());
        FenxiaoOrdersGetRequest req = new FenxiaoOrdersGetRequest();
        //req.setStatus("WAIT_BUYER_PAY");
        //req.setStartCreated(StringUtils.parseDateTime("2000-01-01 00:00:00"));
        //req.setEndCreated(StringUtils.parseDateTime("2000-01-01 23:59:59"));
       // req.setTimeType("trade_time_type");
        //req.setPageNo(1L);
        //req.setPageSize(10L);
        //req.setPurchaseOrderId(120121243L);
        req.setFields("fenxiao_id,sub_purchase_orders.tc_order_id,sub_purchase_orders.fenxiao_id");
        req.setTcOrderId(tcOrderId);
        FenxiaoOrdersGetResponse rsp = client.execute(req, OrderCatConfig.getTaobaoApiSessionKey());
        Logger.info(rsp.getBody());
        if(rsp.isSuccess()){

            list = rsp.getPurchaseOrders();

        }
        return list;
    }

    /**
     * taobao.fenxiao.refund.get (查询采购单退款信息)
     * @param subOrderId
     * @return
     * @throws Exception
     */
    public RefundDetail getFenxiaoRefundBySubOrderId(long subOrderId) throws Exception{
        RefundDetail rd = null;
        TaobaoClient client = new DefaultTaobaoClient(OrderCatConfig.getTaobaoApiUrl(), OrderCatConfig.getTaobaoApiAppKey(), OrderCatConfig.getTaobaoApiAppSecret());
        FenxiaoRefundGetRequest req = new FenxiaoRefundGetRequest();
        req.setSubOrderId(subOrderId);
        req.setQuerySellerRefund(true);
        FenxiaoRefundGetResponse rsp = client.execute(req, OrderCatConfig.getTaobaoApiSessionKey());

        Logger.info(rsp.getBody());
        if(rsp.isSuccess()){
            rd = rsp.getRefundDetail();
        }

        return rd;
    }




}
