package com.myjo.ordercat.utils;

import com.myjo.ordercat.config.OrderCatConfig;
import com.myjo.ordercat.domain.InventoryInfo;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_fenxiao_check_result.OcFenxiaoCheckResult;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tmsport_check_result.OcTmsportCheckResult;
import org.apache.commons.io.FileUtils;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lee5hx on 17/5/24.
 */
public class OcCsvUtils {


    public static void writeWithCsvInventoryWriter(List<InventoryInfo> lists, Long execJobId) throws Exception {

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


    public static void writeWithCsvOcTianmaCheckResultWriter(List<OcTmsportCheckResult>  lists, Long execJobId) throws Exception {

        List<Object[]> list = new ArrayList();
        Object[] objects;
        for (OcTmsportCheckResult o : lists) {
            objects = new Object[]{
                    o.getId(),
                    o.getTmOrderIds().isPresent()?o.getTmOrderIds():"",//天马IDs
                    o.getTmOuterOrderId().isPresent()?o.getTmOuterOrderId().get():"",//天马外部订单编码(淘宝订单)
                    o.getTmOrderNum().isPresent()?o.getTmOrderNum().getAsLong():"",//天马订单数量
                    o.getTmNum().isPresent() ? o.getTmNum().getAsLong() : "",//天马购买数量
                    o.getTbOrderNum().isPresent()?o.getTbOrderNum().getAsLong():"",//淘宝订单数量
                    o.getTbNum().isPresent()?o.getTbNum().getAsLong():"",//淘宝购买数量
                    o.getTbCreated().isPresent()?OcDateTimeUtils.localDateTime2String(o.getTbCreated().get()):"",//'淘宝订单时间'
                    o.getTbPaytime().isPresent()?OcDateTimeUtils.localDateTime2String(o.getTbPaytime().get()):"",//'淘宝支付时间'
                    o.getTbPrice().isPresent()?o.getTbPrice().get().toPlainString():"",//商品价格
                    o.getTbPayment().isPresent()?o.getTbPayment().get().toPlainString():"",//子订单实付金额
                    o.getTbDiscountFee().isPresent()?o.getTbDiscountFee().get().toPlainString():"",//子订单级订单优惠金额
                    o.getTbTotalFee().isPresent()?o.getTbTotalFee().get().toPlainString():"",//应付金额
                    o.getDzStatus().isPresent()?o.getDzStatus().get():"",//'对账状态'
                    o.getDzDetailsMessage().isPresent()?o.getDzDetailsMessage().get():"",//''对账详细描述''
                    o.getRemarks().isPresent()?o.getRemarks().get():"",//备注
                    OcDateTimeUtils.localDateTime2String(o.getAddTime()),//对账时间
            };
            list.add(objects);
        }


        ICsvListWriter listWriter = null;
        try {


            File file = new File(OrderCatConfig.getOrderCatOutPutPath() + String.format("tianma_check_rt_%d.csv", execJobId.intValue()));
            if (file.exists()) {
                FileUtils.forceDelete(file);
            }

            listWriter = new CsvListWriter(new FileWriter(OrderCatConfig.getOrderCatOutPutPath() + String.format("tianma_check_rt_%d.csv", execJobId.intValue())),
                    CsvPreference.STANDARD_PREFERENCE);

            final String[] header = new String[]{
                    "序号",
                    "天马订单",
                    "天马外部订单编码(淘宝订单)",
                    "天马订单数量",
                    "天马购买数量",
                    "淘宝订单数量",
                    "淘宝购买数量",
                    "淘宝订单时间",
                    "淘宝支付时间",
                    "商品价格",
                    "子订单实付金额",
                    "子订单级订单优惠金额",
                    "应付金额",
                    "对账状态",
                    "对账详细描述",
                    "备注",
                    "对账日期",
            };

            // write the header
            listWriter.writeHeader(header);

            // write the customer lists

            for (Object[] o : list) {
                listWriter.write(o);
            }

        } finally {
            if (listWriter != null) {
                listWriter.close();
            }
        }
    }


    public static void writeWithCsvOcFenxiaoCheckResultWriter(List<OcFenxiaoCheckResult> lists, Long execJobId) throws Exception {

        List<Object[]> list = new ArrayList();
        Object[] objects;
        for (OcFenxiaoCheckResult o : lists) {
            objects = new Object[]{
                    o.getId(),
                    o.getTid().getAsLong(),
                    o.getOrderStatus().isPresent() ? o.getOrderStatus().get() : "",
                    o.getRefundId().getAsLong(),
                    o.getNumIid().getAsLong(),
                    o.getTitle(),
                    o.getFenxiaoId().isPresent() ? o.getFenxiaoId().getAsLong() : "",
                    o.getSupplierNick().isPresent() ? o.getSupplierNick().get() : "",
                    o.getDistributorNick().isPresent() ? o.getOrderStatus().get() : "",
                    o.getFenxiaoRefundStatus().isPresent()?o.getFenxiaoRefundStatus().get():"",
                    o.getFenxiaoRefundFee().isPresent()?o.getFenxiaoRefundFee().get().toPlainString():"",
                    o.getFenxiaoPaySupFee().isPresent()?o.getFenxiaoPaySupFee().get().toPlainString():"",
                    o.getFenxiaoRefundDesc().isPresent()?o.getFenxiaoRefundDesc().get():"",
                    o.getFenxiaoRefundReason().isPresent()?o.getFenxiaoRefundReason().get():"",
                    o.getStatus().isPresent()?o.getStatus().get():"",
                    o.getRemarks().isPresent()?o.getRemarks().get():"",
                    o.getAddTime()
            };
            list.add(objects);
        }


        ICsvListWriter listWriter = null;
        try {


            File file = new File(OrderCatConfig.getOrderCatOutPutPath() + String.format("fenxiao_check_rt_%d.csv", execJobId.intValue()));
            if (file.exists()) {
                FileUtils.forceDelete(file);
            }

            listWriter = new CsvListWriter(new FileWriter(OrderCatConfig.getOrderCatOutPutPath() + String.format("fenxiao_check_rt_%d.csv", execJobId.intValue())),
                    CsvPreference.STANDARD_PREFERENCE);

            final String[] header = new String[]{
                    "序号",
                    "订单ID",
                    "订单状态",
                    "退款ID",
                    "宝贝ID",
                    "宝贝标题",
                    "分销ID",
                    "供应商nick",
                    "分销商昵称",
                    "分销退款状态",
                    "分销退款的金额",
                    "分销-支付给供应商的金额",
                    "分销-退款原因",
                    "分销-退款说明",
                    "对账状态",
                    "备注",
                    "对账日期",
            };

            // write the header
            listWriter.writeHeader(header);

            // write the customer lists

            for (Object[] o : list) {
                listWriter.write(o);
            }

        } finally {
            if (listWriter != null) {
                listWriter.close();
            }
        }
    }

}
