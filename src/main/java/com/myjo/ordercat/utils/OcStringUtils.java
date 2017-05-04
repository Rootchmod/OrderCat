package com.myjo.ordercat.utils;

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

    public static boolean isNumeric(String str){
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

}
