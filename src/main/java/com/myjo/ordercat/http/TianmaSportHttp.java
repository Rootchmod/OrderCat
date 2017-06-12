package com.myjo.ordercat.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.GetRequest;
import com.myjo.ordercat.config.OrderCatConfig;
import com.myjo.ordercat.domain.*;
import com.myjo.ordercat.exception.OCException;
import com.myjo.ordercat.utils.OcDateTimeUtils;
import com.myjo.ordercat.utils.OcSizeUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * Created by lee5hx on 17/4/21.
 */
public class TianmaSportHttp {

//    private static final String VERIFY_CODE_IMAGE_FILE_NAME = "vcode.jpg";
//    private static final String VERIFY_CODE_HTTP_URL = "http://www.tianmasport.com/ms/ImageServlet?time=%d";
//    private static final String LOGIN_HTTP_URL = "http://www.tianmasport.com/ms/beLogin.do";


    private static final Logger Logger = LogManager.getLogger(TianmaSportHttp.class);


    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36";
    private static final String Lee_USER_AGENT = "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)";


    //private final OrderCatConfig orderCatContext;
    private Map<String, String> map;


    public TianmaSportHttp(Map<String, String> map) {
        this.map = map;
    }


    public String main_html() throws Exception {
        Logger.info(" pass into main_html");
        String rt = null;
        HttpResponse<String> jsonResponse = Unirest.get("http://www.tianmasport.com/ms/main.shtml")
                .header("Host", "www.tianmasport.com")
                .header("Connection", "keep-alive")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .header("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4")


                .header("Origin", "http://www.tianmasport.com")
                .header("X-Requested-With", "XMLHttpRequest")
                .header("User-Agent", USER_AGENT)
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .header("Referer", "http://www.tianmasport.com/ms/login.shtml")
                .header("Upgrade-Insecure-Requests", "1")
                .header("Accept-Encoding", "gzip, deflate, sdch")

                .asString();
        rt = jsonResponse.getBody();

        Logger.debug("main_html rt:" + rt);
        return rt;
    }

    public JSONObject login(String verifyCode) throws Exception {
        JSONObject rt = null;
        Logger.info("http tianmaSport login verifyCode: " + verifyCode);

        String sessionId = map.get("seesion_id");
        Logger.info("http tianmaSport login sessionId: " + sessionId);


        HttpResponse<JsonNode> jsonResponse = Unirest.post(OrderCatConfig.getTianmaSportLoginHttpUrl())
                .header("Host", "www.tianmasport.com")
                .header("Connection", "keep-alive")
                .header("Accept", "application/json, text/javascript, */*; q=0.01")
                .header("Origin", "http://www.tianmasport.com")
                .header("X-Requested-With", "XMLHttpRequest")
                .header("User-Agent", USER_AGENT)
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .header("Referer", "http://www.tianmasport.com/ms/login.shtml")
                .header("Accept-Encoding", "gzip, deflate")
                .header("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4")
                //.header("Cookie", sessionId)
                .field("nickName", OrderCatConfig.getTianmaSportUserName())
                .field("pwd", OrderCatConfig.getTianmaSportPassWord())
                .field("verifyCode", verifyCode)
                .field("remember", "on")
                .asJson();
        rt = jsonResponse.getBody().getObject();


        Logger.info("login rt:" + rt);
        return rt;
    }


    public Optional<Boolean> addOrderRemark(String id,String remark) throws Exception {
        boolean rt;

        Logger.debug(String.format("add remark orderid=%s,remark=%s",id,remark));
        String sessionId = map.get("seesion_id");
        Logger.debug("http tianmaSport login sessionId: " + sessionId);


        HttpResponse<JsonNode> jsonResponse = Unirest.post(OrderCatConfig.getTradeOrderAddRemarkHttpUrl())
                .header("Host", "www.tianmasport.com")
                .header("Connection", "keep-alive")
                .header("Accept", "application/json, text/javascript, */*; q=0.01")
                .header("Origin", "http://www.tianmasport.com")
                .header("X-Requested-With", "XMLHttpRequest")
                .header("User-Agent", USER_AGENT)
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .header("Referer", "http://www.tianmasport.com/ms/tradeOrders/myorder_list.shtml")
                .header("Accept-Encoding", "gzip, deflate")
                .header("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4")
                //.header("Cookie", sessionId)
                .field("id", id)
                .field("remark", remark)
                .asJson();
        JSONObject ob = jsonResponse.getBody().getObject();

        rt = ob.getBoolean("success");
        Logger.debug("AddOrderRemark jsonResponse body:" + jsonResponse.getBody());
        return Optional.ofNullable(rt);
    }




    public String getVerifyCodeImage() throws Exception {
        Logger.info("http download verify code image");


        DateTime dt = new DateTime();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String dtStr = dt.toString(fmt);
        long millis = dt.getMillis();
        String vcfile = OrderCatConfig.getTianmaSportVcImageFileName();
        //IOUtils.toByteArray(inputStream);
        File vfile = new File(OrderCatConfig.getOrderCatTempPath() + vcfile);

        Logger.debug(millis);
        Logger.debug(dtStr);

        HttpResponse<InputStream> response =
                Unirest.get(String.format(OrderCatConfig.getTianmaSportVcHttpUrl(), millis))
                        .header("Host", "www.tianmasport.com")
                        .header("Connection", "keep-alive")
                        .header("User-Agent", USER_AGENT)
                        .header("Accept", "image/webp,image/*,*/*;q=0.8")
                        .header("Accept-Encoding", "gzip, deflate, sdch")
                        .header("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4")
                        .asBinary();
//                .field("file", new FileInputStream(vfile)), ContentType.APPLICATION_OCTET_STREAM, "image.jpg")
//                .asJson();

        List<String> setCookie = response.getHeaders().get("Set-Cookie");
        String seesion_id = setCookie.get(0).split("\\;")[0];
        map.put("seesion_id", seesion_id);

        FileUtils.writeByteArrayToFile(vfile, IOUtils.toByteArray(response.getBody()));

        Logger.info("verify code image file:" + vcfile);
        return vcfile;
    }


    public void inventoryDownGroup(String fileName, String brandName, String quarter) throws Exception {
        Logger.info("inventory_down_group_http_url: " + OrderCatConfig.getTianmaSportIDGHttpUrl());
        Logger.info("brandName: " + brandName);
        Logger.info("quarter: " + quarter);

        String sessionId = map.get("seesion_id");
        Logger.info("http tianmaSport login sessionId: " + sessionId);
        HttpResponse<JsonNode> response = Unirest.post(OrderCatConfig.getTianmaSportIDGHttpUrl())
                .header("Host", "www.tianmasport.com")
                .header("Connection", "keep-alive")
                .header("Accept", "*/*")
                .header("Origin", "http://www.tianmasport.com")
                .header("X-Requested-With", "XMLHttpRequest")
                .header("User-Agent", USER_AGENT)
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .header("Referer", "http://www.tianmasport.com/ms/Inventory/grouPurchase.shtml")
                .header("Accept-Encoding", "gzip, deflate")
                .header("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4")
                .field("goods_no", "")
                .field("warehouse_name", "")
                .field("brand_name", brandName)
                .field("minMarketprice", "")
                .field("maxMarketprice", "")
                .field("minInnerNum", "")
                .field("maxInnerNum", "")
                .field("size1", "")
                .field("sex", "")
                .field("division", "")
                .field("cate", "")
                .field("quarter", quarter)
                .field("maxDis", "")
                .field("minDis", "")
                .asJson();
        int code = response.getStatus();
        Logger.info("http-status:" + code);
        Logger.info("http-status-text:" + response.getStatusText());
        JSONObject rt = response.getBody().getObject();
        Logger.info("inventoryDownGroup rt:" + rt);
        if (rt.getBoolean("success") == true) {
            String path = rt.getString("path");
            Logger.info("inventoryDownGroup return path:" + path);
            dataDownLoad(path, fileName);
        }
    }


    public String dataDownLoad(String path, String fileName) throws Exception {
        Logger.info("http data DownLoad:" + path);

        String dfileStr = OrderCatConfig.getOrderCatTempPath() + fileName;
        //IOUtils.toByteArray(inputStream);
        File dfile = new File(dfileStr);

        HttpResponse<InputStream> response =
                Unirest.get(String.format(OrderCatConfig.getTianmaSportDownLoadHttpUrl(), path))
                        .header("Host", "www.tianmasport.com")
                        .header("Connection", "keep-alive")
                        .header("Upgrade-Insecure-Requests", "1")
                        .header("User-Agent", USER_AGENT)
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                        .header("Referer", "http://www.tianmasport.com/ms/Inventory/grouPurchase.shtml")
                        .header("Accept-Encoding", "gzip, deflate, sdch")
                        .header("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4")
                        .asBinary();
//                .field("file", new FileInputStream(vfile)), ContentType.APPLICATION_OCTET_STREAM, "image.jpg")
//                .asJson();
        FileUtils.writeByteArrayToFile(dfile, IOUtils.toByteArray(response.getBody()), true);

        Logger.info("http data DownLoad:" + dfileStr);
        return dfileStr;
    }


    public List<InventoryInfo> getSearchByArticleno(String Articleno) throws Exception {
        List<InventoryInfo> list = new ArrayList<>();
        String jsonstr = "";
        Logger.info("getSearchByArticlenoHttpUrl: " + OrderCatConfig.getSearchByArticlenoHttpUrl());
        Logger.info("Articleno: " + Articleno);
        String sessionId = map.get("seesion_id");
        Logger.info("http tianmaSport login sessionId: " + sessionId);
        HttpResponse<String> response = Unirest.post(OrderCatConfig.getSearchByArticlenoHttpUrl())
                .header("Host", "www.tianmasport.com")
                .header("Connection", "keep-alive")
                .header("Accept", "*/*")
                .header("Origin", "http://www.tianmasport.com")
                .header("X-Requested-With", "XMLHttpRequest")
                .header("User-Agent", USER_AGENT)
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .header("Referer", "http://www.tianmasport.com/ms/order/quickOrder.shtml")
                .header("Accept-Encoding", "gzip, deflate")
                .header("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4")
                .field("articleno", Articleno)
                .asString();
        int code = response.getStatus();
        Logger.debug("http-status:" + code);
        Logger.debug("http-status-text:" + response.getStatusText());

        if (code == 200) {
            String rt = response.getBody();
            if (rt.indexOf("没有类似货号的商品!") > -1) {
                return list;
            }
            Document doc = Jsoup.parse(rt);
            Element script = doc.select("script").get(1);
            String data = script.data();
            String bstr = "var data = $.parseJSON('";
            int bstr_index = data.indexOf(bstr);
            int estr_index = data.indexOf("');", bstr_index);
            jsonstr = data.substring(bstr_index + bstr.length(), estr_index);
            com.alibaba.fastjson.JSONObject object = JSON.parseObject(jsonstr);
            JSONArray array = object.getJSONArray("rows");
            com.alibaba.fastjson.JSONObject jsonObject;
            InventoryInfo inventoryInfo;

            String dd1; //配货率
            String dd2; //发货时效
            for (int i = 0; i < array.size(); i++) {

                jsonObject = array.getJSONObject(i);
                dd1 = StringUtils.substringBeforeLast(jsonObject.getString("pickRate"), "%");
                dd2 = StringUtils.substringAfterLast(jsonObject.getString("pickRate"), "发货时效:");

                inventoryInfo = new InventoryInfo();
                inventoryInfo.setWareHouseID(Integer.valueOf(jsonObject.getString("wareHouseID")));
                inventoryInfo.setWarehouseName(jsonObject.getString("wareHouseName"));
                inventoryInfo.setPickRate(Integer.valueOf(dd1.replaceAll("配货率：", "")));
                inventoryInfo.setThedtime(dd2.replaceAll("小时", ""));
                inventoryInfo.setPickDate(PickDate.valueOf(Integer.valueOf(jsonObject.getString("pick_date"))));
                inventoryInfo.setMark(jsonObject.getString("mark"));
                inventoryInfo.setRetrunDesc(jsonObject.getString("retrun_desc"));
                inventoryInfo.setExpressName(jsonObject.getString("expressName"));
                inventoryInfo.setReturnRate(Integer.valueOf(jsonObject.getString("returnRate")));
                inventoryInfo.setEndT(jsonObject.getString("endT"));


                inventoryInfo.setUpdateTime(OcDateTimeUtils.string2LocalDateTime(jsonObject.getString("updateTime")));
                list.add(inventoryInfo);
            }
            //Logger.info("JSON-String:" + jsonstr);
        }
        return list;

    }


//    public List<Trade> getSoldTrades(Date begin, Date end, TradeStatus status) throws Exception {
//        long pageNo = 1l;
//        long pageSize = 100l;
//        List<Trade> rtlist = new ArrayList<>();
//
//        PageResult<Trade> pageResult;
//        do {
//            pageResult = getSoldTrades(begin, end, status, pageNo, pageSize);
//            rtlist.addAll(pageResult.getRows());
//            Logger.debug("Math.ceil((double)pageResult.getTotal() / pageSize):" + Math.ceil((double) pageResult.getTotal() / pageSize));
//            //++pageNo;
//        } while (Math.ceil((double) pageResult.getTotal() / pageSize) >= (++pageNo));
//
//        return rtlist;
//    }

    public List<TianmaOrder> tradeOrderDataList(String startTime, String endTime, TianmaOrderStatus orderStatus, String sort) throws Exception {
        List<TianmaOrder> rtlist = new ArrayList<>();
        int pageNo = 1;
        int pageSize = 300;

        PageResult<TianmaOrder> pageResult;
        do {
            pageResult = tradeOrderDataList(startTime, endTime, orderStatus, sort,pageNo, pageSize);
            rtlist.addAll(pageResult.getRows());
            Logger.debug("Math.ceil((double)pageResult.getTotal() / pageSize):" + Math.ceil((double) pageResult.getTotal() / pageSize));
        } while (Math.ceil((double) pageResult.getTotal() / pageSize) >= (++pageNo));
        return rtlist;
    }


    public PageResult<TianmaOrder> tradeOrderDataList(String startTime,
                                                      String endTime,
                                                      TianmaOrderStatus orderStatus,
                                                      String sort,
                                                      Integer pageNo,
                                                      Integer pageSize) throws Exception {
        Logger.info("inventory_down_group_http_url: " + OrderCatConfig.getTianmaSportIDGHttpUrl());
        Logger.info(String.format("startTime:%s endTime:%s order_status:%s",
                startTime==null?"":startTime,
                endTime==null?"":endTime,
                orderStatus==null?"":orderStatus.getVal()));

        PageResult<TianmaOrder> pr = new PageResult();


        List<TianmaOrder> orders = new ArrayList<>();

        String sessionId = map.get("seesion_id");
        Logger.info("http tianmaSport login sessionId: " + sessionId);
        HttpResponse<JsonNode> response = Unirest.post(OrderCatConfig.getTradeOrdersDataListHttpUrl())
                .header("Host", "www.tianmasport.com")
                .header("Connection", "keep-alive")
                .header("Accept", "*/*")
                .header("Origin", "http://www.tianmasport.com")
                .header("X-Requested-With", "XMLHttpRequest")
                .header("User-Agent", USER_AGENT)
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .header("Referer", "http://www.tianmasport.com/ms/Inventory/grouPurchase.shtml")
                .header("Accept-Encoding", "gzip, deflate")
                .header("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4")
                //查询参数
                .field("page", pageNo.intValue())
                .field("rows", pageSize.intValue())
                .field("status", orderStatus==null?"":orderStatus.getVal())
                .field("m_warehouse_name", "")
                .field("goods_no", "")
                .field("names", "")
                .field("startTime", startTime==null?"":startTime)
                .field("endsTime", endTime==null?"":endTime)
                .field("size", "")
                .field("sort", sort==null?"":sort) //feed_back_time
                .field("order", "desc")
                .field("outer_tid", "")
                .field("order_id", "")
                .asJson();
        int code = response.getStatus();

        Logger.info("http-status:" + code);
        Logger.info("http-status-text:" + response.getStatusText());

        JSONObject rt = response.getBody().getObject();
        //Logger.info("inventoryDownGroup rt:" + rt);
        if (code == 200) {
            pr.setTotal(rt.getInt("total"));
            org.json.JSONArray rows = rt.getJSONArray("rows");
            JSONObject order;
            TianmaOrder tianmaOrder;
            for (int i = 0; i < rows.length(); i++) {
                tianmaOrder = new TianmaOrder();
                order = rows.getJSONObject(i);

                tianmaOrder.setCreated(order.get("created").toString());
                tianmaOrder.setDeliveryName(order.get("delivery").toString());
                tianmaOrder.setDeliveryNo(order.get("p_delivery_no").toString());
                tianmaOrder.setFeedBackTime(order.get("feed_back_time").toString());
                tianmaOrder.setName(order.get("name").toString());
                tianmaOrder.setNoShipmentRemark(order.get("no_shipment_remark").toString());
                tianmaOrder.setOrderId(order.get("order_id").toString());
                tianmaOrder.setOuterOrderId(order.get("outer_order_id").toString());
                tianmaOrder.setSize1(order.get("size1").toString());

                if(OcSizeUtils.getClothesConversionSize1(tianmaOrder.getSize1()).indexOf("error")>-1){
                    tianmaOrder.setSize1(tianmaOrder.getSize1());
                }else {
                    tianmaOrder.setSize1(OcSizeUtils.getClothesConversionSize1(tianmaOrder.getSize1()));
                }


                tianmaOrder.setSize2(order.get("size2").toString());
                tianmaOrder.setPayPrice(order.getBigDecimal("pay_price"));
                tianmaOrder.setPostFee(order.getBigDecimal("post_fee"));
                tianmaOrder.setWarehouseId(order.getInt("m_warehouse_id"));
                tianmaOrder.setWarehouseName(order.getString("m_warehouse_name"));
                tianmaOrder.setStatus(TianmaOrderStatus.valueOf1(order.get("status").toString()));
                tianmaOrder.setGoodsNo(order.get("goods_no").toString());



                orders.add(tianmaOrder);

            }
            pr.setRows(orders);
        } else {

            throw new OCException("获取天马反馈订单失败:" + code);
        }
        return pr;
    }


    public Optional<LogisticsCompany> ajaxGuessMailNoRequest(String mailNo, String tradeId) throws Exception {
        //https://wuliu.taobao.com/user/ajax_guess_mail_no.do?code=utf-8&mailNo=3921971273918
        LogisticsCompany logisticsCompany = null;

        String requestJsonFile = OrderCatConfig.getOrderCatTempPath()+"ajax_guess_mail_no_request.json";
        String requestJsonStr = FileUtils.readFileToString(new File(requestJsonFile),"UTF-8");
        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(requestJsonStr);
        GetRequest getRequest = Unirest.get(String.format("https://wuliu.taobao.com/user/ajax_guess_mail_no.do?code=utf-8&mailNo=%s", mailNo));
        getRequest  = getRequest.header("Host", "wuliu.taobao.com");
        getRequest  = getRequest.header("Connection", "keep-alive");
//        {
//            "Accept": "application/json, text/javascript, */*; q=0.01",
//                "X-DevTools-Emulate-Network-Conditions-Client-Id": "a4cfb530-24eb-4b35-93f9-fb41aab8f0ad",
//                "X-Requested-With": "XMLHttpRequest",
//                "X-DevTools-Request-Id": "7324.2138",
//                "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36",
//                "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8",
//                "Referer": "https://wuliu.taobao.com/user/consign.htm?trade_id=23055370418247368",
//                "Accept-Encoding": "gzip, deflate, sdch, br",
//                "Accept-Language": "zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4",
//                "Cookie": "_tb_token_=HB9E6HKteq; thw=cn; lui=VAKFDSkq4kYp; luo=Uok%3D; x=738840638; uc3=sg2=ACIKPdBNvzXQZZ1%2F6JueC%2FRAZLgHu10UukDKGHjL3x4%3D&nk2=&id2=&lg2=; uss=VWn19n9GAJhytItVeCtkdiTm0IUMb4iI77oYPuWlTj83RPL9PBq3DJMP7g%3D%3D; tracknick=; sn=%E9%BA%A6%E5%B7%A8%E9%9E%8B%E7%B1%BB%E4%B8%93%E8%90%A5%E5%BA%97%3Alee5hx; skt=6f88357582fbda2b; v=0; cookie2=1c6693fa9385a3eca2c7fdc21381eb79; unb=3277376423; t=e60a0198245b4cd5661acbb044370c7c; l=Ao6OW5cItfkAIF/B8mbzJucxXm9RW1JN; isg=Avv7jte1JiViLhqTROucjAWBitm7TQ9S4HjJJ-25g_pVTBMuYyHbonBMEFr5; cna=rGepEXF8izACAS9KB+o89YoQ; uc1=cookie14=UoW%2Bvf0UQFk1zQ%3D%3D&lng=zh_CN"
//        }

        getRequest  = getRequest.header("Accept", jsonObject.getString("Accept"));
        getRequest  = getRequest.header("X-Requested-With", jsonObject.getString("X-Requested-With"));
        getRequest  = getRequest.header("User-Agent", jsonObject.getString("User-Agent"));
        getRequest  = getRequest.header("Referer", String.format("https://wuliu.taobao.com/user/consign.htm?trade_id=%s",tradeId));
        getRequest  = getRequest.header("Accept-Encoding", jsonObject.getString("Accept-Encoding"));
        getRequest  = getRequest.header("Accept-Language", jsonObject.getString("Accept-Language"));
        getRequest  = getRequest.header("Cookie", jsonObject.getString("Cookie"));

        HttpResponse<JsonNode> response = getRequest.asJson();

        if(response.getStatus()==200){
            JSONObject jsonObject1 = response.getBody().getObject();
            boolean success = jsonObject1.getBoolean("success");
            if(success){
                org.json.JSONArray datas = jsonObject1.getJSONArray("data");
                for(int i=0;i<datas.length();i++){
                    logisticsCompany = new LogisticsCompany();
                    jsonObject1 = datas.getJSONObject(i);
                    logisticsCompany.setCode(jsonObject1.getString("cpCode"));
                    logisticsCompany.setName(jsonObject1.getString("cpName"));
                    break;
                }
            }else {
                throw new OCException("ajaxGuessMailNoRequest 请求失败");
            }
        }
        Logger.debug("ajaxGuessMailNoRequest--response-str:" + response.getBody());
        return Optional.ofNullable(logisticsCompany);
    }


}
