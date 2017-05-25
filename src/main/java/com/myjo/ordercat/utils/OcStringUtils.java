package com.myjo.ordercat.utils;

import com.myjo.ordercat.domain.InventoryInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lee5hx on 17/5/3.
 */
public class OcStringUtils {

//    private static double size12double(String size1){
//        Double.valueOf(inventoryInfo.getSize1())
//    }
    private static final Pattern pattern = Pattern.compile("-?[0-9]+.?[0-9]+");



    public static boolean isPatternMatcher(String pattern,String str){
        Matcher isNum = Pattern.compile(pattern).matcher(str);
        if(isNum.matches()){
            return true;
        }
        return false;
    }


    public static boolean isNumeric(String str){
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }


    public static boolean isCssNumeric(InventoryInfo inventoryInfo){
        boolean rt;
        if(inventoryInfo.getDivision().equals("鞋")){
            if(isNumeric(inventoryInfo.getSize1())){
                rt = true;
            }else {
                rt = false;
            }
        }else {
            rt = true;
        }
        return rt;
    }

    public static String getGoodsNoByOuterId(String skuOuterId){
        return StringUtils.substringBeforeLast(skuOuterId, "-");
    }

}
