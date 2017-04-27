package com.myjo.ordercat.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.myjo.ordercat.domain.ItemsOnSale;
import com.myjo.ordercat.domain.PageResult;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.Sku;
import com.taobao.api.request.ItemSkusGetRequest;
import com.taobao.api.request.ItemsOnsaleGetRequest;
import com.taobao.api.request.SellercatsListGetRequest;
import com.taobao.api.response.ItemSkusGetResponse;
import com.taobao.api.response.ItemsOnsaleGetResponse;
import com.taobao.api.response.SellercatsListGetResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by lee5hx on 17/4/24.
 */
public class TaoBaoHttp {

    private static final Logger Logger = LogManager.getLogger(TianmaSportHttp.class);
    private static final String URL = "http://gw.api.taobao.com/router/rest";
    //private static final String URL = "http://gw.api.tbsandbox.com/router/rest";
    private static final String APP_KEY = "23279400";
    private static final String APP_SECRET = "981280b8b5b1bd8d196d1373c83427ec";
    private static final String SESSION_KEY = "610061860172d8cb9f255a51961f5d4885daaff6a5bad72738840638";
    private static final Long CID = Long.parseLong("1282880675");


    public TaoBaoHttp() {

    }

    public static void main(String agrs[]) {
        System.out.println(40 % 40);
    }

    public void itemcatsGetRequest() throws Exception {

        TaobaoClient client = new DefaultTaobaoClient(URL, APP_KEY, APP_SECRET);
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
    public PageResult<ItemsOnSale> getTaobaoItemsOnSale(long pageNo, long pageSize) throws Exception {


        PageResult<ItemsOnSale> pageResult = new PageResult<>();
        Logger.debug("getTaobaoItemsOnSale:" + CID);
        List<ItemsOnSale> list = new ArrayList<>();
        TaobaoClient client = new DefaultTaobaoClient(URL, APP_KEY, APP_SECRET);
        ItemsOnsaleGetRequest req = new ItemsOnsaleGetRequest();
        req.setFields("num_iid");
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

        req.setIsCspu(true);
//        req.setIsCombine(true);
        ItemsOnsaleGetResponse rsp = client.execute(req, SESSION_KEY);


        JSONObject object = JSON.parseObject(rsp.getBody());

        JSONObject items_onsale_get_response = object.getJSONObject("items_onsale_get_response");
        JSONObject items = items_onsale_get_response.getJSONObject("items");
        JSONArray itemArray = items.getJSONArray("item");



        JSONObject item;
        ItemsOnSale itemsOnSale;
        for (int i = 0; i < itemArray.size(); i++) {
            itemsOnSale = new ItemsOnSale();
            item = itemArray.getJSONObject(i);
            itemsOnSale.setNumIid(item.getLong("num_iid"));
            list.add(itemsOnSale);
        }
        pageResult.setRows(list);
        pageResult.setTotal(items_onsale_get_response.getInteger("total_results"));

        Logger.debug("getTaobaoItemsOnSale.pageResult.getRows().size:" + pageResult.getRows().size());
        Logger.debug("getTaobaoItemsOnSale.pageResult.getTotal():" + pageResult.getTotal());
        return pageResult;
    }


    //taobao.item.skus.get (根据商品ID列表获取SKU信息)

    public List<ItemsOnSale> getTaobaoItemsOnSale() throws Exception {
        long pageNo = 1l;
        long pageSize = 100l;
        List<ItemsOnSale> rtlist = new ArrayList<>();

        PageResult<ItemsOnSale> pageResult;
        do {
            pageResult = getTaobaoItemsOnSale(pageNo, pageSize);
            rtlist.addAll(pageResult.getRows());
            Logger.debug("Math.ceil((double)pageResult.getTotal() / pageSize):" + Math.ceil((double)pageResult.getTotal() / pageSize));
            //++pageNo;
        } while (Math.ceil((double)pageResult.getTotal() / pageSize) >= ( ++pageNo));

        return rtlist;
    }

    /**
     * taobao.item.skus.get (根据商品ID列表获取SKU信息)
     *
     * @param list
     * @return
     */
    public List<Sku> getTaoBaoItemSkus(List<ItemsOnSale> list) throws Exception {
        Logger.debug("getTaoBaoItemSkus:" + CID);
        List<Sku> rtlist = new ArrayList<>();
        int[] indexes =
                Stream.of(IntStream.range(-1, list.size())
                        .filter(i -> i % 40 == 0), IntStream.of(list.size()))
                        .flatMapToInt(s -> s).toArray();
        List<List<ItemsOnSale>> subLists =
                IntStream.range(0, indexes.length - 1)
                        .mapToObj(i -> list.subList(indexes[i], indexes[i + 1]))
                        .collect(Collectors.toList());

        for(List<ItemsOnSale> list1 :subLists){
            rtlist.addAll(taoBaoItemSkus(list1));
        }
        //list.stream().limit()
        Logger.debug("getTaoBaoItemSkus.rt.size:" + rtlist.size());
        return rtlist;
    }

    private List<Sku> taoBaoItemSkus(List<ItemsOnSale> list) throws Exception {
        List<Sku> rtlist = new ArrayList<>();

        TaobaoClient client = new DefaultTaobaoClient(URL, APP_KEY, APP_SECRET);
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
        ItemSkusGetResponse rsp = client.execute(req, SESSION_KEY);
        if(rsp.isSuccess() == true){
            rtlist.addAll(rsp.getSkus());
        }

        return rtlist;
    }
}
