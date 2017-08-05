package com.myjo.ordercat.rest.api;

import com.aol.micro.server.auto.discovery.Rest;
import com.myjo.ordercat.config.OrderCatConfig;
import com.myjo.ordercat.rest.api.jwt.JwtTokenUtil;
import com.myjo.ordercat.rest.api.jwt.JwtUser;
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
@Path("/auth")
@Api(value = "/auth", description = "认证")
public class AuthResource {



    private static final Logger Logger = LogManager.getLogger(AuthResource.class);

    @POST
    @Produces("application/json;charset=utf-8")
    @Path("/login")
    @ApiOperation(value = "Login", response = String.class)
    public Map<String, Object> login(@ApiParam(required = true, name = "userName", value = "用户名") @FormParam("userName") String userName,
                                     @ApiParam(required = true, name = "passWord", value = "密码") @FormParam("passWord") String passWord) {

        boolean success;
        String message;
        Map<String, Object> rt = new HashMap<>();
        JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();
        List<JwtUser> list = OrderCatConfig.getOrderCatUsers();
        Optional<JwtUser> opJwtUser = list.parallelStream().filter(user -> user.getUsername().equals(userName)).findFirst();
        if (opJwtUser.isPresent()) {
            JwtUser user = opJwtUser.get();
            if (user.getPassword().equals(passWord)) {
                success = true;
                message = jwtTokenUtil.generateToken(user);
            } else {
                success = false;
                message = "对不起!用户名或密码错误!";

            }
        } else {
            success = false;
            message = "对不起!用户名或密码错误!";
        }
        rt.put("success", success);
        rt.put("message", message);
        return rt;
    }


    @POST
    @Produces("application/json;charset=utf-8")
    @Path("/testToken")
    @ApiOperation(value = "testToken", response = String.class)
    public Map<String, Object> testToken(@HeaderParam("token") String token) {
        Logger.info("testToken:"+token);
        Map<String, Object> rt = new HashMap<>();
        boolean success;
        Object message;
        JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();
        Optional<JwtUser> user = jwtTokenUtil.validateToken(token);
        if (user.isPresent()) {
            success = true;
            JwtUser user1 = user.get();
            user1.setPassword("");
            message = user1;
        } else {
            success = false;
            message = "对不起!您的会话已经失效!请重新登陆!";

        }
        rt.put("success", success);
        rt.put("message", message);
        return rt;
    }


}