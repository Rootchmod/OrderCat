package com.myjo.ordercat.rest.api;

import com.aol.micro.server.auto.discovery.Rest;
import com.myjo.ordercat.config.OrderCatConfig;
import com.myjo.ordercat.context.OrderCatContext;
import com.myjo.ordercat.http.TaoBaoHttp;
import com.myjo.ordercat.rest.api.jwt.JwtTokenUtil;
import com.myjo.ordercat.rest.api.jwt.JwtUser;
import com.taobao.api.domain.Trade;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Rest
@Path("/sinfo")
@Api(value = "/sinfo", description = "认证")
public class GetInfoResource {



    private static final Logger Logger = LogManager.getLogger(GetInfoResource.class);

    @GET
    @Produces("application/json;charset=utf-8")
    @Path("/refund")
    @ApiOperation(value = "refund", response = String.class)
    public Map<String, Object> refund(@ApiParam(required = true, name = "orderId", value = "订单编号") @QueryParam("orderId") String orderId) {


        Map<String, Object> rt = new HashMap<>();
        TaoBaoHttp taobaoHttp = OrderCatContext.getTaoBaoHttp();


        Optional<Trade> info = taobaoHttp.getTaobaoTradeFullInfo(Long.valueOf(orderId));


        if(info.isPresent()){

        }

        rt.put("success", "测试服务器-通信");
        rt.put("message", "测试服务器-通信");
        return rt;
    }


}