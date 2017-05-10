package com.myjo.ordercat.handle;

import com.myjo.ordercat.config.OrderCatConfig;
import com.myjo.ordercat.domain.*;
import com.myjo.ordercat.exception.OCException;
import com.myjo.ordercat.http.TaoBaoHttp;
import com.myjo.ordercat.http.TianmaSportHttp;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_inventory_info.OcInventoryInfoManager;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_job_exec_info.OcJobExecInfo;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_job_exec_info.OcJobExecInfoManager;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_sales_info.OcSalesInfo;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_sales_info.OcSalesInfoImpl;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_sales_info.OcSalesInfoManager;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_warehouse_info.OcWarehouseInfo;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_warehouse_info.OcWarehouseInfoImpl;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_warehouse_info.OcWarehouseInfoManager;
import com.myjo.ordercat.utils.OcBigDecimalUtils;
import com.myjo.ordercat.utils.OcDateTimeUtils;
import com.myjo.ordercat.utils.OcSizeUtils;
import com.myjo.ordercat.utils.OcStringUtils;
import com.taobao.api.domain.Item;
import com.taobao.api.domain.Sku;
import com.taobao.api.domain.Trade;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;


/**
 * 同步库存相关
 * Created by lee5hx on 17/4/24.
 */
public class SyncInventory {


    private static final Logger Logger = LogManager.getLogger(SyncInventory.class);

    ;; //配货率小于多少删除


    //private static final int SKU_MULTIPLY_RATE = 60;
    //private static final int AVG_PRICE_ABOVE_RATE = OrderCatConfig.getAvgPriceAboveRate();
    // private static final long PRODUCT_SALES_LIMIT_COUNT = OrderCatConfig.getProductSalesLimitCount();


    //private static OrderCatConfig context;
    private TianmaSportHttp tianmaSportHttp;

    private TaoBaoHttp taoBaoHttp;

    private OcWarehouseInfoManager ocWarehouseInfoManager;

    private OcJobExecInfoManager ocJobExecInfoManager;

    private OcInventoryInfoManager ocInventoryInfoManager;

    private OcSalesInfoManager ocSalesInfoManager;

    public SyncInventory(TianmaSportHttp tianmaSportHttp, TaoBaoHttp taoBaoHttp) {


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


    private void writeWithCsvInventoryWriter(List<InventoryInfo> lists, Long execJobId) throws Exception {

        ICsvBeanWriter beanWriter = null;
        try {
            File file = new File(OrderCatConfig.getOrderCatOutPutPath() + String.format("inventory_info_rt_%d.csv", execJobId.intValue()));
            if (file.exists()) {
                FileUtils.forceDelete(file);
            }

            beanWriter = new CsvBeanWriter(new FileWriter(OrderCatConfig.getOrderCatOutPutPath() + String.format("inventory_info_rt_%d.csv", execJobId.intValue())),
                    CsvPreference.STANDARD_PREFERENCE);

            // the header elements are used to map the bean values to each column (names must match)
            final String[] header = new String[]{
                    "goodsNo",
                    "wareHouseID",
                    "warehouseName",
                    "size1",
                    "size2",
                    "brand",
                    "marketprice",
                    "num2",
                    "division",
                    "cate",
                    "sex",
                    "quarter",
                    "discount",
                    "bdiscount",
                    "pickRate",
                    "updateTime",
                    "pickDate",
                    "thedtime",
                    "proxyPrice",
                    "salesPrice",
                    "avgPrice",
                    "salesCount",
                    "expressName",
                    "retrunDesc",
                    "returnRate",
                    "endT",
                    "mark",
                    "numIid",
                    "skuId"
            };

            //final CellProcessor[] processors = getProcessors();

            // write the header
            beanWriter.writeHeader(header);

            // write the beans
            for (final InventoryInfo inventoryInfo : lists) {
                beanWriter.write(inventoryInfo, header);
            }

        } finally {
            if (beanWriter != null) {
                beanWriter.close();
            }
        }
    }


    private Integer getJobID(String jobName) {
        Integer execJobId;
        Optional<OcJobExecInfo> oexecJob = ocJobExecInfoManager.stream()
                .filter(OcJobExecInfo.STATUS.equal(JobStatus.SUCCESS.toString())
                        .and(OcJobExecInfo.JOB_NAME.equal(jobName))
                ).sorted(OcJobExecInfo.ID.comparator().reversed()).findFirst();

        if (oexecJob.isPresent()) {
            execJobId = (int) oexecJob.get().getId();
        } else {
            throw new OCException(String.format("对不起,没有找到对应的执行信息信息[%s]!", jobName));
        }
        return execJobId;
    }


    /**
     * 同步仓库信息
     */
    public void syncWarehouseInfo(Long execJobId) throws Exception {

        //删除
        delDataGatheringFile(OrderCatConfig.getInventoryGroupWhfile());


        ocWarehouseInfoManager.stream()
                .filter(OcWarehouseInfo.EXEC_JOB_ID.equal(execJobId.intValue()))
                .forEach(ocWarehouseInfoManager.remover());

        Logger.info("同步配货率信息,job-id:" + execJobId);
        //抓取天马库存信息数据
        Logger.info("抓取天马库存信息数据");
        dataGathering(OrderCatConfig.getInventoryGroupWhfile());
        List<InventoryInfo> list = getInventoryInfoInCsv(OrderCatConfig.getInventoryGroupWhfile());
        Logger.info("InventoryInfoInCsv.origin.size:" + list.size());

        //库存信息文件中根据仓库名称进行去重

        List<InventoryInfo> distinctWarehouseList = InventoryDataOperate.distinctWarehouseList(list);

        int distinctWarehouseList_size = distinctWarehouseList.size();
        Logger.info("distinctWarehouseList.size:" + distinctWarehouseList_size);


        //库存分组信息
        Map<String, List<InventoryInfo>> inventoryInfoInCsvMap =
                Stream.concat(list.parallelStream(), distinctWarehouseList.parallelStream())
                        .collect(Collectors.groupingBy(InventoryInfo::getWarehouseName));

        //匹配配货率
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
                if (j == OrderCatConfig.getFailurePickRateCount()) {//只检查10次
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
            ocWarehouseInfo.setExecJobId(execJobId.intValue());
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
            ocWarehouseInfo.setExecJobId(execJobId.intValue());

            ocWarehouseInfo.setEndT(null);
            ocWarehouseInfo.setMark(null);
            ocWarehouseInfo.setRetrunDesc(null);
            ocWarehouseInfo.setReturnRate(null);
            ocWarehouseInfo.setExpressName(null);

            ocWarehouseInfoManager.persist(ocWarehouseInfo);
        }


        //ocWarehouseInfo.setAddTime();


        // OcWarehouseInfo ocWarehouseInfo = new O


        ocJobExecInfoManager.stream()
                .filter(OcJobExecInfo.ID.equal(execJobId))
                .map(OcJobExecInfo.STATUS.setTo(JobStatus.SUCCESS.toString()))
                .forEach(ocJobExecInfoManager.updater());


        Logger.info(String.format("匹配完成,总仓库条数:[%d],匹配成功:[%d],未匹配成功:[%d] :", distinctWarehouseList_size, distinctPickRateList.size(), distinctWarehouseList_size - distinctPickRateList.size()));


//        Map<String, List<String>> map2 = list.stream()
//                .collect(
//                        Collectors.groupingBy(
//                                InventoryInfo::getWarehouseName,
//                                Collectors.mapping(InventoryInfo::getBrandName,
//                                        Collectors.toList())));
        //System.out.println(map2);

        //删除
        delDataGatheringFile(OrderCatConfig.getInventoryGroupWhfile());

    }


    public void syncSalesInfo(Long execJobId) throws Exception {

        LocalDateTime lend = LocalDateTime.now();
        LocalDateTime lbegin = lend.minusMonths(1);
        Date begin = OcDateTimeUtils.localDateTime2Date(lbegin);
        Date end = OcDateTimeUtils.localDateTime2Date(lend);

        List<Trade> trades = new ArrayList<>();
        for (TradeStatus ts : TradeStatus.JY_CG) {
            Logger.info(String.format("获取Taobao店铺销量[%s]-[%s],[%s]",
                    OcDateTimeUtils.localDateTime2String(lbegin),
                    OcDateTimeUtils.localDateTime2String(lend),
                    ts.toString()
            ));
            trades.addAll(taoBaoHttp.getSoldTrades(begin, end, ts));
            Logger.info(String.format("获取Taobao店铺销量[%s]-list.size:[%d]",
                    ts.toString(),
                    trades.size()
            ));
        }
        trades = trades.parallelStream()
                .filter(trade -> trade.getNumIid() != null)
                .collect(toList());

        Logger.info(String.format("获取Taobao店铺销量-过滤掉NumIid为空的记录后:%d", trades.size()));


        Map<Long, Long> tradesMap = trades
                .parallelStream()
                .collect(
                        groupingBy(
                                i -> i.getNumIid(),
                                summingLong(p -> p.getNum()))
                );


        Logger.info(String.format("获取Taobao店铺销量-按商品编码分组后.size:%d", tradesMap.size()));


        Logger.info(String.format("销量信息插入数据库"));
        OcSalesInfo ocSalesInfo;
        for (Map.Entry<Long, Long> entry : tradesMap.entrySet()) {
            ocSalesInfo = new OcSalesInfoImpl();
            ocSalesInfo.setNumIid(String.valueOf(entry.getKey()));
            ocSalesInfo.setSalesCount(entry.getValue().intValue());
            ocSalesInfo.setExecJobId(execJobId.intValue());
            ocSalesInfo.setAddTime(LocalDateTime.now());

            ocSalesInfoManager.persist(ocSalesInfo);

        }
        Logger.info(String.format("商品销量同步完成"));
    }

    public void syncTaoBaoInventory(Long execJobId) throws Exception {

        //删除
        delDataGatheringFile(OrderCatConfig.getInventoryGroupIwhfile());

        Logger.info("同步淘宝库存");
        //抓取天马库存信息数据
        Logger.info("抓取天马库存信息数据");
        dataGathering(OrderCatConfig.getInventoryGroupIwhfile());
        List<InventoryInfo> list =
                getInventoryInfoInCsv(OrderCatConfig.getInventoryGroupIwhfile());

        if (list.size() == 0) {
            throw new OCException("天马库存信息为空,请检测天马数据获取接口!");
        }

        Logger.info("InventoryInfoInCsv.origin.size:" + list.size());

        //获取Taobao店铺SKU-list
        Logger.info("获取Taobao店铺SKU-list");

        List<Item> itemsOnSaleList = taoBaoHttp.getTaobaoItemsOnSale();
        if (itemsOnSaleList.size() == 0) {
            throw new OCException("麦巨自营类目下商品信息为空!");
        }

        Logger.info(String.format("麦巨自营类目下所有商品-list.size:%d", itemsOnSaleList.size()));

        List<Sku> skus = taoBaoHttp.getTaoBaoItemSkus(itemsOnSaleList);

        if (skus.size() == 0) {
            throw new OCException("获取Taobao店铺SKU信息为空");
        }
        Logger.info("获取Taobao店铺SKU-list.size:" + skus.size());


        Integer salesJobId = getJobID(JobName.SYNC_SALES_INFO_JOB.getValue());
        Logger.info(String.format("同步销量信息最后一次执行ID:[%d]", salesJobId.intValue()));


        Map<String, OcSalesInfo> tradesMap = ocSalesInfoManager.stream()
                .filter(OcSalesInfo.EXEC_JOB_ID.equal(salesJobId))
                .collect(
                        Collectors.toMap(o -> o.getNumIid().get(),
                                Function.identity())
                );

        if (tradesMap == null || tradesMap.size() == 0) {
            throw new OCException("淘宝销量表[oc_sales_info]信息为空,请检查!");
        }


        //按照SKU,在天马库存中进行过滤
        Logger.info("按照SKU,在天马库存中进行过滤");
        Map<String, List<Sku>> inventoryInfoInCsvNumIidMap =
                skus.parallelStream()
                        .collect(Collectors.groupingBy(sku -> StringUtils.substringBeforeLast(sku.getOuterId(), "-")));

        //skus.parallelStream().forEach(sku -> System.out.println(sku.getOuterId()+":"+sku.getNumIid()));

        Map<String, Sku> inventoryInfoInCsvSkuIdMap =
                skus.parallelStream()
                        .filter(sku -> sku.getOuterId().indexOf("麦巨") == -1)
                        .filter(sku -> sku.getOuterId().indexOf("临时") == -1)
                        .collect(Collectors.toMap(o -> o.getOuterId() + ":" + o.getNumIid(), Function.identity()
//                                        ,
//                                        (o1, o2) -> {
//                                            System.out.println("duplicate key found:"+o1.getNumIid()+":"+o1.getOuterId());
//                                            return o1;
//                                        }
                        ));


        List<InventoryInfo> intersectionList = list.parallelStream()
                .filter(inventoryInfo -> !inventoryInfo.getDiscount().equals("折扣"))
                .filter(inventoryInfo -> new BigDecimal(inventoryInfo.getDiscount()).compareTo(BigDecimal.ZERO) == 1)
                .collect(Collectors.toList());

        Logger.info(String.format("过滤折扣小于等于零的库存.size:[%d]", intersectionList.size()));

        intersectionList = intersectionList.parallelStream()
                //.filter(inventoryInfo -> inventoryInfo.getSize1().indexOf("Y")<0)
                .filter(InventoryInfo.distinctBySkusMap(inventoryInfo -> inventoryInfo.getGoodsNo(), inventoryInfoInCsvNumIidMap))
                .collect(Collectors.toList());
        Logger.info("按照SKU,在天马库存中进行过滤后的条数:" + intersectionList.size());

        intersectionList.parallelStream().forEach(inventoryInfo -> {
            inventoryInfo.setNumIid(inventoryInfoInCsvNumIidMap.get(inventoryInfo.getGoodsNo()).get(0).getNumIid());
        });


        // 对库存信息进行配货率匹配  lee5hx


        Logger.info("对库存信息进行配货率匹配");

        Integer whExecJobId = getJobID(JobName.SYNC_WAREHOUSE_JOB.getValue());

        Logger.info(String.format("同步仓库信息最后一次执行ID:[%d]", whExecJobId.intValue()));


        //查询仓库信息
        Map<String, OcWarehouseInfo> warehouseMap = ocWarehouseInfoManager.stream()
                .filter(OcWarehouseInfo.EXEC_JOB_ID.equal(whExecJobId)
                        .and(OcWarehouseInfo.WAREHOUSE_ID.isNotNull()))
                .collect(
                        Collectors.toMap(o -> o.getWarehouseName(),
                                Function.identity())
                );

        if (warehouseMap == null || warehouseMap.size() == 0) {
            throw new OCException("仓库表[oc_warehouse_info]信息为空,请检查!");
        }
        Logger.info("仓库记录数-warehouseMap.size:" + warehouseMap.size());


        intersectionList.parallelStream().forEach(inventoryInfo -> {
            OcWarehouseInfo ocWarehouseInfo = warehouseMap.get(inventoryInfo.getWarehouseName());
            if (ocWarehouseInfo != null) {
                inventoryInfo.setWareHouseID(ocWarehouseInfo.getWarehouseId().getAsInt());
                inventoryInfo.setReturnRate(ocWarehouseInfo.getReturnRate().getAsInt());
                inventoryInfo.setUpdateTime(ocWarehouseInfo.getUdpateWarehouseTime().get());
                inventoryInfo.setExpressName(ocWarehouseInfo.getExpressName().get());
                inventoryInfo.setPickRate(ocWarehouseInfo.getPickRate().getAsInt());
                inventoryInfo.setPickDate(PickDate.valueOf(ocWarehouseInfo.getPickDate().getAsInt()));
                inventoryInfo.setMark(ocWarehouseInfo.getMark().get());
                inventoryInfo.setRetrunDesc(ocWarehouseInfo.getRetrunDesc().get());
                inventoryInfo.setEndT(ocWarehouseInfo.getEndT().get());
                inventoryInfo.setThedtime(String.valueOf(ocWarehouseInfo.getThedTime().getAsInt()));
            }
            //折扣转换，除以10
            inventoryInfo.setBdiscount(
                    OcBigDecimalUtils.divide(
                            new BigDecimal(inventoryInfo.getDiscount()),
                            BigDecimal.TEN
                    )
            );

            inventoryInfo.setProxyPrice(new BigDecimal(inventoryInfo.getMarketprice()).multiply(inventoryInfo.getBdiscount()));


        });
        Logger.info("库存信息进行配货率匹配-结束");


        Logger.info(String.format("配货率低于[%d]百分比,进行删除.", OrderCatConfig.getPickRateLessThanDelLimit()));
        intersectionList = intersectionList.parallelStream().
                filter(inventoryInfo -> inventoryInfo.getPickRate() > OrderCatConfig.getPickRateLessThanDelLimit())
                .collect(Collectors.toList());

        Logger.info(String.format("配货率低于[%d]百分比,进行删除后的记录数:[%d].", OrderCatConfig.getPickRateLessThanDelLimit(), intersectionList.size()));

        Logger.info("进行[童鞋]尺码删除.");

        intersectionList = intersectionList.parallelStream()
                .filter(inventoryInfo -> OcStringUtils.isCssNumeric(inventoryInfo))
                //.filter(inventoryInfo -> OcStringUtils.isNumeric(inventoryInfo.getSize1()))
                .collect(toList());

        Logger.info(String.format("进行[童鞋]尺码删除.size:[%d]", intersectionList.size()));


        //鞋类-尺码换算
        Logger.info("进行[鞋类]-尺码换算");
        intersectionList.parallelStream()
                .filter(inventoryInfo -> inventoryInfo.getDivision().equals("鞋"))
                .filter(inventoryInfo -> OcStringUtils.isNumeric(inventoryInfo.getSize1()))//儿童鞋，暂时不做计算
                .filter(inventoryInfo -> Double.valueOf(inventoryInfo.getSize1()) <= 18)
                .forEach(inventoryInfo -> {
                    inventoryInfo.setSize1(
                            OcSizeUtils.getShoeSize1BySize2(
                                    inventoryInfo.getBrand(),
                                    inventoryInfo.getSex(),
                                    inventoryInfo.getSize1()
                            ));
                });
        Logger.info("进行[鞋类]-尺码换算结束");

        //衣服-尺码换算
        Logger.info("进行[衣服类]-尺码换算");
        intersectionList.parallelStream()
                .filter(inventoryInfo -> inventoryInfo.getDivision().equals("服"))
                .forEach(inventoryInfo -> {
                    inventoryInfo.setSize1(
                            OcSizeUtils.getClothesConversionSize1(
                                    inventoryInfo.getSize1()
                            ));
                });

        Logger.info("进行[衣服类]-尺码换算结束");


        intersectionList = intersectionList.parallelStream()
                .filter(inventoryInfo -> inventoryInfo.getSize1().indexOf("error_size") == -1)
                .collect(toList());

        Logger.info(String.format("对错误尺码进行剔除-size:[%d]", intersectionList.size()));


        //库存汇总
        Map<String, Integer> quarterMap = intersectionList
                .parallelStream()
                .filter(p -> p.getWareHouseID() != null)
                .collect(
                        groupingBy(
                                i -> i.getGoodsNo() + ":" + i.getWareHouseID() + ":" + i.getSize1(),
                                summingInt(p -> Integer.parseInt(p.getNum2()))
                        ));

        Logger.info(String.format("根据配货率与库存过滤"));
        intersectionList = InventoryDataOperate.filterPickRateList(intersectionList, quarterMap);
        Logger.info(String.format("根据配货率与库存过滤-size:[%d]", intersectionList.size()));


        //商品-平均价格
        Map<String, Double> avgPriceMap = intersectionList
                .parallelStream()
                .filter(p -> p.getWareHouseID() != null)
                .filter(InventoryInfo.distinctByField(inventoryInfo1 -> inventoryInfo1.getWareHouseID() + ":" + inventoryInfo1.getGoodsNo()))
                .collect(
                        groupingBy(
                                i -> i.getGoodsNo(),
                                averagingDouble((InventoryInfo x) ->
                                        x.getProxyPrice().doubleValue()
                                )
                        )
                );

        //赋值平均价格
        intersectionList.parallelStream().forEach(inventoryInfo -> {
            BigDecimal avgPrice = InventoryDataOperate.getAvgPrice(avgPriceMap, inventoryInfo.getGoodsNo());
            avgPrice = avgPrice.add(avgPrice.multiply(BigDecimal.valueOf(OrderCatConfig.getAvgPriceAboveRate() / 100)));
            inventoryInfo.setAvgPrice(avgPrice);
        });


        Logger.info(String.format("过滤平均采购价格"));
        intersectionList = InventoryDataOperate.filterAvgPriceList(intersectionList);
        Logger.info(String.format("过滤平均采购价格-size:[%d]", intersectionList.size()));

        //所有仓库，对应尺码最低价格
        Map<String, Optional<InventoryInfo>> whSizePriceMap = intersectionList
                .parallelStream()
                .filter(p -> p.getWareHouseID() != null)
                .collect(
                        groupingBy(
                                i -> i.getGoodsNo() + ":" + i.getSize1(),
                                Collectors.minBy(Comparator.comparingDouble(i -> i.getProxyPrice().doubleValue()))

                        ));


        //尺码汇总
        Map<String, Long> size1Map = intersectionList
                .parallelStream()
                .filter(p -> p.getWareHouseID() != null)
                .collect(
                        groupingBy(
                                i -> i.getGoodsNo() + ":" + i.getWareHouseID(),
                                Collectors.counting()
                        ));


        List<GoodsInventoryInfo> goodsInventoryInfos = new ArrayList<>();
        GoodsInventoryInfo goodsInventoryInfo;
        String[] strKeys;
        for (Map.Entry<String, Long> entry : size1Map.entrySet()) {
            goodsInventoryInfo = new GoodsInventoryInfo();
            strKeys = entry.getKey().split(":", -1);
            //System.out.println("key = " + entry.getKey() + " and value = " + entry.getValue());
            goodsInventoryInfo.setGoodsNo(strKeys[0]);
            goodsInventoryInfo.setWareHouseID(strKeys[1]);
            goodsInventoryInfo.setSizeCount(entry.getValue());
            goodsInventoryInfos.add(goodsInventoryInfo);
        }
        //货号最大尺码数
        Map<String, Optional<GoodsInventoryInfo>> maxSize1Map = goodsInventoryInfos.parallelStream()
                .collect(
                        groupingBy(
                                i -> i.getGoodsNo(),
                                Collectors.maxBy(Comparator.comparingLong(i -> i.getSizeCount()))
                        )
                );


        // 赋值销量
        intersectionList.parallelStream().forEach(inventoryInfo -> {
            long rt = 0;
            String numIid = String.valueOf(inventoryInfo.getNumIid().toString());
            if (tradesMap.get(numIid) != null) {
                rt = tradesMap.get(numIid).getSalesCount().getAsInt();
            }
            inventoryInfo.setSalesCount(rt);
//            if (inventoryInfoInCsvSkuIdMap.get(inventoryInfo.getGoodsNo() + "-" + inventoryInfo.getSize1() + ":" + inventoryInfo.getNumIid()) == null) {
//                inventoryInfo.setSkuId(8888888888888888l);
//            } else {
//                inventoryInfo.setSkuId(inventoryInfoInCsvSkuIdMap.get(inventoryInfo.getGoodsNo() + "-" + inventoryInfo.getSize1() + ":" + inventoryInfo.getNumIid()).getSkuId());
//            }

        });


        Logger.info(String.format("销量小于[%d]-只要仓库有货,取最低价格.", OrderCatConfig.getProductSalesLimitCount()));
        intersectionList.parallelStream()
                .filter(inventoryInfo -> inventoryInfo.getWareHouseID() != null)
                .filter(inventoryInfo -> inventoryInfo.getSalesCount() < OrderCatConfig.getProductSalesLimitCount())
                .forEach(inventoryInfo -> {
                    Optional<InventoryInfo> op = whSizePriceMap.get(inventoryInfo.getGoodsNo() + ":" + inventoryInfo.getSize1());
                    if (op.isPresent()) {
                        if (op.get().getWareHouseID() == inventoryInfo.getWareHouseID()) {
                            inventoryInfo.setSalesPrice(OcBigDecimalUtils.toSalesPrice(op.get().getProxyPrice(), false));
                        }
                    }
                });

        //筛选采购价高于  平均价（+1%）
        //Logger.info(String.format("过滤SKU大于最大SKU*[%d]百分比.", SKU_MULTIPLY_RATE));
        Logger.info(String.format("销量大于[%d]-筛选采购价高于平均价的[%d]百分比 并且 过滤SKU大于最大SKU*[%d]百分比.",
                OrderCatConfig.getProductSalesLimitCount(),
                OrderCatConfig.getAvgPriceAboveRate(),
                OrderCatConfig.getSkuMultiplyRate()));


        Map<String, Optional<InventoryInfo>> mapMinSkuPrice = intersectionList.parallelStream()
                .filter(inventoryInfo -> inventoryInfo.getWareHouseID() != null)
                //.filter(inventoryInfo -> filterAvgPriceAbove(inventoryInfo, avgPriceMap))
                .filter(inventoryInfo ->
                        getSize1Count(size1Map, inventoryInfo.getGoodsNo(), inventoryInfo.getWareHouseID()) >=
                                getMaxSkuAvgCount(maxSize1Map, inventoryInfo.getGoodsNo()))
                .collect(
                        groupingBy(
                                i -> i.getGoodsNo(),
                                Collectors.minBy(Comparator.comparingDouble(i -> i.getProxyPrice().doubleValue()))
                        )
                );


        intersectionList.parallelStream()
                .filter(inventoryInfo -> inventoryInfo.getWareHouseID() != null)
                .filter(inventoryInfo -> mapMinSkuPrice.get(inventoryInfo.getGoodsNo()) != null)
                //.filter(inventoryInfo -> filterAvgPriceAbove(inventoryInfo, avgPriceMap))
                .filter(inventoryInfo -> inventoryInfo.getSalesCount() >= OrderCatConfig.getProductSalesLimitCount())
                .filter(inventoryInfo ->
                        inventoryInfo.getProxyPrice().compareTo(mapMinSkuPrice.get(inventoryInfo.getGoodsNo()).get().getProxyPrice()) <= 0


                )
                .forEach(inventoryInfo -> {
                    inventoryInfo.setSalesPrice(OcBigDecimalUtils.toSalesPrice(mapMinSkuPrice.get(inventoryInfo.getGoodsNo()).get().getProxyPrice(), true));
                });


        Logger.info(String.format("销量大于[%d]-筛选采购价高于平均价的[%d]百分比 并且 过滤SKU大于最大SKU*[%d]百分比. 记录数:[%d]",
                OrderCatConfig.getProductSalesLimitCount(),
                OrderCatConfig.getAvgPriceAboveRate(),
                OrderCatConfig.getSkuMultiplyRate(),
                intersectionList.size()));


        Logger.info(String.format("正在输出结果"));

        List<InventoryInfo> csvList = intersectionList.parallelStream()
                .filter(inventoryInfo -> inventoryInfo.getSalesPrice() != null).collect(toList());

        writeWithCsvInventoryWriter(csvList, execJobId);

        Logger.info(String.format("输出结果list.size:[%d]", csvList.size()));


        Logger.info(String.format("开始同步淘宝库存与销售价格"));


        skus = skus.parallelStream()
                .filter(sku -> sku.getOuterId().indexOf("麦巨") == -1)
                .filter(sku -> sku.getOuterId().indexOf("临时") == -1)
                .collect(toList());


        Logger.info(String.format("SKU-list中过滤掉商家编码中有[ 麦巨 or 临时 ]的SKU:[%d]", skus.size()));


        Map<String, InventoryInfo> csvListSukMap =
                csvList.parallelStream()
                        .collect(Collectors.toConcurrentMap(o -> o.getGoodsNo()+"-"+o.getSize1(), Function.identity()
                                ,
                                (o1, o2) -> {
                                    //System.out.println("duplicate key found:"+o1.getSkuId());
                                    o1.setNum2(String.valueOf(Integer.valueOf(o1.getNum2()) + Integer.valueOf(o2.getNum2()))); // 对库存进行累计(因为一个商品代码对应的多个SKU)
                                    return o1;
                                }
                        ));


        //System.out.println(csvListSukMap.get(3409869335638l).getNum2());

        Logger.info(String.format("csvListSukMap.size:[%d]", csvListSukMap.size()));


        Map<Long, List<Sku>> skuNumIidMap =
                skus.parallelStream()
                        .collect(Collectors.groupingBy(sku -> sku.getNumIid()));


        Logger.info(String.format("skuNumIidMap.size:[%d]", skuNumIidMap.size()));
        List<Sku> subSkuList;
        List<InventoryInfo> subSkuIPriceList ;//获取
        //List<InventoryInfo> subSkuIQuantityList ;
        //按照宝贝ID,汇总更新SKU需要的价格.
        Map<Long,List<InventoryInfo>> subSkuIPriceListMap = new ConcurrentHashMap<>();

        for (Map.Entry<Long, List<Sku>> entry : skuNumIidMap.entrySet()) {
            subSkuIPriceList = new ArrayList<>();
            final long itemId = entry.getKey().longValue();

            subSkuList = entry.getValue();
            subSkuIPriceList.addAll(
                    subSkuList.stream()
                            .map(sku -> {
                                InventoryInfo iv = csvListSukMap.get(sku.getOuterId());
                                if(iv!=null){
                                    if(iv.getNumIid().longValue() == itemId){ // 商品ID必须一致，因为按货号去会取到别的宝贝ID的信息
                                        iv.setSkuId(sku.getSkuId());//赋值SKU,价格更新方面直接获取
                                    }else {
                                        iv = null;
                                    }
                                }
                                return iv;
                            })
                            .collect(Collectors.toList()));

            subSkuIPriceListMap.put(itemId,subSkuIPriceList);
        }


        //更新淘宝库存与价格(接口调用)
        if (csvListSukMap != null && csvListSukMap.size() > 0) {
            skuNumIidMap.entrySet().parallelStream()
                    //.filter(longListEntry -> longListEntry.getKey() == 538962836245l || longListEntry.getKey() == 537503925789l)
                    //.filter(longListEntry -> longListEntry.getKey() == 535913995369l)
                    .forEach(longListEntry -> {
                Logger.info(String.format("开始同步-商品ID: [%d] 的SKU价格与库存. ", longListEntry.getKey()));
                try {
                    taoBaoHttp.updateTmallQuantityAndPrice(
                            longListEntry.getKey(),//商品ID
                            subSkuIPriceListMap.get(longListEntry.getKey()),//需要更新的价格信息
                            longListEntry.getValue(),//该商品ID下的，SKUlist
                            csvListSukMap);//
                } catch (Exception e) {
                    Logger.error(String.format("[%d]淘宝库存与价格更新失败:",longListEntry.getKey().longValue()),e);
                    //e.printStackTrace();
                    //throw new OCException(String.format("[%d]淘宝库存与价格更新失败:",longListEntry.getKey().longValue()),e);
                }
            });
        } else {
            throw new OCException("csvListSukMap接口SKU映射为空!请检查!");
        }

        //删除
        delDataGatheringFile(OrderCatConfig.getInventoryGroupIwhfile());

        Logger.info(String.format("运行完成."));

    }

    //final Comparator<InventoryInfo> inventoryInfoComp = (p1, p2) -> p1.getSalesPrice().compareTo(p2.getSalesPrice());


    private Long getMaxSkuAvgCount(Map<String, Optional<GoodsInventoryInfo>> maxSize1Map, String goodsNo) {
        GoodsInventoryInfo goodsInventoryInfo = maxSize1Map.get(goodsNo).get();
        double t = (double) OrderCatConfig.getSkuMultiplyRate() / 100;
        Long rt = Math.round(goodsInventoryInfo.getSizeCount() * t);
        return rt;
    }

    private Long getSize1Count(Map<String, Long> size1Map, String goodsNo, Integer wareHouseID) {
        return size1Map.get(goodsNo + ":" + wareHouseID);
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
        if (dfile.exists()) {
            FileUtils.forceDelete(dfile);
        }
        Logger.debug("if exists:" + dfile.exists() + " onExit del:" + dfileStr);

    }


    public OcWarehouseInfoManager getOcWarehouseInfoManager() {
        return ocWarehouseInfoManager;
    }

    public void setOcWarehouseInfoManager(OcWarehouseInfoManager ocWarehouseInfoManager) {
        this.ocWarehouseInfoManager = ocWarehouseInfoManager;
    }

    public OcJobExecInfoManager getOcJobExecInfoManager() {
        return ocJobExecInfoManager;
    }

    public void setOcJobExecInfoManager(OcJobExecInfoManager ocJobExecInfoManager) {
        this.ocJobExecInfoManager = ocJobExecInfoManager;
    }

    public OcInventoryInfoManager getOcInventoryInfoManager() {
        return ocInventoryInfoManager;
    }

    public void setOcInventoryInfoManager(OcInventoryInfoManager ocInventoryInfoManager) {
        this.ocInventoryInfoManager = ocInventoryInfoManager;
    }

    public OcSalesInfoManager getOcSalesInfoManager() {
        return ocSalesInfoManager;
    }

    public void setOcSalesInfoManager(OcSalesInfoManager ocSalesInfoManager) {
        this.ocSalesInfoManager = ocSalesInfoManager;
    }
}
