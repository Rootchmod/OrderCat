package com.myjo.ordercat.rest.api;

import com.aol.micro.server.auto.discovery.Rest;

import javax.ws.rs.*;

@Rest
@Path("/status")
public class StatusResource {
    @GET
    @Produces("text/plain")
    @HeaderParam("")
    @Path("/ping")
    public String ping() {
        return "ok";
    }

}