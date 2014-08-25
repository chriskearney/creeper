package com.comandante.creeper.server;

import com.comandante.creeper.managers.PlayerManager;
import com.comandante.creeper.managers.RoomManager;
import com.comandante.creeper.model.Player;
import com.comandante.creeper.model.Room;

public class ChannelUtils {

    private final PlayerManager playerManager;
    private final RoomManager roomManager;

    public ChannelUtils(PlayerManager playerManager, RoomManager roomManager) {
        this.playerManager = playerManager;
        this.roomManager = roomManager;
    }

    public void write(String playerId, String message) {
        Player player = playerManager.getPlayer(playerId);
        Room playerCurrentRoom = roomManager.getPlayerCurrentRoom(player).get();
        StringBuilder sb = new StringBuilder();
        sb.append("\r\n");
        sb.append(sanitze(message));
        sb.append(("\r\n"));
        sb.append(playerManager.getPrompt(playerId, playerCurrentRoom.getRoomId()));
        player.getChannel().write(sb.toString());
    }

    public static String sanitze(String msg) {
        byte[] data = msg.getBytes();
        byte groomedData[] = new byte[data.length];
        int bytesCopied = 0;

        for (int i = 0; i < data.length; i++) {
            switch (data[i]) {
                case (byte) '\n' :
                    if (i == 0 || i == 1 ) {
                        break;
                    }
                    if (i == data.length - 1 || i == data.length - 2 ) {
                        break;
                    }
                case (byte) '\r' :
                    if (i == 0 || i == 1 ) {
                        break;
                    }
                    if (i == data.length - 1 || i == data.length - 2) {
                        break;
                    }
                default:
                    groomedData[bytesCopied++] = data[i];
            }
        }

        byte packedData[] = new byte[bytesCopied];

        System.arraycopy(groomedData, 0, packedData, 0, bytesCopied);

        return new String(packedData);
    }
}

