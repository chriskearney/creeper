package com.comandante.creeper.cclient;

import java.util.Optional;

public interface NearMeHandler {

    void attack(String entityId);

    void look(Optional<String> npcId, Optional<String> playerId);

    void pick(String itemId);

    void compare(String playerId);

    void talk(String target);
}
