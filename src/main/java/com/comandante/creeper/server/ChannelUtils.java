package com.comandante.creeper.server;

import com.comandante.creeper.player.Player;
import com.comandante.creeper.player.PlayerManager;
import com.comandante.creeper.world.Room;
import com.comandante.creeper.world.RoomManager;

import java.util.Set;

public class ChannelUtils {

    private final PlayerManager playerManager;
    private final RoomManager roomManager;

    public ChannelUtils(PlayerManager playerManager, RoomManager roomManager) {
        this.playerManager = playerManager;
        this.roomManager = roomManager;
    }

    public void writeNoPrompt(String playerId, String message) {
        if (playerManager.getSessionManager().getSession(playerId).getGrabMultiLineInput().isPresent()) {
            return;
        }
        write(playerId, message, false, true);
    }

    public void writeNoPromptNoAfterSpace(String playerId, String message) {
        if (playerManager.getSessionManager().getSession(playerId).getGrabMultiLineInput().isPresent()) {
            return;
        }
        write(playerId, message, false, false);
    }

    public void writeOnlyPrompt(String playerId) {
        if (playerManager.getSessionManager().getSession(playerId).getGrabMultiLineInput().isPresent()) {
            return;
        }
        Player player = playerManager.getPlayer(playerId);
        Room playerCurrentRoom = roomManager.getPlayerCurrentRoom(player).get();
        player.getChannel().write(playerManager.getPrompt(playerId, playerCurrentRoom.getRoomId()));
    }

    public void writeToRoom(String playerId, String message) {
        if (playerManager.getSessionManager().getSession(playerId).getGrabMultiLineInput().isPresent()) {
            return;
        }
        Room playerCurrentRoom = roomManager.getPlayerCurrentRoom(playerManager.getPlayer(playerId)).get();
        Set<String> presentPlayerIds = playerCurrentRoom.getPresentPlayerIds();
        for (String id : presentPlayerIds) {
            Player presentPlayer = playerManager.getPlayer(id);
       //     if (presentPlayer.getPlayerId().equals(playerId)) {
        //        write(playerId, message);
         //   } else {
                writeNoPrompt(presentPlayer.getPlayerId(), message);
            }
        }


    public void write(String playerId, String message) {
        if (playerManager.getSessionManager().getSession(playerId).getGrabMultiLineInput().isPresent()) {
            return;
        }
        write(playerId, message, true, true);
    }

    public void write(String playerId, String message, boolean isPrompt, boolean isAfterSpace) {
        if (playerManager.getSessionManager().getSession(playerId).getGrabMultiLineInput().isPresent()) {
            return;
        }
        Player player = playerManager.getPlayer(playerId);
        Room playerCurrentRoom = roomManager.getPlayerCurrentRoom(player).get();
        StringBuilder sb = new StringBuilder();
        sb.append("\r\n");
        sb.append(sanitze(message));
        if (isAfterSpace) {
            sb.append(("\r\n"));
        }
        if (isPrompt) {
            sb.append(playerManager.getPrompt(playerId, playerCurrentRoom.getRoomId()));
        }
        player.getChannel().write(sb.toString());
    }

    public static String sanitze(String msg) {
        byte[] data = msg.getBytes();
        byte groomedData[] = new byte[data.length];
        int bytesCopied = 0;

        for (int i = 0; i < data.length; i++) {
            switch (data[i]) {
                case (byte) '\n':
                    if (i == 0 || i == 1) {
                        break;
                    }
                    if (i == data.length - 1 || i == data.length - 2) {
                        break;
                    }
                case (byte) '\r':
                    if (i == 0 || i == 1) {
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

