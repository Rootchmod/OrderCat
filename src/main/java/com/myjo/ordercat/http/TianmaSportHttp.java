package com.myjo.ordercat.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.myjo.ordercat.domain.InventoryInfo;
import com.myjo.ordercat.config.OrderCatConfig;
import com.myjo.ordercat.domain.PickDate;
import com.myjo.ordercat.utils.OcDateTimeUtils;
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


    public String getVerifyCodeImage() throws Exception {
        Logger.info("http download verify code image");


        DateTime dt = new DateTime();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String dtStr = dt.toString(fmt);
        long millis = dt.getMillis();
        String vcfile = OrderCatConfig.getTianmaSportVcImageFileName();
        //IOUtils.toByteArray(inputStream);
        File vfile = new File(OrderCatConfig.getOrderCatOutPutPath()+vcfile);

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

    public void inventoryDownGroup(String fileName,String brandName, String quarter) throws Exception {
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
        if(rt.getBoolean("success") == true){
            String path = rt.getString("path");
            Logger.info("inventoryDownGroup return path:" + path);
            dataDownLoad(path,fileName);
        }
    }


    public String dataDownLoad(String path,String fileName) throws Exception {
        Logger.info("http data DownLoad:"+path);

        String dfileStr = OrderCatConfig.getOrderCatOutPutPath()+fileName;
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
        FileUtils.writeByteArrayToFile(dfile, IOUtils.toByteArray(response.getBody()),true);

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

        if(code == 200){
            String rt = response.getBody();
            if(rt.indexOf("没有类似货号的商品!")>-1){
                return list;
            }
            Document doc = Jsoup.parse(rt);
            Element script = doc.select("script").get(1);
            String data = script.data();
            String bstr = "var data = $.parseJSON('";
            int  bstr_index  = data.indexOf(bstr);
            int  estr_index  = data.indexOf("');",bstr_index);
            jsonstr = data.substring(bstr_index+bstr.length(),estr_index);
            com.alibaba.fastjson.JSONObject object = JSON.parseObject(jsonstr);
            JSONArray array = object.getJSONArray("rows");
            com.alibaba.fastjson.JSONObject jsonObject;
            InventoryInfo inventoryInfo;

            String dd1 ; //配货率
            String dd2 ; //发货时效
            for(int i=0;i<array.size();i++){

                jsonObject = array.getJSONObject(i);
                dd1 = StringUtils.substringBeforeLast(jsonObject.getString("pickRate"),"%");
                dd2 = StringUtils.substringAfterLast(jsonObject.getString("pickRate"),"发货时效:");

                inventoryInfo = new InventoryInfo();
                inventoryInfo.setWareHouseID(Integer.valueOf(jsonObject.getString("wareHouseID")));
                inventoryInfo.setWarehouseName(jsonObject.getString("wareHouseName"));
                inventoryInfo.setPickRate(Integer.valueOf(dd1.replaceAll("配货率：","")));
                inventoryInfo.setThedtime(dd2.replaceAll("小时",""));
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




}
