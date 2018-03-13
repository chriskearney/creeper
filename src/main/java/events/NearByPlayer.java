package events;

import com.comandante.creeper.items.Item;
import com.comandante.creeper.npc.Npc;
import com.comandante.creeper.player.Player;

import java.util.List;
import java.util.Map;

public class NearByPlayer {
    private final Map<String, String> npcs;
    private final List<Item> items;
    private final Map<String, String> players;

    public NearByPlayer(Map<String, String> npcs, List<Item> items, Map<String, String> players) {
        this.npcs = npcs;
        this.items = items;
        this.players = players;
    }

    public Map<String, String> getNpcs() {
        return npcs;
    }

    public List<Item> getItems() {
        return items;
    }

    public Map<String, String> getPlayers() {
        return players;
    }
}
