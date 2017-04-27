package com.myjo.ordercat.handle;

import com.myjo.ordercat.config.OrderCatConfig;
import com.myjo.ordercat.domain.*;
import com.myjo.ordercat.exception.OCException;
import com.myjo.ordercat.http.TaoBaoHttp;
import com.myjo.ordercat.http.TianmaSportHttp;
import com.myjo.ordercat.spm.OrdercatApplication;
import com.myjo.ordercat.spm.OrdercatApplicationBuilder;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_warehouse_info.OcWarehouseInfo;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_warehouse_info.OcWarehouseInfoImpl;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_warehouse_info.OcWarehouseInfoManager;
import com.myjo.ordercat.utils.OcDateTimeUtils;
import com.myjo.ordercat.utils.SizeUtils;
import com.taobao.api.domain.Sku;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by lee5hx on 17/4/24.
 */
public class SyncInventory {


    private static final Logger Logger = LogManager.getLogger(SyncInventory.class);

    private static final int FAILURE_PICKRATE_COUNT = 10;

    private static final int PICK_RATE_LESS_THAN_DEL_LIMIT = 50; //配货率小于多少删除

    //private static OrderCatConfig context;
    private TianmaSportHttp tianmaSportHttp;

    private TaoBaoHttp taoBaoHttp;

    private OcWarehouseInfoManager ocWarehouseInfoManager;

    public SyncInventory(TianmaSportHttp tianmaSportHttp, TaoBaoHttp taoBaoHttp) {


        OrdercatApplication app = new OrdercatApplicationBuilder()
                .withConnectionUrl("ordercat", "jdbc:mysql://localhost:50012")
                .withUsername("root")
                .withPassword("123456")
                .build();

        ocWarehouseInfoManager = app.getOrThrow(OcWarehouseInfoManager.class);

        this.tianmaSportHttp = tianmaSportHttp;
        this.taoBaoHttp = taoBaoHttp;
    }


    /**
     * 数据采集,多个CSV合成一个
     */
    public void dataGathering(String fileName) throws Exception {
        //删除历史库存CSV
//        String dfileStr = OrderCatConfig.getOrderCatOutPutPath() + fileName;
//        File dfile = new File(dfileStr);
//        FileUtils.forceDeleteOnExit(dfile);
//        Logger.debug("if exists:" + dfile.exists() + " onExit del:" + dfileStr);

        //下载合并CSV
        List<InventoryQueryCondition> list = OrderCatConfig.getInventoryQueryConditions();
        Logger.debug("dataGathering.InventoryQueryCondition.list" + list.size());
        for (InventoryQueryCondition iqc : list) {
            Logger.debug("dataGathering.InventoryQueryCondition.BrandName" + iqc.getBrandName());
            Logger.debug("dataGathering.InventoryQueryCondition.Quarter" + iqc.getQuarter());
            tianmaSportHttp.inventoryDownGroup(fileName, iqc.getBrandName(), iqc.getQuarter());
        }
        Logger.debug("SyncInventory.dataGathering.exec done.");
    }

    private List<InventoryInfo> getInventoryInfoInCsv(String fileName) throws Exception {

        List<InventoryInfo> list = new ArrayList<>();
        String dfileStr = OrderCatConfig.getOrderCatOutPutPath() + fileName;
        ICsvListReader listReader = null;
        try {


            InputStreamReader freader = new InputStreamReader(new FileInputStream(
                    new File(dfileStr)), "GBK");

            listReader = new CsvListReader(freader, CsvPreference.STANDARD_PREFERENCE);
            listReader.getHeader(true); // skip the header (can't be used with CsvListReader)
            //final CellProcessor[] processors = getProcessors();
            List<String> customerList;
            InventoryInfo inventoryInfo = null;
            while ((customerList = listReader.read()) != null) {
                inventoryInfo = new InventoryInfo();
//                Logger.debug(String.format("lineNo=%s, rowNo=%s, customerList=%s", listReader.getLineNumber(),
//                        listReader.getRowNumber(), customerList));
                inventoryInfo.setGoodsNo(customerList.get(0));
                inventoryInfo.setWarehouseName(customerList.get(1));
                inventoryInfo.setSize1(customerList.get(2));
                inventoryInfo.setSize2(customerList.get(3));

                inventoryInfo.setBrand(Brand.NIKE);
                inventoryInfo.setMarketprice(customerList.get(5));
                inventoryInfo.setNum2(customerList.get(6));
                inventoryInfo.setDivision(customerList.get(7));
                inventoryInfo.setCate(customerList.get(8));
                if (customerList.get(9) == null) {
                    inventoryInfo.setSex(null);
                } else {
                    if (customerList.get(9).equals("男")) {
                        inventoryInfo.setSex(Sex.MALE);
                    } else {
                        inventoryInfo.setSex(Sex.FEMALE);
                    }
                }
                inventoryInfo.setQuarter(customerList.get(10));
                inventoryInfo.setDiscount(customerList.get(11));
                list.add(inventoryInfo);
            }

        } finally {
            if (listReader != null) {
                listReader.close();
            }
        }
        return list;
    }

    /**
     * 同步仓库信息
     */
    public void syncWarehouseInfo() throws Exception {

        int execJobId = 1;

//        hares.stream()
//                .filter(Hare.ID.equal(71))  // Filters out all Hares with ID = 71 (just one)
//                .forEach(hares.remover());

        ocWarehouseInfoManager.stream()
                .filter(OcWarehouseInfo.EXEC_JOB_ID.equal(execJobId))
                .forEach(ocWarehouseInfoManager.remover());

        Logger.info("同步配货率信息,job-id:" + execJobId);

//        File inventoryGroupWhfile = new File(OrderCatConfig.getOrderCatOutPutPath() + OrderCatConfig.getInventoryGroupWhfile());
//        if(inventoryGroupWhfile.exists()){
//            FileUtils.forceDelete(inventoryGroupWhfile);
//        }
//
//
//        //抓取天马库存信息数据
//        Logger.info("抓取天马库存信息数据");
//        dataGathering(OrderCatConfig.getInventoryGroupWhfile());
        List<InventoryInfo> list = getInventoryInfoInCsv(OrderCatConfig.getInventoryGroupWhfile());
        Logger.info("InventoryInfoInCsv.origin.size:" + list.size());

        //库存信息文件中根据仓库名称进行去重

        List<InventoryInfo> distinctWarehouseList = list.parallelStream()
                .filter(inventoryInfo -> !inventoryInfo.getDiscount().equals("折扣"))
                //.filter(InventoryInfo.distinctByField(inventoryInfo -> inventoryInfo.getGoodsNo()))
                .filter(InventoryInfo.distinctByField(inventoryInfo -> inventoryInfo.getWarehouseName()))
                .collect(Collectors.toList());


        int distinctWarehouseList_size = distinctWarehouseList.size();
        Logger.info("distinctWarehouseList.size:" + distinctWarehouseList_size);


        //库存分组信息
        Map<String, List<InventoryInfo>> inventoryInfoInCsvMap =
                Stream.concat(list.parallelStream(), distinctWarehouseList.parallelStream())
                        .collect(Collectors.groupingBy(InventoryInfo::getWarehouseName));

//        distinctWarehouseList.parallelStream().forEach(inventoryInfo ->
//                Logger.info(inventoryInfo.getGoodsNo()+":"+inventoryInfo.getWarehouseName()));

        //匹配配合率
        List<InventoryInfo> pickRateList = new ArrayList<>();
        InventoryInfo inventoryInfo;
        for (int i = 0; i < distinctWarehouseList.size(); i++) {
            inventoryInfo = distinctWarehouseList.get(i);
            Logger.info(inventoryInfo.getGoodsNo() + ":" + inventoryInfo.getWarehouseName() + "--" + (i + 1) + "/" + distinctWarehouseList.size());
            pickRateList.addAll(tianmaSportHttp.getSearchByArticleno(inventoryInfo.getGoodsNo()));

        }
        List<InventoryInfo> distinctPickRateList = pickRateList.parallelStream()
                .filter(InventoryInfo.distinctByField(inventoryInfo1 -> inventoryInfo1.getWareHouseID()))
                .collect(Collectors.toList());

        Logger.info("pickRateList.size:" + pickRateList.size());
        Logger.info("匹配成功,配合率信息:" + distinctPickRateList.size());


        //没有匹配到配合率的货源
        List<InventoryInfo> noRateList = new ArrayList<>();
        Map<String, List<InventoryInfo>> map =
                Stream.concat(distinctPickRateList.parallelStream(), distinctWarehouseList.parallelStream())
                        .collect(Collectors.groupingBy(InventoryInfo::getWarehouseName));
//                .filter(InventoryInfo.distinctByField(inventoryInfo2 -> inventoryInfo2.getWarehouseName()))
//                .collect(Collectors.toList());

        InventoryInfo noRate;
        for (Map.Entry<String, List<InventoryInfo>> entry : map.entrySet()) {
            if (entry.getValue().size() == 1) {
                noRate = entry.getValue().get(0);
                noRateList.add(noRate);
            }
        }
        Logger.info("未匹配成功,配合率信息:" + noRateList.size());

        //继续匹配
        Logger.info("继续匹配,未成功配合率信息:" + noRateList.size());
        List<InventoryInfo> inventoryInfos;
        List<InventoryInfo> for_inventoryInfos;
        for (int i = 0; i < noRateList.size(); i++) {
            inventoryInfo = noRateList.get(i);
            inventoryInfos = inventoryInfoInCsvMap.get(inventoryInfo.getWarehouseName())
                    .parallelStream()
                    .filter(InventoryInfo.distinctByField(inventoryInfo1 -> inventoryInfo1.getGoodsNo())).collect(Collectors.toList());
            for (int j = 0; j < inventoryInfos.size(); j++) {
                if (j == FAILURE_PICKRATE_COUNT) {//只检查10次
                    break;
                }
                inventoryInfo = inventoryInfos.get(j);
                Logger.info(inventoryInfo.getGoodsNo() + ":" + inventoryInfo.getWarehouseName() + "--" + (j + 1) + "/" + inventoryInfos.size());
                for_inventoryInfos = tianmaSportHttp.getSearchByArticleno(inventoryInfo.getGoodsNo());
                for (InventoryInfo info : for_inventoryInfos) {
                    if (info.getWarehouseName().equals(inventoryInfo.getWarehouseName())) {
                        pickRateList.addAll(for_inventoryInfos);
                        Logger.info("已经找到[%s]的配货率信息!", inventoryInfo.getWarehouseName());

                        j = inventoryInfos.size();
                        break;
                    }
                }


            }
        }
        distinctPickRateList = pickRateList.parallelStream()
                .filter(InventoryInfo.distinctByField(inventoryInfo1 -> inventoryInfo1.getWareHouseID()))
                .collect(Collectors.toList());


        Logger.info("pickRateList.size:" + pickRateList.size());


        distinctPickRateList.parallelStream().forEach(inventoryInfo1 ->
                Logger.info(inventoryInfo1.getWareHouseID() + "|" + inventoryInfo1.getWarehouseName() + "|" + inventoryInfo1.getPickRate() + "|" + inventoryInfo1.getThedtime() + "|" + inventoryInfo1.getUpdateTime()));


        noRateList.parallelStream().forEach(inventoryInfo1 ->
                Logger.info(inventoryInfo1.getWareHouseID() + "|" + inventoryInfo1.getWarehouseName() + "|" + inventoryInfo1.getPickRate() + "|" + inventoryInfo1.getThedtime() + "|" + inventoryInfo1.getUpdateTime()));


        OcWarehouseInfoImpl ocWarehouseInfo;

        for (InventoryInfo i1 : distinctPickRateList) {
            ocWarehouseInfo = new OcWarehouseInfoImpl();
            ocWarehouseInfo.setAddTime(LocalDateTime.now());
            ocWarehouseInfo.setWarehouseName(i1.getWarehouseName());
            ocWarehouseInfo.setWarehouseId(Integer.valueOf(i1.getWareHouseID()));
            ocWarehouseInfo.setPickRate(Integer.valueOf(i1.getPickRate()));
            ocWarehouseInfo.setThedTime(Integer.valueOf(i1.getThedtime()));
            ocWarehouseInfo.setUdpateWarehouseTime(i1.getUpdateTime());
            ocWarehouseInfo.setExecJobId(execJobId);
            ocWarehouseInfo.setEndT(i1.getEndT());
            ocWarehouseInfo.setMark(i1.getMark());
            ocWarehouseInfo.setPickDate(i1.getPickDate().getValue());
            ocWarehouseInfo.setRetrunDesc(i1.getRetrunDesc());
            ocWarehouseInfo.setReturnRate(Integer.valueOf(i1.getReturnRate()));
            ocWarehouseInfo.setExpressName(i1.getExpressName());


            // ocWarehouseInfo.setUdpateWarehouseTime(LocalDateTime.ofEpochSecond())

            ocWarehouseInfoManager.persist(ocWarehouseInfo);

        }


        for (InventoryInfo i1 : noRateList) {
            ocWarehouseInfo = new OcWarehouseInfoImpl();
            ocWarehouseInfo.setAddTime(LocalDateTime.now());
            ocWarehouseInfo.setWarehouseName(i1.getWarehouseName());
            ocWarehouseInfo.setWarehouseId(null);
            ocWarehouseInfo.setPickRate(null);
            ocWarehouseInfo.setThedTime(null);
            ocWarehouseInfo.setUdpateWarehouseTime(null);
            ocWarehouseInfo.setExecJobId(execJobId);

            ocWarehouseInfo.setEndT(null);
            ocWarehouseInfo.setMark(null);
            ocWarehouseInfo.setRetrunDesc(null);
            ocWarehouseInfo.setReturnRate(null);
            ocWarehouseInfo.setExpressName(null);

            ocWarehouseInfoManager.persist(ocWarehouseInfo);
        }


        //ocWarehouseInfo.setAddTime();


        // OcWarehouseInfo ocWarehouseInfo = new O


        Logger.info(String.format("匹配完成,总仓库条数:[%d],匹配成功:[%d],未匹配成功:[%d] :", distinctWarehouseList_size, distinctPickRateList.size(), distinctWarehouseList_size - distinctPickRateList.size()));


//        Map<String, List<String>> map2 = list.stream()
//                .collect(
//                        Collectors.groupingBy(
//                                InventoryInfo::getWarehouseName,
//                                Collectors.mapping(InventoryInfo::getBrandName,
//                                        Collectors.toList())));
        //System.out.println(map2);
    }

    public void syncTaoBaoInventory() throws Exception {
        Logger.info("同步淘宝库存");
        //抓取天马库存信息数据
        Logger.info("抓取天马库存信息数据");
        //dataGathering(OrderCatConfig.getInventoryGroupIwhfile());
        List<InventoryInfo> list = getInventoryInfoInCsv(OrderCatConfig.getInventoryGroupIwhfile());

        if (list.size() == 0) {
            throw new OCException("天马库存信息为空,请检测天马数据获取接口!");
        }

        Logger.info("InventoryInfoInCsv.origin.size:" + list.size());

        //获取Taobao店铺SKU-list
        Logger.info("获取Taobao店铺SKU-list");

        List<ItemsOnSale> itemsOnSaleList = taoBaoHttp.getTaobaoItemsOnSale();
        List<Sku> skus = taoBaoHttp.getTaoBaoItemSkus(itemsOnSaleList);

        if (skus.size() == 0) {
            throw new OCException("获取Taobao店铺SKU信息为空");
        }

        Logger.info("获取Taobao店铺SKU-list.size:" + skus.size());

        //按照SKU,在天马库存中进行过滤
        Logger.info("按照SKU,在天马库存中进行过滤");
        Map<String, List<Sku>> inventoryInfoInCsvMap =
                skus.parallelStream()
                        .collect(Collectors.groupingBy(sku -> StringUtils.substringBeforeLast(sku.getOuterId(), "-")));

        List<InventoryInfo> intersectionList = list.parallelStream()
                //.filter(InventoryInfo.distinctByField(inventoryInfo -> inventoryInfo.getGoodsNo()))
                .filter(InventoryInfo.distinctBySkusMap(inventoryInfo -> inventoryInfo.getGoodsNo(), inventoryInfoInCsvMap))
                .collect(Collectors.toList());
        Logger.info("按照SKU,在天马库存中进行过滤后的条数:" + intersectionList.size());

        // 对库存信息进行配货率匹配  lee5hx

        Logger.info("对库存信息进行配货率匹配");

        int execJobId = 1; // todo 这里需要获取最新执行ID
        //查询仓库信息
        Map<String, OcWarehouseInfo> warehouseMap = ocWarehouseInfoManager.stream()
                .filter(OcWarehouseInfo.EXEC_JOB_ID.equal(execJobId)
                        .and(OcWarehouseInfo.WAREHOUSE_ID.isNotNull()))
                .collect(
                        Collectors.toMap(o -> o.getWarehouseName(),
                                Function.identity())
                );
        Logger.info("仓库记录数-warehouseMap.size:"+warehouseMap.size());


        intersectionList.parallelStream().forEach(inventoryInfo -> {

            OcWarehouseInfo ocWarehouseInfo = warehouseMap.get(inventoryInfo.getWarehouseName());
            if(ocWarehouseInfo!=null){
                inventoryInfo.setReturnRate(ocWarehouseInfo.getReturnRate().getAsInt());
                inventoryInfo.setUpdateTime(ocWarehouseInfo.getUdpateWarehouseTime().get());
                inventoryInfo.setExpressName(ocWarehouseInfo.getExpressName().get());
                inventoryInfo.setPickRate(ocWarehouseInfo.getPickRate().getAsInt());
                inventoryInfo.setPickDate(PickDate.valueOf(ocWarehouseInfo.getPickDate().getAsInt()));
                inventoryInfo.setMark(ocWarehouseInfo.getMark().get());
                inventoryInfo.setRetrunDesc(ocWarehouseInfo.getRetrunDesc().get());
            }
        });
        Logger.info("库存信息进行配货率匹配-结束");


        Logger.info(String.format("配货率低于[%d]百分比,进行过滤.",PICK_RATE_LESS_THAN_DEL_LIMIT));
        intersectionList = intersectionList.parallelStream().
                filter(inventoryInfo -> inventoryInfo.getPickRate()>PICK_RATE_LESS_THAN_DEL_LIMIT)
                .collect(Collectors.toList());

        Logger.info(String.format("配货率低于[%d]百分比,进行过滤后的记录数:[%d].",PICK_RATE_LESS_THAN_DEL_LIMIT,intersectionList.size()));





        //尺码换算
        Logger.info("进行尺码换算");
        intersectionList.parallelStream()
                .filter(inventoryInfo -> inventoryInfo.getDivision().equals("鞋"))
                .filter(inventoryInfo -> Double.valueOf(inventoryInfo.getSize1()) <= 18)
                .forEach(inventoryInfo -> {
                    inventoryInfo.setSize1(
                            SizeUtils.getShoeSize1BySize2(
                                    inventoryInfo.getBrand(),
                                    inventoryInfo.getSex(),
                                    inventoryInfo.getSize1()
                            ));
                });

        Logger.info("尺码换算结束");

        //根据商家编码去重复项，计算各颜色价格平均值（根据商家编码与仓库算出唯一价格，在根据仓库计算平均值）











//      System.out.print(intersectionList);

        //删除
        //delDataGatheringFile(OrderCatConfig.getInventoryGroupIwhfile());

    }

    /**
     * 删除下载文件
     *
     * @param fileName
     * @throws Exception
     */
    private void delDataGatheringFile(String fileName) throws Exception {
        String dfileStr = OrderCatConfig.getOrderCatOutPutPath() + fileName;
        File dfile = new File(dfileStr);
        FileUtils.forceDeleteOnExit(dfile);
        Logger.debug("if exists:" + dfile.exists() + " onExit del:" + dfileStr);

    }


}
