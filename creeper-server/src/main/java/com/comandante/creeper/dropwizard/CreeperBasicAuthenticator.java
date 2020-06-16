package com.comandante.creeper.dropwizard;

import com.comandante.creeper.Creeper;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.player.PlayerManager;
import com.comandante.creeper.player.PlayerMetadata;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

import java.util.Optional;

public class CreeperBasicAuthenticator implements Authenticator<BasicCredentials, Player> {

    PlayerManager playerManager;

    public CreeperBasicAuthenticator(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public Optional<Player> authenticate(BasicCredentials basicCredentials) throws AuthenticationException {
        String password = basicCredentials.getPassword();
        String username = basicCredentials.getUsername();

        Optional<PlayerMetadata> playerMetadataOptional = playerManager.getPlayerMetadata(Creeper.createPlayerId(username));
        if (!playerMetadataOptional.isPresent()) {
            return Optional.empty();
        }
        PlayerMetadata playerMetadata = playerMetadataOptional.get();
        if (!playerMetadata.getPassword().equals(password)) {
            return Optional.empty();
        }

        return Optional.ofNullable(playerManager.getPlayer(Creeper.createPlayerId(username)));
    }
}
