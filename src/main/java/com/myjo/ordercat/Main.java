package com.myjo.ordercat;

import com.myjo.ordercat.http.TaoBaoHttp;
import com.myjo.ordercat.http.TianmaSportHttp;
import com.taobao.api.TaobaoBatchRequest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by lee5hx on 17/4/19.
 */
public class Main {
    public static void main(String args[]) throws Exception {
//        PostExample pe = new PostExample();
//        pe.login();
        //27.210.139.91

          TaoBaoHttp taoBaoHttp = new TaoBaoHttp();

          taoBaoHttp.itemcatsGetRequest();
//        Map<String,String> map =  new HashMap<>();;
//
//
//
//
//        TianmaSportHttp tianmaSportHttp = new TianmaSportHttp(map);
//
//        tianmaSportHttp.getVerifyCodeImage();
//
//        System.out.print("请输入验证码:");
//        InputStreamReader stdin = new InputStreamReader(System.in);//键盘输入
//        BufferedReader bufin = new BufferedReader(stdin);
//        String str = bufin.readLine();
//
//
//        //String vc = String.valueOf(System.in.read());
//        System.out.println("你输入的验证码:"+str);
//        tianmaSportHttp.login(str);
//        tianmaSportHttp.main_html();
//        tianmaSportHttp.inventoryDownGroup("耐克","17Q1");
        //

        // FileUtils.forceDeleteOnExit(new File("/Users/lee5hx/src/myjo/OrderCat/vcode.jpg"));

    }
}
