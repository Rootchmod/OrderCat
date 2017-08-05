package com.myjo.ordercat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lee5hx on 2017/8/5.
 */

@Configuration
public class EbayCorsConfig {



//    resp.addHeader("Access-Control-Allow-Origin", "*");
//		resp.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
//		resp.addHeader("Access-Control-Allow-Headers", "X-Requested-With, Content-Type, X-Codingpedia");

    @Bean(name="ebay-cors-config")
    public Map<String,String> getConfig(){
        Map<String,String> map = new HashMap<>();
        map.put("cors.allowed.origins","*");
        map.put("cors.allowed.methods","GET, POST, DELETE, PUT");
        map.put("cors.allowed.headers","X-Requested-With, Content-Type, X-Codingpedia, token");
        return map;

    }
}
