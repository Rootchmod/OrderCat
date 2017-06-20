package com.myjo.ordercat.rest.api;

import com.aol.micro.server.auto.discovery.Rest;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Created by lee5hx on 17/6/19.
 */

@Rest
@Path("/common")
public class CommonResource {
    @GET
    @Produces("text/plain")
    @HeaderParam("")
    @Path("/ping")
    public String ping() {
        return "ok";
    }
}
