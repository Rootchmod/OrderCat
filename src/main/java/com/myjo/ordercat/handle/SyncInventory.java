package com.myjo.ordercat.handle;

import com.myjo.ordercat.bean.InventoryInfo;
import com.myjo.ordercat.bean.InventoryQueryCondition;
import com.myjo.ordercat.config.OrderCatConfig;
import com.myjo.ordercat.http.TianmaSportHttp;
import com.speedment.common.injector.execution.Execution;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.supercsv.cellprocessor.*;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by lee5hx on 17/4/24.
 */
public class SyncInventory {


    private static final Logger Logger = LogManager.getLogger(SyncInventory.class);

    //private static OrderCatConfig context;
    private static TianmaSportHttp tianmaSportHttp;

    public SyncInventory(TianmaSportHttp tianmaSportHttp) {
        this.tianmaSportHttp = tianmaSportHttp;
    }


    /**
     * 数据采集,多个CSV合成一个
     */
    public void dataGathering() throws Exception {
        //删除历史库存CSV
        String dfileStr = OrderCatConfig.getOrderCatOutPutPath() + OrderCatConfig.getInventoryGroupFileName();
        //IOUtils.toByteArray(inputStream);
        File dfile = new File(dfileStr);
        FileUtils.forceDeleteOnExit(dfile);
        Logger.debug("if exists:" + dfile.exists() + " onExit del:" + dfileStr);

        //下载合并CSV
        List<InventoryQueryCondition> list = OrderCatConfig.getInventoryQueryConditions();
        Logger.debug("dataGathering.InventoryQueryCondition.list" + list.size());
        for (InventoryQueryCondition iqc : list) {
            Logger.debug("dataGathering.InventoryQueryCondition.BrandName" + iqc.getBrandName());
            Logger.debug("dataGathering.InventoryQueryCondition.Quarter" + iqc.getQuarter());
            tianmaSportHttp.inventoryDownGroup(iqc.getBrandName(), iqc.getQuarter());
        }
        Logger.debug("SyncInventory.dataGathering.exec done.");
    }


    //商品货号
    //货源
    //中国尺码
    //外国尺码
    //品牌
    //市场价
    //库存数量
    //类别
    //小类
    //性别
    //季节
    //折扣

    private CellProcessor[] getProcessors() {

        //final String emailRegex = "[a-z0-9\\._]+@[a-z0-9\\.]+"; // just an example, not very robust!
        //StrRegEx.registerMessage(emailRegex, "must be a valid email address");

        final CellProcessor[] processors = new CellProcessor[]{
                new Optional(),//商品货号 )
                new Optional(),//货源
                new Optional(),//中国尺码
                new Optional(),//外国尺码
                new Optional(),//品牌
                new Optional(new ParseBigDecimal()),//市场价
                new Optional(new ParseInt()),//库存数量
                new Optional(),//类别
                new Optional(),//小类
                new Optional(),//性别
                new Optional(),//季节
                new Optional() //折扣
        };

        return processors;
    }


    private List<InventoryInfo> getInventoryInfoInCsv() throws Exception {

        List<InventoryInfo> list = new ArrayList<>();
        String dfileStr = OrderCatConfig.getOrderCatOutPutPath() + OrderCatConfig.getInventoryGroupFileName();
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
                inventoryInfo.setBrandName(customerList.get(4));
                inventoryInfo.setMarketprice(customerList.get(5));
                inventoryInfo.setNum2(customerList.get(6));
                inventoryInfo.setDivision(customerList.get(7));
                inventoryInfo.setCate(customerList.get(8));
                inventoryInfo.setSex(customerList.get(9));
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
//        DataGatheringHandle dataGatheringHandle = new DataGatheringHandle(tianmaSportHttp);
//        dataGatheringHandle.exec();

        List<InventoryInfo> list = getInventoryInfoInCsv();
        Logger.info("InventoryInfoInCsv.origin.size:" + list.size());
        List<InventoryInfo> distinctWarehouseList = list.parallelStream()
                .filter(inventoryInfo -> !inventoryInfo.getDiscount().equals("折扣"))
                //.filter(InventoryInfo.distinctByField(inventoryInfo -> inventoryInfo.getGoodsNo()))
                .filter(InventoryInfo.distinctByField(inventoryInfo -> inventoryInfo.getWarehouseName()))
                .collect(Collectors.toList());


        int distinctWarehouseList_size = distinctWarehouseList.size();
        Logger.info("distinctWarehouseList.size:"+distinctWarehouseList_size);


        //库存分组信息
        Map<String,List<InventoryInfo>> inventoryInfoInCsvMap =
                Stream.concat(list.parallelStream(),distinctWarehouseList.parallelStream())
                        .collect(Collectors.groupingBy(InventoryInfo::getWarehouseName));

//        distinctWarehouseList.parallelStream().forEach(inventoryInfo ->
//                Logger.info(inventoryInfo.getGoodsNo()+":"+inventoryInfo.getWarehouseName()));

        //匹配配合率
        List<InventoryInfo> pickRateList = new ArrayList<>();
        InventoryInfo inventoryInfo;
        for(int i=0;i<distinctWarehouseList.size();i++){
            inventoryInfo = distinctWarehouseList.get(i);
            Logger.info(inventoryInfo.getGoodsNo()+":"+inventoryInfo.getWarehouseName()+"--"+(i+1)+"/"+distinctWarehouseList.size());
            pickRateList.addAll(tianmaSportHttp.getSearchByArticleno(inventoryInfo.getGoodsNo()));

        }
        List<InventoryInfo> distinctPickRateList =pickRateList.parallelStream()
                .filter(InventoryInfo.distinctByField(inventoryInfo1 -> inventoryInfo1.getWareHouseID()))
                .collect(Collectors.toList());

        Logger.info("pickRateList.size:"+pickRateList.size());
        Logger.info("匹配成功,配合率信息:"+distinctPickRateList.size());



        //没有匹配到配合率的货源
        List<InventoryInfo> noRateList = new ArrayList<>();
        Map<String,List<InventoryInfo>> map =
                Stream.concat(distinctPickRateList.parallelStream(),distinctWarehouseList.parallelStream())
                .collect(Collectors.groupingBy(InventoryInfo::getWarehouseName));
//                .filter(InventoryInfo.distinctByField(inventoryInfo2 -> inventoryInfo2.getWarehouseName()))
//                .collect(Collectors.toList());

        InventoryInfo noRate;
        for (Map.Entry<String, List<InventoryInfo>> entry : map.entrySet()) {
            if(entry.getValue().size()==1){
                noRate = entry.getValue().get(0);
                noRateList.add(noRate);
            }
        }
        Logger.info("未匹配成功,配合率信息:"+noRateList.size());

        //继续匹配
        Logger.info("继续匹配,未成功配合率信息:"+noRateList.size());
        List<InventoryInfo> inventoryInfos;
        List<InventoryInfo> for_inventoryInfos;
        for(int i=0;i<noRateList.size();i++){
            inventoryInfo = noRateList.get(i);
            inventoryInfos = inventoryInfoInCsvMap.get(inventoryInfo.getWarehouseName())
                    .parallelStream()
                    .filter(InventoryInfo.distinctByField(inventoryInfo1 -> inventoryInfo1.getGoodsNo())).collect(Collectors.toList());
            for(int j= 0;j<inventoryInfos.size();j++){
                if(j==10){//只检查10次
                   break;
                }
                inventoryInfo = inventoryInfos.get(j);
                Logger.info(inventoryInfo.getGoodsNo()+":"+inventoryInfo.getWarehouseName()+"--"+(j+1)+"/"+inventoryInfos.size());
                for_inventoryInfos = tianmaSportHttp.getSearchByArticleno(inventoryInfo.getGoodsNo());
                for(InventoryInfo info  :for_inventoryInfos){
                    if(info.getWarehouseName().equals(inventoryInfo.getWarehouseName())){
                        pickRateList.addAll(for_inventoryInfos);
                        Logger.info("已经找到[%s]的配货率信息!",inventoryInfo.getWarehouseName());

                        j = inventoryInfos.size();
                        break;
                    }
                }


            }
        }


        distinctPickRateList =pickRateList.parallelStream()
                .filter(InventoryInfo.distinctByField(inventoryInfo1 -> inventoryInfo1.getWareHouseID()))
                .collect(Collectors.toList());


        Logger.info("pickRateList.size:"+pickRateList.size());


        Logger.info(String.format("匹配完成,总仓库条数:[%d],匹配成功:[%d],未匹配成功:[%d] :",distinctWarehouseList_size,distinctPickRateList.size(),distinctWarehouseList_size-distinctPickRateList.size()));


        distinctPickRateList.parallelStream().forEach(inventoryInfo1 ->
                Logger.info(inventoryInfo1.getWareHouseID()+"|"+inventoryInfo1.getWarehouseName()+"|"+inventoryInfo1.getPickRate()+"|"+inventoryInfo1.getUpdateTime()));


        noRateList.parallelStream().forEach(inventoryInfo1 ->
                Logger.info(inventoryInfo1.getWareHouseID()+"|"+inventoryInfo1.getWarehouseName()+"|"+inventoryInfo1.getPickRate()+"|"+inventoryInfo1.getUpdateTime()));



//        Map<String, List<String>> map2 = list.stream()
//                .collect(
//                        Collectors.groupingBy(
//                                InventoryInfo::getWarehouseName,
//                                Collectors.mapping(InventoryInfo::getBrandName,
//                                        Collectors.toList())));
       //System.out.println(map2);
    }


}
