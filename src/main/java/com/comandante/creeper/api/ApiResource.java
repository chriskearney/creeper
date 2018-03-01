package com.comandante.creeper.api;

import com.codahale.metrics.annotation.Timed;
import com.comandante.creeper.core_game.GameManager;
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

    private final GameManager gameManager;

    public ApiResource(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @GET
    @Timed
    @PermitAll
    @Path("/prompt")
    public CreeperApiStringResponse getPrompt(@Auth Player player) {
        String buildPrompt = gameManager.buildPrompt(player.getPlayerId());
        return new CreeperApiStringResponse(buildPrompt);
    }
}