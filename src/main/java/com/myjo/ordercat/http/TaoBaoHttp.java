package com.myjo.ordercat.http;

import com.myjo.ordercat.config.OrderCatConfig;
import com.myjo.ordercat.domain.*;
import com.myjo.ordercat.domain.constant.TradeStatus;
import com.myjo.ordercat.exception.OCException;
import com.myjo.ordercat.utils.OcDateTimeUtils;
import com.myjo.ordercat.utils.OcListUtils;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.*;
import com.taobao.api.domain.LogisticsCompany;
import com.taobao.api.internal.tmc.TmcClient;
import com.taobao.api.request.*;
import com.taobao.api.response.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
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

        //Logger.debug("getTaobaoItemsOnSale.pageResult.getRows().size:" + pageResult.getRows().size());
        //Logger.debug("getTaobaoItemsOnSale.pageResult.getTotal():" + pageResult.getTotal());
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
            updateTmallItemQuantityUpdate(itemId, list, csvListSukMap, tmallPriceUpdateInfos);//库存更新
        }

        //过滤为空的SKU
        tmallPriceUpdateInfos = tmallPriceUpdateInfos
                .parallelStream()
                .filter(t -> t != null)
                .sorted((o1, o2) -> o2.getSalesPrice().compareTo(o1.getSalesPrice()))
                .collect(Collectors.toList());

        List<List<TmallPriceUpdateInfo>> subTmallPriceUpdateInfos = OcListUtils.splitList(tmallPriceUpdateInfos, 19);

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
     * ERR_RULE_CATEGORY_CANNOT_BLANK
     *
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

        if (price != null) {
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
    public Long updateTmallItemQuantityUpdate(Long itemId, List<Sku> skuList, Map<String, InventoryInfo> csvListSukMap, List<TmallPriceUpdateInfo> tmallPriceUpdateInfos) throws Exception {

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
     *
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
        if (rsp.isSuccess()) {
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
     * taobao.trades.sold.get (查询卖家已卖出的交易数据（根据创建时间）)
     * WAIT_BUYER_PAY：等待买家付款
     * WAIT_SELLER_SEND_GOODS：等待卖家发货
     * SELLER_CONSIGNED_PART：卖家部分发货
     * WAIT_BUYER_CONFIRM_GOODS：等待买家确认收货
     * TRADE_BUYER_SIGNED：买家已签收（货到付款专用）
     * TRADE_FINISHED：交易成功
     * TRADE_CLOSED：交易关闭
     * TRADE_CLOSED_BY_TAOBAO：交易被淘宝关闭
     * TRADE_NO_CREATE_PAY：没有创建外部交易（支付宝交易）
     * WAIT_PRE_AUTH_CONFIRM：余额宝0元购合约中
     * PAY_PENDING：外卡支付付款确认中
     * ALL_WAIT_PAY：所有买家未付款的交易（包含：WAIT_BUYER_PAY、TRADE_NO_CREATE_PAY）
     * ALL_CLOSED：所有关闭的交易（包含：TRADE_CLOSED、TRADE_CLOSED_BY_TAOBAO）
     *
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
            if (pageResult != null) {
                rtlist.addAll(pageResult.getRows());
            }
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
        if (status != null) {
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
            List<Trade> list = rsp.getTrades();
            if (list == null) {
                list = new ArrayList<>();
            }
            pr.setRows(list);
            pr.setTotal(rsp.getTotalResults());
        }
        return pr;
    }


    /**
     * taobao.refunds.receive.get (查询卖家收到的退款列表)
     *
     * @param begin
     * @param end
     * @return
     * @throws Exception
     */
    public List<Refund> getReceiveRefunds(String status, Date begin, Date end) throws Exception {
        long pageNo = 1l;
        long pageSize = 100l;
        List<Refund> rtlist = new ArrayList<>();

        PageResult<Refund> pageResult;
        do {
            pageResult = getReceiveRefunds(status, begin, end, pageNo, pageSize);
            if(pageResult.getRows()!=null){
                rtlist.addAll(pageResult.getRows());
            }
            Logger.debug("Math.ceil((double)pageResult.getTotal() / pageSize):" + Math.ceil((double) pageResult.getTotal() / pageSize));
            //++pageNo;
        } while (Math.ceil((double) pageResult.getTotal() / pageSize) >= (++pageNo));

//        Refund temp;
//        for (Refund refund : rtlist) {
//            temp = getRefundById(refund.getRefundId());
//            refund.setNumIid(temp.getNumIid());
//            refund.setOuterId(temp.getOuterId());
//            refund.setOrderStatus(temp.getOrderStatus());
//        }
        return rtlist;
    }

    /**
     * taobao.refunds.receive.get (查询卖家收到的退款列表)
     *
     * @param begin
     * @param end
     * @param pageNo
     * @param pageSize
     * @return
     * @throws Exception
     */
    public PageResult<Refund> getReceiveRefunds(String status, Date begin, Date end, Long pageNo, Long pageSize) throws Exception {
        PageResult<Refund> pr = new PageResult();
        TaobaoClient client = new DefaultTaobaoClient(OrderCatConfig.getTaobaoApiUrl(), OrderCatConfig.getTaobaoApiAppKey(), OrderCatConfig.getTaobaoApiAppSecret());
        RefundsReceiveGetRequest req = new RefundsReceiveGetRequest();
        req.setFields("refund_id,tid,num,refund_phase,good_status title, numIid,buyer_nick, seller_nick, total_fee, status, created, refund_fee, oid, company_name, sid, payment, reason, desc, has_good_return, modified, order_status");

        if (status != null) {
            req.setStatus(status);
        }
//        req.setBuyerNick("hz0799");
//        req.setType("fixed");
        req.setStartModified(begin);
        req.setEndModified(end);
        req.setPageNo(pageNo);
        req.setPageSize(pageSize);
        req.setUseHasNext(false);
        RefundsReceiveGetResponse rsp = client.execute(req, OrderCatConfig.getTaobaoApiSessionKey());
        if (rsp.isSuccess()) {
            pr.setRows(rsp.getRefunds());
            pr.setTotal(rsp.getTotalResults());
        }
        return pr;
    }

    /**
     * taobao.refund.get (获取单笔退款详情)
     *
     * @param refundId
     * @return
     * @throws Exception
     */
    public Refund getRefundById(long refundId) throws Exception {
        Refund refund = null;
        TaobaoClient client = new DefaultTaobaoClient(OrderCatConfig.getTaobaoApiUrl(), OrderCatConfig.getTaobaoApiAppKey(), OrderCatConfig.getTaobaoApiAppSecret());
        RefundGetRequest req = new RefundGetRequest();
        req.setFields("refund_phase,refund_version,refund_id,refund_fee");
        req.setRefundId(refundId);
        RefundGetResponse rsp = client.execute(req, OrderCatConfig.getTaobaoApiSessionKey());
        if (rsp.isSuccess()) {
            refund = rsp.getRefund();
        }
        return refund;
    }


    public Optional<PurchaseOrder> getPurchaseOrderByTcOrderId(Long tcOrderId) {
        PurchaseOrder purchaseOrder = null;
        //天马退款处理流程
        List<PurchaseOrder> list = null;
        try {
            list = getFenxiaoOrders(tcOrderId, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (list != null && list.size() == 1) {
            purchaseOrder = list.get(0);
        }
        return Optional.ofNullable(purchaseOrder);
    }


    public List<PurchaseOrder> getFenxiaoOrders(Long tcOrderId, String status, Date begin, Date end) throws Exception {
        long pageNo = 1l;
        long pageSize = 49l;
        List<PurchaseOrder> rtlist = new ArrayList<>();

        PageResult<PurchaseOrder> pageResult;
        do {
            pageResult = getFenxiaoOrders(tcOrderId, status, begin, end, pageNo, pageSize);
            if (pageResult.getRows() != null) {
                rtlist.addAll(pageResult.getRows());
            }
            Logger.debug("Math.ceil((double)pageResult.getTotal() / pageSize):" + Math.ceil((double) pageResult.getTotal() / pageSize));
            //++pageNo;
        } while (Math.ceil((double) pageResult.getTotal() / pageSize) >= (++pageNo));

        return rtlist;
    }


    /**
     * taobao.fenxiao.orders.get (查询采购单信息)
     *
     * @return
     * @throws Exception
     */
    public PageResult<PurchaseOrder> getFenxiaoOrders(Long tcOrderId, String status, Date begin, Date end, Long pageNo, Long pageSize) {

        PageResult<PurchaseOrder> pr = new PageResult();
        List<PurchaseOrder> list;
        TaobaoClient client = new DefaultTaobaoClient(OrderCatConfig.getTaobaoApiUrl(), OrderCatConfig.getTaobaoApiAppKey(), OrderCatConfig.getTaobaoApiAppSecret());
        FenxiaoOrdersGetRequest req = new FenxiaoOrdersGetRequest();
        if (status != null) {
            req.setStatus(status);
        }
        if (begin != null) {
            req.setStartCreated(begin);
        }
        if (end != null) {
            req.setEndCreated(end);
        }
        // req.setTimeType("trade_time_type");
        req.setPageNo(pageNo);
        if (tcOrderId != null) {
            req.setTcOrderId(tcOrderId);
        }
        req.setPageSize(pageSize);
        //req.setPurchaseOrderId(120121243L);
        req.setFields("id,status,buyer_payment,fenxiao_id,tc_order_id,status,sub_purchase_orders.tc_order_id,sub_purchase_orders.fenxiao_id,sub_purchase_orders.status");
        //req.setTcOrderId(tcOrderId);
        try {
            FenxiaoOrdersGetResponse rsp = client.execute(req, OrderCatConfig.getTaobaoApiSessionKey());
            Logger.info(rsp.getBody());
            if (rsp.isSuccess()) {
                list = rsp.getPurchaseOrders();
                pr.setRows(list);
                pr.setTotal(rsp.getTotalResults());
            }
        } catch (Exception e) {
            Logger.error(e);
        }
        return pr;
    }

    /**
     * taobao.fenxiao.refund.get (查询采购单退款信息)
     *
     * @param subOrderId
     * @return
     * @throws Exception
     */
    public RefundDetail getFenxiaoRefundBySubOrderId(long subOrderId) throws Exception {
        RefundDetail rd = null;
        TaobaoClient client = new DefaultTaobaoClient(OrderCatConfig.getTaobaoApiUrl(), OrderCatConfig.getTaobaoApiAppKey(), OrderCatConfig.getTaobaoApiAppSecret());
        FenxiaoRefundGetRequest req = new FenxiaoRefundGetRequest();
        req.setSubOrderId(subOrderId);
        req.setQuerySellerRefund(true);
        FenxiaoRefundGetResponse rsp = client.execute(req, OrderCatConfig.getTaobaoApiSessionKey());

        Logger.info(rsp.getBody());
        if (rsp.isSuccess()) {
            rd = rsp.getRefundDetail();
        }

        return rd;
    }

    /**
     * taobao.logistics.offline.send (自己联系物流（线下物流）发货)
     *
     * @throws Exception
     */
    public Optional<ReturnResult<Shipping>> sendTaobaoLogisticsOffline(long tid, String outSid, String companyCode) throws Exception {
        ReturnResult<Shipping> rt = new ReturnResult<>();
        //       TaobaoClient client = new DefaultTaobaoClient(OrderCatConfig.getTaobaoApiUrl(), OrderCatConfig.getTaobaoApiAppKey(), OrderCatConfig.getTaobaoApiAppSecret());
//        LogisticsOnlineSendRequest req = new LogisticsOnlineSendRequest();
//        req.setTid(tid);
        //req.setIsSplit(0L);
//        req.setOutSid(outSid);
//        req.setCompanyCode(companyCode);
        //req.setSenderId(123456L);
        //req.setCancelId(123456L);
        //req.setFeature("identCode=tid:aaa,bbb;machineCode=tid2:aaa");
        //req.setSellerIp("192.168.1.10");
        //LogisticsOnlineSendResponse rsp = client.execute(req, OrderCatConfig.getTaobaoApiSessionKey());


        TaobaoClient client = new DefaultTaobaoClient(OrderCatConfig.getTaobaoApiUrl(), OrderCatConfig.getTaobaoApiAppKey(), OrderCatConfig.getTaobaoApiAppSecret());
        LogisticsOfflineSendRequest req = new LogisticsOfflineSendRequest();
        //req.setSubTid("1,2,3");
        req.setTid(tid);
        //req.setIsSplit(0L);
        req.setOutSid(outSid);
        req.setCompanyCode(companyCode);
        //req.setSenderId(123456L);
        //req.setCancelId(123456L);
        //req.setFeature("identCode=tid:aaa,bbb;machineCode=tid2:aaa");
        //req.setSellerIp("192.168.1.10");
        LogisticsOfflineSendResponse rsp = client.execute(req, OrderCatConfig.getTaobaoApiSessionKey());
        Logger.debug(rsp.getBody());
        if (rsp.isSuccess()) {
            rt.setSuccess(true);
            rt.setResult(rsp.getShipping());
        } else {
            rt.setSuccess(false);
            rt.setErrorCode(rsp.getErrorCode());
            rt.setErrorMessages(rsp.getMsg() + "|" + rsp.getSubMsg());
            Logger.error(rsp.getErrorCode() + ":" + rsp.getMsg());
        }


        return Optional.ofNullable(rt);
    }


    public void tmc_test() throws Exception {

        TaobaoClient client = new DefaultTaobaoClient(OrderCatConfig.getTaobaoApiUrl(), OrderCatConfig.getTaobaoApiAppKey(), OrderCatConfig.getTaobaoApiAppSecret());
        TmcUserPermitRequest req = new TmcUserPermitRequest();
        req.setTopics("taobao_trade_TradeBuyerPay,taobao_trade_TradeChanged");
        TmcUserPermitResponse rsp = client.execute(req, OrderCatConfig.getTaobaoApiSessionKey());
        System.out.println(rsp.getBody());


        TmcClient client1 = new TmcClient(OrderCatConfig.getTaobaoApiAppKey(), OrderCatConfig.getTaobaoApiAppSecret(), "default"); // 关于default参考消息分组说明
        client1.setMessageHandler((message, status) -> {
            try {
                System.out.println(message.getContent());
                System.out.println(message.getTopic());

            } catch (Exception e) {
                e.printStackTrace();
                status.fail(); // 消息处理失败回滚，服务端需要重发
                // 重试注意：不是所有的异常都需要系统重试。
                // 对于字段不全、主键冲突问题，导致写DB异常，不可重试，否则消息会一直重发
                // 对于，由于网络问题，权限问题导致的失败，可重试。
                // 重试时间 5分钟不等，不要滥用，否则会引起雪崩
            }
        });
        client1.connect("ws://mc.api.taobao.com");
        Thread.sleep(100000 * 100000);


    }


    public void consumer_test() throws Exception {

//        Properties properties = new Properties();
//        properties.put(PropertyKeyConst.ConsumerId, "CID_OC_1");
//        properties.put(PropertyKeyConst.AccessKey, OrderCatConfig.getTaobaoApiAppKey());
//        properties.put(PropertyKeyConst.SecretKey, OrderCatConfig.getTaobaoApiAppSecret());
//        Consumer consumer = ONSFactory.createConsumer(properties);
//        consumer.subscribe("rmq_sys_jst_23279400", "*", new MessageListener() {
//            public Action consume(Message message, ConsumeContext context) {
//                System.out.println("Receive: " + message);
//                return Action.CommitMessage;
//            }
//        });
//        consumer.start();
//        System.out.println("Consumer Started");

    }


    //taobao.trade.get (获取单笔交易的部分信息(性能高))
    public Optional<Trade> getTaobaoTrade(long tid) {
        Trade trade = null;
        TaobaoClient client = new DefaultTaobaoClient(OrderCatConfig.getTaobaoApiUrl(), OrderCatConfig.getTaobaoApiAppKey(), OrderCatConfig.getTaobaoApiAppSecret());
        TradeGetRequest req = new TradeGetRequest();

        req.setFields("tid,title,buyer_nick,type,status,num,payment,orders,created,pay_time,price,discount_fee,total_fee,is_daixiao");
        req.setTid(tid);
        try {
            TradeGetResponse rsp = client.execute(req, OrderCatConfig.getTaobaoApiSessionKey());
            if (rsp.isSuccess()) {
                trade = rsp.getTrade();
            }
        } catch (Exception e) {
            Logger.error(e);
        }
        return Optional.ofNullable(trade);
    }


    //taobao.trade.fullinfo.get

    public Optional<Trade> getTaobaoTradeFullInfo(long tid) {


//        receiver_name String 东方不败收货人的姓名
//        receiver_state String 浙江省收货人的所在省份
//        receiver_address String 淘宝城911号收货人的详细地址
//        receiver_zip String 223700收货人的邮编
//        receiver_mobile String 13512501826收货人的手机号码
//        receiver_phone String 13819175372收货人的电话号码
//        consign_time Date 2000-01-01 00:00:00卖家发货时间。格式:yyyy-MM-dd HH:mm:ss
//        received_payment String 200.07卖家实际收到的支付宝打款金额（由于子订单可以部分确认收货，这个金额会随着子订单的确认收货而不断增加，交易成功后等于买家实付款减去退款金额）。精确到2位小数;单位:元。如:200.07，表示:200元7分
//        receiver_city
//        receiver_district


        Trade trade = null;
        TaobaoClient client = new DefaultTaobaoClient(OrderCatConfig.getTaobaoApiUrl(), OrderCatConfig.getTaobaoApiAppKey(), OrderCatConfig.getTaobaoApiAppSecret());
        TradeFullinfoGetRequest req = new TradeFullinfoGetRequest();
        req.setFields("tid,type,created,seller_memo,buyer_nick,seller_flag,status,num,payment,orders,receiver_name,receiver_state,receiver_address,receiver_zip,receiver_mobile,receiver_phone,received_payment,receiver_city,receiver_district,buyer_message,is_daixiao,orders.refund_id");
        req.setTid(tid);
        try {
            TradeFullinfoGetResponse rsp = client.execute(req, OrderCatConfig.getTaobaoApiSessionKey());
            if (rsp.isSuccess()) {
                trade = rsp.getTrade();
            }
        } catch (Exception e) {
            Logger.error(e);
        }
        return Optional.ofNullable(trade);
    }

    public void addTradeMemo(long tid, String memo, long flag) {

        TaobaoClient client = new DefaultTaobaoClient(OrderCatConfig.getTaobaoApiUrl(), OrderCatConfig.getTaobaoApiAppKey(), OrderCatConfig.getTaobaoApiAppSecret());
        TradeMemoAddRequest req = new TradeMemoAddRequest();
        req.setTid(tid);
        req.setMemo(memo);
        req.setFlag(flag);

        try {
            TradeMemoAddResponse rsp = client.execute(req, OrderCatConfig.getTaobaoApiSessionKey());
            if (rsp.isSuccess()) {
                Logger.info(rsp.getBody());
            }
        } catch (Exception e) {
            Logger.error(e);
        }
    }

    public void updateTradeMemo(long tid, String memo, long flag) {

        TaobaoClient client = new DefaultTaobaoClient(OrderCatConfig.getTaobaoApiUrl(), OrderCatConfig.getTaobaoApiAppKey(), OrderCatConfig.getTaobaoApiAppSecret());
        TradeMemoUpdateRequest req = new TradeMemoUpdateRequest();
        req.setTid(tid);
        req.setMemo(memo);
        req.setFlag(flag);
        req.setReset(false);
        try {
            TradeMemoUpdateResponse rsp = client.execute(req,  OrderCatConfig.getTaobaoApiSessionKey());
            if (rsp.isSuccess()) {
                Logger.info(rsp.getBody());
            }
        } catch (Exception e) {
            Logger.error(e);
        }
    }



    /**
     * taobao.rp.refunds.agree (同意退款)
     * 退款信息，格式：refund_id|amount|version|phase，
     * 其中refund_id为退款编号，amount为退款金额（以分为单位），
     * version为退款最后更新时间（时间戳格式），
     * phase为退款阶段（可选值为：onsale, aftersale，天猫退款必值，淘宝退款不需要传），
     * 多个退款以半角逗号分隔。
     *
     * @param refundId
     * @param amount
     * @param version
     * @param phase
     */
    public ReturnResult<RefundMappingResult> agreeTaobaoRpRefunds(String refundInfos,String sessionKey,String code) {
        ReturnResult<RefundMappingResult> rr = new ReturnResult<>();
        TaobaoClient client = new DefaultTaobaoClient(OrderCatConfig.getTaobaoApiUrl(), OrderCatConfig.getTaobaoApiAppKey(), OrderCatConfig.getTaobaoApiAppSecret());
        RpRefundsAgreeRequest req = new RpRefundsAgreeRequest();
        //req.setCode("839212");
//        String refundInfos = String.format("%d|%d|%d|%s",
//                refundId,
//                amount,
//                version,
//                phase
//        );
        Logger.info(String.format("refundInfos[%s]", refundInfos));
        req.setRefundInfos(refundInfos);
        req.setCode(code);

        RpRefundsAgreeResponse rsp;
        try {
            rsp = client.execute(req, sessionKey);
            if (rsp.isSuccess()) {
                rr.setSuccess(rsp.getSucc());
                rr.setResult(rsp.getResults().get(0));
            } else {
                rr.setErrorCode(rsp.getErrorCode());
                rr.setErrorMessages(String.format("[msg:%s]-[subCode:%s]-[subMsg:%s]",rsp.getMsg(),rsp.getSubCode(),rsp.getSubMsg()));
                rr.setSuccess(false);
            }
        } catch (ApiException e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            rr.setSuccess(false);
            rr.setErrorCode("OC-90001");
            rr.setErrorMessages(errors.toString());
            e.printStackTrace();
        }
        return rr;
    }


    /**
     * taobao.fenxiao.refund.create (创建退款)
     *
     * @return
     */
    public ReturnResult<String> createTaobaoFeixiaoRefund(long subOrderId,boolean isReturnGoods,long returnFee) {

        ReturnResult<String> rr = new ReturnResult<>();
        TaobaoClient client = new DefaultTaobaoClient(OrderCatConfig.getTaobaoApiUrl(), OrderCatConfig.getTaobaoApiAppKey(), OrderCatConfig.getTaobaoApiAppSecret());
        FenxiaoRefundCreateRequest req = new FenxiaoRefundCreateRequest();
        req.setSubOrderId(subOrderId);
        req.setIsReturnGoods(isReturnGoods);
        req.setIsReturnPostFee(true);
        req.setReturnFee(returnFee);
        req.setRefundReasonId(4L);
        req.setRefundDesc("OC-自动创建退款");
        FenxiaoRefundCreateResponse rsp;
        try {
            rsp = client.execute(req, OrderCatConfig.getTaobaoApiSessionKey());
            if (rsp.isSuccess()) {
                rr.setSuccess(true);
                rr.setResult("供销退款申请成功");
            } else {
                rr.setErrorCode(rsp.getErrorCode());
                rr.setErrorMessages("退款申请失败!");
                rr.setSuccess(false);
            }
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            rr.setSuccess(false);
            rr.setErrorCode("OC-90002");
            rr.setErrorMessages(errors.toString());
            e.printStackTrace();
        }
        return rr;

    }


}
