package com.comandante.creeper.api;

import com.codahale.metrics.annotation.Timed;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.world.model.Coords;
import com.comandante.creeper.world.model.Room;
import io.dropwizard.auth.Auth;
import org.glassfish.jersey.internal.util.Base64;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api")
@Produces(MediaType.TEXT_PLAIN)
public class ApiResource {

    private final GameManager gameManager;

    public ApiResource(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @GET
    @Timed
    @PermitAll
    @Path("/map")
    public String getPrompt(@Auth Player player) {
        final Room playerCurrentRoom = gameManager.getRoomManager().getPlayerCurrentRoom(player).get();
        String map = gameManager.getMapsManager().drawMap(playerCurrentRoom.getRoomId(), new Coords(20, 15));
        return Base64.encodeAsString(map);
    }
}