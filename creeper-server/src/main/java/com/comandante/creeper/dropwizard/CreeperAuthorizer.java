package com.comandante.creeper.dropwizard;

import com.comandante.creeper.player.Player;
import io.dropwizard.auth.Authorizer;

public class CreeperAuthorizer implements Authorizer<Player> {
    @Override
    public boolean authorize(Player player, String role) {
        return player.getName().equals("good-guy") && role.equals("ADMIN");
    }
}