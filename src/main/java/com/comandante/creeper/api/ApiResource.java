package com.comandante.creeper.api;

import com.codahale.metrics.annotation.Timed;
import com.comandante.creeper.player.Player;
import io.dropwizard.auth.Auth;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
public class ApiResource {
    public ApiResource() {

    }

    @GET
    @Timed
    @PermitAll
    public void getMap(@Auth Player player) {
        System.out.println(player.getPlayerName());
    }
}