package com.myjo.ordercat.rest.api;

import com.aol.micro.server.auto.discovery.Rest;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;

@Rest
@Path("/status")
@Api(value = "/status", description = "查看状态是否可用")
public class StatusResource {
    @GET
    @Produces("text/plain")
    @Path("/ping")
    @ApiOperation(value = "Make a ping call", response = String.class)
    public String ping() {
        return "ok";
    }

}