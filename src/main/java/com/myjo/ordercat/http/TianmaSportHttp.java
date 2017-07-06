package com.myjo.ordercat.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import com.mashape.unirest.request.body.MultipartBody;
import com.myjo.ordercat.config.OrderCatConfig;
import com.myjo.ordercat.domain.*;
import com.myjo.ordercat.exception.OCException;
import com.myjo.ordercat.job.OcBaseJob;
import com.myjo.ordercat.utils.OcDateTimeUtils;
import com.myjo.ordercat.utils.OcEncryptionUtils;
import com.myjo.ordercat.utils.OcSizeUtils;
import org.apache.commons.collections.FastHashMap;
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
import java.math.BigDecimal;
import java.util.*;


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
        HttpResponse<String> jsonResponse = Unirest.get(OrderCatConfig.getTianmaMainHtml())
                .header("Host", OrderCatConfig.getTianmaSportHost())
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


        String pwd = OcEncryptionUtils.base64Decoder(OrderCatConfig.getTianmaSportPassWord(),5);
        String nickName = OrderCatConfig.getTianmaSportUserName();

        Logger.info("http tianmaSport login pwd: " + pwd);
        Logger.info("http tianmaSport login nickName: " + nickName);



        HttpResponse<JsonNode> jsonResponse = Unirest.post(OrderCatConfig.getTianmaSportLoginHttpUrl())
                .header("Host", OrderCatConfig.getTianmaSportHost())
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
                .field("nickName", nickName)
                .field("pwd", pwd)
                .field("verifyCode", verifyCode)
                .field("remember", "on")
                .asJson();
        rt = jsonResponse.getBody().getObject();


        Logger.info("login rt:" + rt);
        return rt;
    }


    public Optional<Boolean> addOrderRemark(String id, String remark) throws Exception {
        boolean rt;

        Logger.debug(String.format("add remark orderid=%s,remark=%s", id, remark));
        String sessionId = map.get("seesion_id");
        Logger.debug("http tianmaSport login sessionId: " + sessionId);


        HttpResponse<JsonNode> jsonResponse = Unirest.post(OrderCatConfig.getTradeOrderAddRemarkHttpUrl())
                .header("Host", OrderCatConfig.getTianmaSportHost())
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

        String url = OrderCatConfig.getTianmaSportVcHttpUrl();

        HttpResponse<InputStream> response =
                Unirest.get(String.format(url, millis))
                        .header("Host", OrderCatConfig.getTianmaSportHost())
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


    public void inventoryDownGroup(String fileName, String brandName,String sex, String quarter) throws Exception {
        Logger.info("inventory_down_group_http_url: " + OrderCatConfig.getTianmaSportIDGHttpUrl());
        Logger.info("brandName: " + brandName);
        Logger.info("quarter: " + quarter);
        Logger.info("sex: " + sex);

        String sessionId = map.get("seesion_id");
        Logger.info("http tianmaSport login sessionId: " + sessionId);
        HttpResponse<JsonNode> response = Unirest.post(OrderCatConfig.getTianmaSportIDGHttpUrl())
                .header("Host", OrderCatConfig.getTianmaSportHost())
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
                .field("sex", sex.equals("全部")?"":sex)
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
        }else {
            Logger.error(rt.getString("msg"));
        }
    }


    public String dataDownLoad(String path, String fileName) throws Exception {
        Logger.info("http data DownLoad:" + path);

        String dfileStr = OrderCatConfig.getOrderCatTempPath() + fileName;
        //IOUtils.toByteArray(inputStream);
        File dfile = new File(dfileStr);

        HttpResponse<InputStream> response =
                Unirest.get(String.format(OrderCatConfig.getTianmaSportDownLoadHttpUrl(), path))
                        .header("Host", OrderCatConfig.getTianmaSportHost())
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





    public List<InventoryInfo> getTmSportWhInfo(String Articleno) throws  Exception{

        Map<String,Object> rtMap = null;
        try{
            rtMap = getSearchByArticleno(Articleno);
        }catch (Exception e){
            Logger.error(e);
        }
        if(rtMap == null){
            List<InventoryInfo> list = new ArrayList<>();
            return list;
        }else {
            return (List<InventoryInfo>)rtMap.get("whlist");
        }
    }

    public Map<String,Object> getSearchByArticleno(String Articleno) throws Exception {


        Map<String,Object> rtMap = new HashMap<>();

        List<InventoryInfo> list = new ArrayList<>();
        List<com.alibaba.fastjson.JSONObject> jsonObjectList = new ArrayList<>();
        String jsonstr = "";
        Logger.info("getSearchByArticlenoHttpUrl: " + OrderCatConfig.getSearchByArticlenoHttpUrl());
        Logger.info("Articleno: " + Articleno);
        String sessionId = map.get("seesion_id");
        Logger.info("http tianmaSport login sessionId: " + sessionId);
        HttpResponse<String> response = Unirest.post(OrderCatConfig.getSearchByArticlenoHttpUrl())
                .header("Host", OrderCatConfig.getTianmaSportHost())
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
                throw new OCException(String.format("没有类似货号的商品[%s]",Articleno));
            }
            Document doc = Jsoup.parse(rt);
            Element script = doc.select("script").get(1);
            String data = script.data();
            //仓库信息
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
                inventoryInfo.setArticlenoOld(jsonObject.getString("articleno_old"));




                inventoryInfo.setUpdateTime(OcDateTimeUtils.string2LocalDateTime(jsonObject.getString("updateTime")));
                list.add(inventoryInfo);
                jsonObjectList.add(jsonObject);

            }
            rtMap.put("whlist",list);
            rtMap.put("jsonObjectList",jsonObjectList);

            //产品ID
            String productID;
            bstr = "'#tt";
            bstr_index = data.indexOf(bstr);
            estr_index = data.indexOf("').datagrid(", bstr_index);
            String productIDStr = data.substring(bstr_index + bstr.length(), estr_index);


            Logger.info(String.format("productID:%s",productIDStr));

            productID = productIDStr;
            rtMap.put("productID",productID);



//            return '<a href="javascript:toOrder('
//                    + v
//                    + ',\''
//                    + r.wareHouseName
//                    + '\',\'耐克\',\''+r.articleno+'\',\'鞋\',\'篮球鞋\',\'1.0\',\'949.0\','
//                    + r.discount + ',' + r.proxyPrice
//                    + ',\'' + encodeURI(this.size1) + '\',\'3\',\''
//                    + r.wareHouseID + '\',\''
//                    +  this.sku
//                    + '\',\''+encodeURI(this.size2)+'\',\'14Q4\',\''
//                    + r.articleno_old + '\');" title="点击下单">' + v + '</a>';


            //weight 商品重量
            String toOrderStr;
            bstr = "+r.articleno+'";
            bstr_index = data.indexOf(bstr);
            estr_index = data.indexOf("r.discount", bstr_index);
            toOrderStr = data.substring(bstr_index + bstr.length(), estr_index);
            Logger.info(String.format("toOrderStr:%s",toOrderStr));
            toOrderStr = toOrderStr.replaceAll("\\'","").replaceAll("\\\\","");
            Logger.info(String.format("toOrderStr:%s",toOrderStr));
            String weight = (toOrderStr.split(",")[3]);
            Logger.info(String.format("weight:%s",weight));

            String dalei = (toOrderStr.split(",")[1]);

            rtMap.put("weight",weight);
            rtMap.put("dalei",dalei);

            Logger.info(String.format("dalei:%s",dalei));

            //size_info
            bstr = "var size_info = '";
            bstr_index = data.indexOf(bstr);
            estr_index = data.indexOf("'.split(", bstr_index);
            String sizeinfoStr = data.substring(bstr_index + bstr.length(), estr_index);

            String[] sizeinfoStrArr = sizeinfoStr.split(",");
            String[] tmSizeArr;

            Map<String,TmSizeInfo> tmSizeInfoMap = new HashMap<>();
            TmSizeInfo tmSizeInfo;
            for(String sizeInfo :sizeinfoStrArr){
                tmSizeInfo = new TmSizeInfo();
                tmSizeArr = sizeInfo.split("<>",-1);

                if(dalei.equals("服")){
                    tmSizeInfo.setSize1(OcSizeUtils.getClothesConversionSize1(tmSizeArr[1]));
                }else {
                    tmSizeInfo.setSize1(tmSizeArr[1]);
                }

                tmSizeInfo.setSize2(tmSizeArr[0]);
                tmSizeInfo.setTmSukId(tmSizeArr[2]);
                //Logger.info(String.format("size1[%s],size2[%s],tmskuid[%s]",tmSizeInfo.getSize1(),tmSizeInfo.getSize2(),tmSizeInfo.getTmSukId()));
                tmSizeInfoMap.put(tmSizeInfo.getSize1(),tmSizeInfo);
            }
            rtMap.put("sizeInfo",tmSizeInfoMap);
            Logger.info(sizeinfoStr);



            //Logger.info("JSON-String:" + jsonstr);
        }
        return rtMap;
    }

    public List<TianmaOrder> tradeOrderDataList(String startTime, String endTime, TianmaOrderStatus orderStatus, String sort) throws Exception {
        List<TianmaOrder> rtlist = new ArrayList<>();
        int pageNo = 1;
        int pageSize = 300;

        PageResult<TianmaOrder> pageResult;
        do {
            pageResult = tradeOrderDataList(startTime, endTime, orderStatus,null, sort, pageNo, pageSize);
            rtlist.addAll(pageResult.getRows());
            Logger.debug("Math.ceil((double)pageResult.getTotal() / pageSize):" + Math.ceil((double) pageResult.getTotal() / pageSize));
        } while (Math.ceil((double) pageResult.getTotal() / pageSize) >= (++pageNo));
        return rtlist;
    }


    public PageResult<TianmaOrder> tradeOrderDataList(String startTime,
                                                      String endTime,
                                                      TianmaOrderStatus orderStatus,
                                                      String outerTid,
                                                      String sort,
                                                      Integer pageNo,
                                                      Integer pageSize) throws Exception {
        Logger.info("trade_orders_data_list_http_url: " + OrderCatConfig.getTradeOrdersDataListHttpUrl());
        Logger.info(String.format("startTime:%s endTime:%s order_status:%s",
                startTime == null ? "" : startTime,
                endTime == null ? "" : endTime,
                orderStatus == null ? "" : orderStatus.getVal()));

        PageResult<TianmaOrder> pr = new PageResult();


        List<TianmaOrder> orders = new ArrayList<>();

        String sessionId = map.get("seesion_id");
        Logger.info("http tianmaSport login sessionId: " + sessionId);
        HttpResponse<JsonNode> response = Unirest.post(OrderCatConfig.getTradeOrdersDataListHttpUrl())
                .header("Host", OrderCatConfig.getTianmaSportHost())
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
                .field("status", orderStatus == null ? "" : orderStatus.getVal())
                .field("m_warehouse_name", "")
                .field("goods_no", "")
                .field("names", "")
                .field("startTime", startTime == null ? "" : startTime)
                .field("endsTime", endTime == null ? "" : endTime)
                .field("size", "")
                .field("sort", sort == null ? "" : sort) //feed_back_time
                .field("order", "desc")
                .field("outer_tid", outerTid == null?"":outerTid)
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

                if (OcSizeUtils.getClothesConversionSize1(tianmaOrder.getSize1()).indexOf("error") > -1) {
                    tianmaOrder.setSize1(tianmaOrder.getSize1());
                } else {
                    tianmaOrder.setSize1(OcSizeUtils.getClothesConversionSize1(tianmaOrder.getSize1()));
                }


                tianmaOrder.setSize2(order.get("size2").toString());
                tianmaOrder.setPayPrice(order.getBigDecimal("pay_price"));
                tianmaOrder.setPostFee(order.getBigDecimal("post_fee"));
                tianmaOrder.setWarehouseId(order.getInt("m_warehouse_id"));
                tianmaOrder.setWarehouseName(order.getString("m_warehouse_name"));
                tianmaOrder.setStatus(TianmaOrderStatus.valueOf1(order.get("status").toString()));
                tianmaOrder.setGoodsNo(order.get("goods_no").toString());

                tianmaOrder.setTid(String.valueOf(order.getInt("tid")));


                orders.add(tianmaOrder);

            }
            pr.setRows(orders);
        } else {

            throw new OCException("获取天马反馈订单失败:" + code);
        }
        return pr;
    }


    public List<TmArea> getArea(String pid) throws Exception {
        Logger.info("http tm-sport getArea-pid:" + pid);

        List<TmArea> list = new ArrayList<>();
        HttpResponse<JsonNode> response = Unirest.get(String.format(OrderCatConfig.getTianmaGetAreaHttpUrl(), pid))
                .header("Host", OrderCatConfig.getTianmaSportHost())
                .header("Connection", "keep-alive")
                .header("Upgrade-Insecure-Requests", "1")
                .header("User-Agent", USER_AGENT)
                .header("Accept", "application/json, text/javascript, */*; q=0.01")
                .header("Referer", "http://www.tianmasport.com/ms/order/quickOrder.shtml")
                .header("Accept-Encoding", "gzip, deflate, sdch")
                .header("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4")
                .header("X-Requested-With", "XMLHttpRequest")
                .asJson();
        int code = response.getStatus();
        if (code == 200) {
            JSONObject rt = response.getBody().getObject();
            org.json.JSONArray rows = rt.getJSONArray("rows");
            TmArea tmArea;
            JSONObject area;
            for (int i = 0; i < rows.length(); i++) {
                area = rows.getJSONObject(i);
                tmArea = new TmArea();


                tmArea.setId(area.getLong("id"));
                tmArea.setFlag(area.getInt("flag"));
                tmArea.setLevel(area.getInt("level"));
                tmArea.setName(area.getString("name"));
                tmArea.setPpid(area.get("ppid").toString().equals("null")?0l:area.getLong("ppid"));
                tmArea.setPid(area.getLong("pid"));
                tmArea.setZipcode(area.getString("zipcode"));
                tmArea.setPpname(area.get("ppname").toString());

                list.add(tmArea);
            }
            if(list.size() == 0){
                throw new OCException("天马地址区域查询结果为0,pid:" + pid);
            }

        } else {
            throw new OCException("天马地址区域查询失败:" + code);
        }
        return list;
    }


    public List<TmPostage> getPostage(String wareHouseName,
                                   String province,
                                   String weight) throws Exception {
        Logger.info(String.format("http tm-sport getPostage:whname:%s,province:%s,weight:%s",
                wareHouseName,
                province,
                weight));

        List<TmPostage> list = new ArrayList<>();
        HttpResponse<JsonNode> response = Unirest.post(String.format("http://www.tianmasport.com/ms/order/getPostage.do"))
                .header("Host", OrderCatConfig.getTianmaSportHost())
                .header("Connection", "keep-alive")
                .header("Upgrade-Insecure-Requests", "1")
                .header("User-Agent", USER_AGENT)
                .header("Accept", "application/json, text/javascript, */*; q=0.01")
                .header("Referer", "http://www.tianmasport.com/ms/order/quickOrder.shtml")
                .header("Accept-Encoding", "gzip, deflate, sdch")
                .header("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4")
                .header("X-Requested-With", "XMLHttpRequest")
                .field("wareHouseName",wareHouseName)
                .field("province",province)
                .field("weight",weight)
                .asJson();

        int code = response.getStatus();
        if (code == 200) {

            org.json.JSONArray rows = response.getBody().getArray();
            TmPostage tmPostage;
            JSONObject area;
            for (int i = 0; i < rows.length(); i++) {
                area = rows.getJSONObject(i);
                tmPostage = new TmPostage();
                tmPostage.setWareHouseName(area.getString("wareHouseName"));
                tmPostage.setProvince(area.getString("province"));
                tmPostage.setExpressName(area.getString("expressName"));
                String data = tmPostage.getExpressName();
                String bstr = "(";
                int bstr_index = data.indexOf(bstr);
                int estr_index = data.indexOf(")", bstr_index);
                String kdCost = data.substring(bstr_index + bstr.length(), estr_index);
                tmPostage.setKdCost(new BigDecimal(kdCost));
                list.add(tmPostage);
            }
            if(list.size() == 0){
                throw new OCException(String.format("天马快递公司查询结果为0!getPostage:whname:%s,province:%s,weight:%s",
                        wareHouseName,
                        province,
                        weight));
            }

        } else {
            throw new OCException("天马地址区域查询失败:" + code);
        }
        return list;
    }




    public String orderBooking(Map<String, String> requestMap) throws Exception {
        Logger.info(String.format("http tm-sport orderBooking"));
        requestMap.entrySet().stream().forEach(o->{
            Logger.info(String.format("%s=%s",o.getKey(),o.getValue()));
        });
        String rt;
        HttpResponse<JsonNode> response = Unirest.post(String.format("http://www.tianmasport.com/ms/order/booking.do"))
                .header("Host", OrderCatConfig.getTianmaSportHost())
                .header("Connection", "keep-alive")
                .header("Upgrade-Insecure-Requests", "1")
                .header("User-Agent", USER_AGENT)
                .header("Accept", "application/json, text/javascript, */*; q=0.01")
                .header("Referer", "http://www.tianmasport.com/ms/order/quickOrder.shtml")
                .header("Accept-Encoding", "gzip, deflate, sdch")
                .header("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4")
                .header("X-Requested-With", "XMLHttpRequest")
                .field("area", requestMap.get("area"))
                .field("city", requestMap.get("city"))
                .field("recv_name", requestMap.get("recv_name"))
                .field("jsonStr", requestMap.get("jsonStr"))
                .field("remark", "")
                .field("area_id", requestMap.get("area_id"))
                .field("zipcode", requestMap.get("zipcode"))
                .field("recv_tel", requestMap.get("recv_tel"))
                .field("recv_address", requestMap.get("recv_address"))
                .field("province", requestMap.get("province"))
                .field("province_id", requestMap.get("province_id"))
                .field("recv_mobile", requestMap.get("recv_mobile"))
                .field("outer_tid", requestMap.get("outer_tid"))
                .field("city_id", requestMap.get("city_id"))
                .asJson();

        int code = response.getStatus();
        if (code == 200) {
            JSONObject object = response.getBody().getObject();
            if(object.getBoolean("success") == true){
                rt = object.getString("msg");
                Logger.info("msg:"+rt);
            }else {
                throw new OCException("orderBooking 查询失败:" + object.getString("msg"));
            }
        } else {
            throw new OCException("orderBooking:" + code+",text:"+response.getStatusText());
        }
        return rt;
    }


    //

    /**
     * 付款
     * http://www.tianmasport.com/ms/tradeInfo/updataBalance.do
     * @param orderId
     * @param payPwd
     * @return
     * @throws Exception
     */
    public String updataBalance(String orderId,String payPwd) throws Exception {

        Logger.info(String.format("http tm-sport updataBalance:orderId:%s,payPwd:********,",
                orderId));

        String rt;
        HttpResponse<JsonNode> response = Unirest.post(String.format("http://www.tianmasport.com/ms/tradeInfo/updataBalance.do"))
                .header("Host", OrderCatConfig.getTianmaSportHost())
                .header("Connection", "keep-alive")
                .header("Upgrade-Insecure-Requests", "1")
                .header("User-Agent", USER_AGENT)
                .header("Accept", "application/json, text/javascript, */*; q=0.01")
                .header("Referer", "http://www.tianmasport.com/ms/tradeOrders/myorder_list.shtml")
                .header("Accept-Encoding", "gzip, deflate, sdch")
                .header("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4")
                .header("X-Requested-With", "XMLHttpRequest")
                .field("orderIDs",orderId)
                .field("payPwd",payPwd)
                .asJson();

        int code = response.getStatus();
        if (code == 200) {
            JSONObject object = response.getBody().getObject();
            if(object.getBoolean("success") == true){
                rt = object.getString("msg");
                Logger.info(rt);
            }else {
                throw new OCException("updataBalance 查询失败:" + object.getString("msg"));
            }
        } else {
            throw new OCException("updataBalance:" + code);
        }
        return rt;
    }



    //http://www.tianmasport.com/ms/tradeInfo/mergePostage.do

    /**
     * 合并订单
     * @param orderId
     * @return
     * @throws Exception
     */
    public String mergePostage(String orderId) throws Exception {

        Logger.info(String.format("http tm-sport mergePostage:orderId:%s",
                orderId));

        String rt;
        HttpResponse<JsonNode> response = Unirest.post(String.format("http://www.tianmasport.com/ms/tradeInfo/mergePostage.do"))
                .header("Host", OrderCatConfig.getTianmaSportHost())
                .header("Connection", "keep-alive")
                .header("Upgrade-Insecure-Requests", "1")
                .header("User-Agent", USER_AGENT)
                .header("Accept", "application/json, text/javascript, */*; q=0.01")
                .header("Referer", "http://www.tianmasport.com/ms/tradeOrders/myorder_list.shtml")
                .header("Accept-Encoding", "gzip, deflate, sdch")
                .header("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4")
                .header("X-Requested-With", "XMLHttpRequest")
                .field("orderIDs",orderId)
                .asJson();

        int code = response.getStatus();
        if (code == 200) {
            JSONObject object = response.getBody().getObject();
            if(object.getBoolean("success") == true){

                //{"easyuiUrl":"http://www.tianmasport.com/ms/js/jquery-easyui","balance":207754.211,"msUrl":"http://www.tianmasport.com/ms","weight":12,"success":true,"totalPrice":"398.850","postage":"12.000"}


                Logger.info(String.format("balance=%s",object.getBigDecimal("balance").toPlainString()));
                Logger.info(String.format("totalPrice=%s",object.getBigDecimal("totalPrice").toPlainString()));
                Logger.info(String.format("postage=%s",object.getBigDecimal("postage").toPlainString()));
                Logger.info(String.format("weight=%s",String.valueOf(object.getInt("weight"))));


                rt = "success";

                Logger.info(rt);
            }else {
                throw new OCException("mergePostage 查询失败:" + object.getString("msg"));
            }
        } else {
            throw new OCException("mergePostage:" + code);
        }
        return rt;
    }



    public String getdefaultPostage(String wareHouseName,
                                      String province,
                                      String weight) throws Exception {
        Logger.info(String.format("http tm-sport getPostage:whname:%s,province:%s,weight:%s",
                wareHouseName,
                province,
                weight));

        String rt = null;
        HttpResponse<JsonNode> response = Unirest.post(String.format("http://www.tianmasport.com/ms/order/defaultPostage.do"))
                .header("Host", OrderCatConfig.getTianmaSportHost())
                .header("Connection", "keep-alive")
                .header("Upgrade-Insecure-Requests", "1")
                .header("User-Agent", USER_AGENT)
                .header("Accept", "application/json, text/javascript, */*; q=0.01")
                .header("Referer", "http://www.tianmasport.com/ms/order/quickOrder.shtml")
                .header("Accept-Encoding", "gzip, deflate, sdch")
                .header("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4")
                .header("X-Requested-With", "XMLHttpRequest")
                .field("wareHouseName",wareHouseName)
                .field("province",province)
                .field("weight",weight)
                .asJson();



        int code = response.getStatus();
        if (code == 200) {

            JSONObject object = response.getBody().getObject();

            if(object.getBoolean("success") == true){
                rt = object.getString("msg");
            }else {
                throw new OCException("getdefaultPostage 查询失败:" + object.getString("msg"));
            }


        } else {
            throw new OCException("getdefaultPostage:" + code);
        }
        return rt;
    }





    public Optional<LogisticsCompany> ajaxGuessMailNoRequest(String mailNo, String tradeId) throws Exception {
        //https://wuliu.taobao.com/user/ajax_guess_mail_no.do?code=utf-8&mailNo=3921971273918
        LogisticsCompany logisticsCompany = null;

        String requestJsonFile = OrderCatConfig.getOrderCatTempPath() + "ajax_guess_mail_no_request.json";
        String requestJsonStr = FileUtils.readFileToString(new File(requestJsonFile), "UTF-8");
        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(requestJsonStr);
        GetRequest getRequest = Unirest.get(String.format("https://wuliu.taobao.com/user/ajax_guess_mail_no.do?code=utf-8&mailNo=%s", mailNo));
        getRequest = getRequest.header("Host", "wuliu.taobao.com");
        getRequest = getRequest.header("Connection", "keep-alive");
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

        getRequest = getRequest.header("Accept", jsonObject.getString("Accept"));
        getRequest = getRequest.header("X-Requested-With", jsonObject.getString("X-Requested-With"));
        getRequest = getRequest.header("User-Agent", jsonObject.getString("User-Agent"));
        getRequest = getRequest.header("Referer", String.format("https://wuliu.taobao.com/user/consign.htm?trade_id=%s", tradeId));
        getRequest = getRequest.header("Accept-Encoding", jsonObject.getString("Accept-Encoding"));
        getRequest = getRequest.header("Accept-Language", jsonObject.getString("Accept-Language"));
        getRequest = getRequest.header("Cookie", jsonObject.getString("Cookie"));

        HttpResponse<JsonNode> response = getRequest.asJson();

        if (response.getStatus() == 200) {
            JSONObject jsonObject1 = response.getBody().getObject();
            boolean success = jsonObject1.getBoolean("success");
            if (success) {
                org.json.JSONArray datas = jsonObject1.getJSONArray("data");
                for (int i = 0; i < datas.length(); i++) {
                    logisticsCompany = new LogisticsCompany();
                    jsonObject1 = datas.getJSONObject(i);
                    logisticsCompany.setCode(jsonObject1.getString("cpCode"));
                    logisticsCompany.setName(jsonObject1.getString("cpName"));
                    break;
                }
            } else {
                throw new OCException("ajaxGuessMailNoRequest 请求失败");
            }
        }
        Logger.debug("ajaxGuessMailNoRequest--response-str:" + response.getBody());
        return Optional.ofNullable(logisticsCompany);
    }


}
