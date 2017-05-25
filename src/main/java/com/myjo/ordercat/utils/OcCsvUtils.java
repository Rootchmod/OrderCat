package com.myjo.ordercat.utils;

import com.myjo.ordercat.config.OrderCatConfig;
import com.myjo.ordercat.domain.InventoryInfo;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_fenxiao_check_result.OcFenxiaoCheckResult;
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


    public static void writeWithCsvOcFenxiaoCheckResultWriter(List<OcFenxiaoCheckResult> lists, Long execJobId) throws Exception {

        // create the customer Lists (CsvListWriter also accepts arrays!)
//        final List<Object> john = Arrays.asList(new Object[] { "1", "John", "Dunbar",
//                new GregorianCalendar(1945, Calendar.JUNE, 13).getTime(),
//                "1600 Amphitheatre Parkway\nMountain View, CA 94043\nUnited States", null, null,
//                "\"May the Force be with you.\" - Star Wars", "jdunbar@gmail.com", 0L });
//
//        final List<Object> bob = Arrays.asList(new Object[] { "2", "Bob", "Down",
//                new GregorianCalendar(1919, Calendar.FEBRUARY, 25).getTime(),
//                "1601 Willow Rd.\nMenlo Park, CA 94025\nUnited States", true, 0,
//                "\"Frankly, my dear, I don't give a damn.\" - Gone With The Wind", "bobdown@hotmail.com", 123456L });

//  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
//  `tid` BIGINT DEFAULT NULL COMMENT '订单ID',
//  `order_status` varchar(255)  COMMENT '订单状态:TRADE_NO_CREATE_PAY(没有创建支付宝交易) WAIT_BUYER_PAY(等待买家付款) WAIT_SELLER_SEND_GOODS(等待卖家发货,即:买家已付款) WAIT_BUYER_CONFIRM_GOODS(等待买家确认收货,即:卖家已发货) TRADE_BUYER_SIGNED(买家已签收,货到付款专用) TRADE_FINISHED(交易成功) TRADE_CLOSED(交易关闭) TRADE_CLOSED_BY_TAOBAO(交易被淘宝关闭) ALL_WAIT_PAY(包含：WAIT_BUYER_PAY、TRADE_NO_CREATE_PAY) ALL_CLOSED(包含：TRADE_CLOSED、TRADE_CLOSED_BY_TAOBAO)',
//  `refundId` BIGINT DEFAULT NULL COMMENT '退款ID',
//  `numIid` BIGINT DEFAULT NULL COMMENT '宝贝ID',
//  `title` varchar(1000) NOT NULL COMMENT '宝贝标题',
//  `fenxiaoId` BIGINT COMMENT '分销ID',
//  `supplier_nick` varchar(255) COMMENT '供应商nick',
//  `distributor_nick` varchar(255) COMMENT '分销商昵称',
//  `fenxiao_refund_status` varchar(255) COMMENT '分销退款状态 1：买家已经申请退款，等待卖家同意 2：卖家已经同意退款，等待买家退货 3：买家已经退货，等待卖家确认收货 4：退款关闭 5：退款成功 6：卖家拒绝退款 12：同意退款，待打款 9：没有申请退款 10：卖家拒绝确认收货',
//  `fenxiao_refund_fee` decimal(25,10) COMMENT '分销退款的金额',
//  `fenxiao_pay_sup_fee` decimal(25,10) COMMENT '分销-支付给供应商的金额',
//  `fenxiao_refund_desc` varchar(255)  COMMENT '分销-退款原因',
//  `fenxiao_refund_reason` varchar(255) COMMENT '分销-退款说明',
//  `status` varchar(255)  COMMENT '对账状态 NOT_FENXIAO("NOT_FENXIAO"),NOT_FENXIAO_REFUND("NOT_FENXIAO_REFUND"),NOT_FENXIAO("NOT_FENXIAO"),SUCCESS_REFUND("SUCCESS_REFUND")',
//  `remarks` TEXT COMMENT ' (json格式)',
//  `add_time` timestamp NOT NULL COMMENT '添加日期',
//

        List<Object[]> list = new ArrayList();
        Object[] objects;
        for (OcFenxiaoCheckResult o : lists) {
            objects = new Object[]{
                    o.getId(),
                    o.getTid().getAsLong(),
                    o.getOrderStatus(),
                    o.getRefundId().getAsLong(),
                    o.getNumIid().getAsLong(),
                    o.getTitle(),
                    o.getFenxiaoId().isPresent() ? o.getFenxiaoId().getAsLong() : null,
                    o.getSupplierNick(),
                    o.getDistributorNick(),
                    o.getFenxiaoRefundStatus(),
                    o.getFenxiaoRefundFee(),
                    o.getFenxiaoPaySupFee(),
                    o.getFenxiaoRefundDesc(),
                    o.getFenxiaoRefundReason(),
                    o.getStatus(),
                    o.getRemarks(),
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


//    public static void writeWithCsvOcFenxiaoCheckResultWriter(List<OcFenxiaoCheckResult> lists, Long execJobId) throws Exception {
//
//        ICsvBeanWriter beanWriter = null;
//        try {
//            File file = new File(OrderCatConfig.getOrderCatOutPutPath() + String.format("fenxiao_check_rt_%d.csv", execJobId.intValue()));
//            if (file.exists()) {
//                FileUtils.forceDelete(file);
//            }
//
//            beanWriter = new CsvBeanWriter(new FileWriter(OrderCatConfig.getOrderCatOutPutPath() + String.format("fenxiao_check_rt_%d.csv", execJobId.intValue())),
//                    CsvPreference.STANDARD_PREFERENCE);
//
//            // the header elements are used to map the bean values to each column (names must match)
//            final String[] header = new String[]{
//                    "goodsNo",
//                    "wareHouseID",
//                    "warehouseName",
//                    "size1",
//                    "size2",
//                    "brand",
//                    "marketprice",
//                    "num2",
//                    "division",
//                    "cate",
//                    "sex",
//                    "quarter",
//                    "discount",
//                    "bdiscount",
//                    "pickRate",
//                    "updateTime",
//                    "pickDate",
//                    "thedtime",
//                    "proxyPrice",
//                    "salesPrice",
//                    "avgPrice",
//                    "salesCount",
//                    "expressName",
//                    "retrunDesc",
//                    "returnRate",
//                    "endT",
//                    "mark",
//                    "numIid",
//                    "skuId"
//            };
//
//            //final CellProcessor[] processors = getProcessors();
//
//            // write the header
//            beanWriter.writeHeader(header);
//
//            // write the beans
//            for (final OcFenxiaoCheckResult o : lists) {
//                beanWriter.write(o, header);
//            }
//
//        } finally {
//            if (beanWriter != null) {
//                beanWriter.close();
//            }
//        }
//    }

}
