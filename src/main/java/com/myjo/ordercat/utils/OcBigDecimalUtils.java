package com.myjo.ordercat.utils;

import com.myjo.ordercat.config.OrderCatConfig;
import com.myjo.ordercat.domain.SalesPriceCalculate;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by lee5hx on 17/4/28.
 */
public class OcBigDecimalUtils {

    // 计算过程中除法的浮点精度
    public static int ROUND = 10;
    // 最终结果的浮点精度
    //private static int LASTROUND = 10;
    //public static BigDecimal PER = BigDecimal.valueOf(100);

    /**
     * 除法
     *
     * @param data
     * @param div
     * @return
     */
    public static BigDecimal divide(BigDecimal data, BigDecimal div) {
        return data.divide(div, ROUND, RoundingMode.HALF_UP);
    }

    /**
     * 获得销售价格
     * @param proxyPrice
     * @param isxl 大于20 = True 或小于20 = False
     * @return
     */
    public static BigDecimal toSalesPrice(BigDecimal proxyPrice, boolean isxl) {
        long lrt;
        SalesPriceCalculate spc;
        if(isxl){
            spc = OrderCatConfig.getSalesPriceGtCalculate();
            lrt = Math.round(divide(proxyPrice,new BigDecimal(spc.getDivide())).add(new BigDecimal(spc.getAdd())).doubleValue());
        }else {
            spc = OrderCatConfig.getSalesPriceLtCalculate();
            lrt = Math.round(divide(proxyPrice,new BigDecimal(spc.getDivide())).add(new BigDecimal(spc.getAdd())).doubleValue());
        }
        String lrtStr = String.valueOf(lrt);
        String a = lrtStr.substring(0, lrtStr.length() - 1);
        return new BigDecimal(a+OrderCatConfig.getSalesPriceEndReplace());
    }



}
