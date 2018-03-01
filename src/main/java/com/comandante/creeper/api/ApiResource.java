package com.comandante.creeper.api;

import com.codahale.metrics.annotation.Timed;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.world.model.Coords;
import com.comandante.creeper.world.model.Room;
import io.dropwizard.auth.Auth;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.StringJoiner;

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
    @Path("/clientdata")
    public CreeperClientData getClientData(@Auth Player player) {
        final Room playerCurrentRoom = gameManager.getRoomManager().getPlayerCurrentRoom(player).get();
        String map = gameManager.getMapsManager().drawMap(playerCurrentRoom.getRoomId(), new Coords(20, 14));

        String prompt = gameManager.buildPrompt(player.getPlayerId());

        String lookString = player.getLookString();

        List<String> rolledUpInventory = player.getRolledUpInventory();
        StringJoiner stringJoiner = new StringJoiner("\n");
        for (String s: rolledUpInventory) {
            stringJoiner.add(s);
        }
        String inventory = stringJoiner.toString();

        List<String> recent = gameManager.getGossipCache().getRecent(15);
        StringJoiner gossipJoiner = new StringJoiner("\n");
        for (String s: recent) {
            gossipJoiner.add(s);
        }
        String gossip = gossipJoiner.toString();

        return new CreeperClientData(map, prompt, lookString, inventory, gossip);

    }
}