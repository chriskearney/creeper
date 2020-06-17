package com.comandante.creeper.world;

import com.codahale.metrics.Timer;
import com.comandante.creeper.Creeper;

import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.dropwizard.CreeperConfiguration;
import com.comandante.creeper.server.player_communication.Color;
import com.comandante.creeper.world.model.Coords;
import com.comandante.creeper.world.model.Room;
import com.google.common.collect.Maps;
import events.ListenerService;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import static com.codahale.metrics.MetricRegistry.name;

public class MapsManager {

    private final RoomManager roomManager;
    private final ListenerService listenerService;
    private final Map<Integer, MapMatrix> floorMatrixMaps;
    private final CreeperConfiguration creeperConfiguration;
    private final ExecutorService mapGeneratorService = Executors.newFixedThreadPool(1);
    private static final Logger log = Logger.getLogger(GameManager.class);
    private final Timer ticktime = Creeper.metrics.timer(name(MapsManager.class, "generate_all_maps_time"));

    public MapsManager(CreeperConfiguration creeperConfiguration, RoomManager roomManager, ListenerService listenerService) {
        this.roomManager = roomManager;
        this.listenerService = listenerService;
        this.floorMatrixMaps = Maps.newHashMap();
        this.creeperConfiguration = creeperConfiguration;
    }

    private void generate() {
        Timer.Context time = ticktime.time();
        int maxRows = creeperConfiguration.getDefaultMapSize();
        int maxColumns = creeperConfiguration.getDefaultMapSize();
        Iterator<Map.Entry<Integer, Room>> rooms = roomManager.getRoomsIterator();
        while (rooms.hasNext()) {
            Map.Entry<Integer, Room> next = rooms.next();
            Integer roomId = next.getValue().getRoomId();
            try {
                String s = drawMap(roomId, new Coords(maxRows, maxColumns));
                next.getValue().setMapData(Optional.of(s));
            } catch (Exception e) {
                log.error("Unable to generate room map for roomId: " + roomId);
            }
        }
        time.stop();
    }

    public void generateAllMaps() {
        mapGeneratorService.submit(new MapGeneration());
    }

    public String drawMap(Integer roomId, Coords max) {
            MapMatrix floorMatrix = floorMatrixMaps.get(roomManager.getRoom(roomId).getFloorId());
            MapMatrix mapMatrix = floorMatrix.extractMatrix(roomId, max);
            return mapMatrix.renderMap(roomId, roomManager);
    }

    public static Function<Integer, String> render(final Integer currentroomId, final RoomManager roomManager) {
        return roomId -> {
            Room room = roomManager.getRoom(roomId);
            if (roomId > 0) {
                if (roomId.equals(currentroomId)) {
                    return "[" + Color.BOLD_ON + Color.RED + "*" + Color.RESET + "]";
                } else if (roomId.equals(1)) {
                    return "[" + Color.BOLD_ON + Color.BLUE + "L" + Color.RESET + "]";
                } else if (room.getMerchants().size() > 0) {
                    return "[" + Color.YELLOW + "m" + Color.RESET + "]";
                }  else if (room.getEnterExits().size() > 0) {
                    return "[" + Color.CYAN + "e" + Color.RESET + "]";
                } else if (room.getUpId().isPresent()) {
                    return "[" + Color.GREEN + "^" + Color.RESET + "]";
                } else if (room.getDownId().isPresent()) {
                    return "[" + Color.GREEN + "v" + Color.RESET + "]";
                } else {
                    return "[ ]";
                }
            } else {
                return "   ";
            }
        };
    }

    public void addFloorMatrix(Integer id, MapMatrix floorMatrix) {
        floorMatrixMaps.put(id, floorMatrix);
    }

    public Map<Integer, MapMatrix> getFloorMatrixMaps() {
        return floorMatrixMaps;
    }

    class MapGeneration implements Runnable {
        @Override
        public void run() {
            try {
                generate();
            } catch (Exception e) {
                log.error("Unable to generate map data!", e);
            }
        }
    }
}