package com.myjo.ordercat.utils;

import com.myjo.ordercat.spm.ordercat.ordercat.oc_logistics_companies_info.OcLogisticsCompaniesInfo;

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
}
