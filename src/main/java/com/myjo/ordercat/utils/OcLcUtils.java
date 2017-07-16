package com.myjo.ordercat.utils;

import com.myjo.ordercat.spm.ordercat.ordercat.oc_logistics_companies_info.OcLogisticsCompaniesInfo;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lee5hx on 17/5/14.
 */
public class OcLcUtils {


    public static List<String> getLogisticsCompaniesCode(List<OcLogisticsCompaniesInfo> oclcList,
                                                         String deliveryNo){
        List<String> codeList = new ArrayList<>();
        for(OcLogisticsCompaniesInfo olci :oclcList){
            if(OcStringUtils.isPatternMatcher(olci.getLcRegMailNo().get(),deliveryNo)){
                codeList.add(olci.getLcCode().get());
            }
        }
        return codeList;
    }

    public static BigDecimal getPickRate(String str){
        String dd1 = StringUtils.substringBeforeLast(str, "%");
        BigDecimal x = new BigDecimal(dd1.replaceAll("配货率：", ""));
        return x;
    }



}
