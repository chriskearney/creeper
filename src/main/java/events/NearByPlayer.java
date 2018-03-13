package events;

import com.comandante.creeper.items.Item;
import com.comandante.creeper.npc.Npc;
import com.comandante.creeper.player.Player;

import java.util.List;
import java.util.Map;

public class NearByPlayer {
    private final List<Npc> npc;
    private final List<Item> items;
    private final Map<String, String> players;

    public NearByPlayer(List<Npc> npc, List<Item> items, Map<String, String> players) {
        this.npc = npc;
        this.items = items;
        this.players = players;
    }

    public List<Npc> getNpc() {
        return npc;
    }

    public List<Item> getItems() {
        return items;
    }

    public Map<String, String> getPlayers() {
        return players;
    }
}
