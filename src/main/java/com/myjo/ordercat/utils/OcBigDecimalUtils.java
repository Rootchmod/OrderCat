package com.myjo.ordercat.utils;

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


    public static BigDecimal purchasePrice(BigDecimal proxyPrice,boolean isxl) {
        long lrt;
        if(isxl){
            lrt = Math.round(divide(proxyPrice,new BigDecimal("0.9")).add(new BigDecimal("25")).doubleValue());
        }else {
            lrt = Math.round(divide(proxyPrice,new BigDecimal("0.93")).add(new BigDecimal("25")).doubleValue());
        }
        String lrtStr = String.valueOf(lrt);
        String a = lrtStr.substring(0, lrtStr.length() - 1);
        return new BigDecimal(a+"9");
    }
}
