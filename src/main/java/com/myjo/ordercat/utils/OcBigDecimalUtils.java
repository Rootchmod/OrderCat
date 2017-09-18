package com.myjo.ordercat.utils;

import com.myjo.ordercat.config.OrderCatConfig;
import com.myjo.ordercat.domain.SalesPriceCalculatePolicy;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
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
     *
     * @param e
     * @param proxyPrice
     * @param equation
     * @return
     */
    public static BigDecimal toSalesPrice(ScriptEngine e, BigDecimal proxyPrice, String equation) {

        BigDecimal salasPrice = null;
        try {
            //e.put("proxyPrice", proxyPrice.doubleValue());
            //e.eval("var i = Math.round("+equation+");");//proxyPrice/0.9+25
           // String ps = "proxyPrice";
            String ps = "Math.round("+equation+");";

            String evalStr = ps.replaceAll("proxyPrice",proxyPrice.toPlainString());
            Object ddd  = e.eval(evalStr);
            //Object ddd = e.get("i");
            Double rt = (double)ddd;
            String lrtStr = String.valueOf(rt.longValue());

            String a = lrtStr.substring(0, lrtStr.length() - 1);
            salasPrice = new BigDecimal(a+OrderCatConfig.getSalesPriceEndReplace());
        } catch (final ScriptException se) {
            se.printStackTrace();
        }

        return salasPrice;
    }



    public static boolean salesLimitCountJudge(ScriptEngine e, Long salesCount, String judge) {
        boolean rt = false;
        try {
            String ps = "("+judge+")";
            String evalStr = ps.replaceAll("salesCount",String.valueOf(salesCount.longValue()));
            rt = ((Boolean) e.eval(evalStr)).booleanValue();
        } catch (final ScriptException se) {
            se.printStackTrace();
        }
        return rt;
    }


    public static BigDecimal toBreakEvenPrice(ScriptEngine e, BigDecimal payAmount,String ps) {

        BigDecimal breakEvenPrice = null;
        try {
            //String ps = OrderCatConfig.getBreakEvenPricePolicyEquation();
            String evalStr = ps.replaceAll("payAmount",payAmount.toPlainString());
            Object ddd  = e.eval(evalStr);
            Double rt = (double)ddd;
            breakEvenPrice = new BigDecimal(rt);
        } catch (final ScriptException se) {
            se.printStackTrace();
        }
        return breakEvenPrice;
    }

    public static boolean pickWhcountCalculatePolicyJudge(ScriptEngine e, String x,String y,String z, String judge) {
        boolean rt = false;
        try {
            String ps = "("+judge+")";
            String evalStr = ps.replaceAll("x",String.valueOf(x));
            evalStr = evalStr.replaceAll("y",String.valueOf(y));
            evalStr = evalStr.replaceAll("z",String.valueOf(z));
            rt = ((Boolean) e.eval(evalStr)).booleanValue();
        } catch (final ScriptException se) {
            se.printStackTrace();
        }
        return rt;
    }




}
