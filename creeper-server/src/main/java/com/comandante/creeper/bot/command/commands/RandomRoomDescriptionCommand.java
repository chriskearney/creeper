package com.comandante.creeper.bot.command.commands;


import com.comandante.creeper.bot.command.BotCommandManager;
import com.comandante.creeper.world.model.Room;
import com.google.api.client.util.Lists;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class RandomRoomDescriptionCommand extends BotCommand {

    static Set<String> triggers = Sets.newHashSet("blazecraft");
    static String helpUsage = "blazecraft";
    static String helpDescription = "A random room description, courtesy of BLAZECRAFT";
    private final Random random = new Random();

    public RandomRoomDescriptionCommand(BotCommandManager botCommandManager) {
        super(botCommandManager, triggers, helpUsage, helpDescription);
    }

    @Override
    public List<String> process() {
        Map<Integer, Room> getrooms = botCommandManager.getGameManager().getRoomManager().getrooms();
        int size = getrooms.size();
        int randomRoomNumber = randInt(1, size);
        Room randomRoom = getrooms.get(randomRoomNumber);
        ArrayList<String> output = Lists.newArrayList();
        output.add(randomRoom.getRoomTitle());
        output.add(" ");
        output.add(randomRoom.getRoomDescription());
        /*String mapString = botCommandManager.getGameManager().getMapsManager().drawMap(randomRoom.getRoomId(), new Coords(5, 5));
        String[] split = mapString.split("\\r?\\n");
        for (String s: split) {
            output.add(s);
        }*/
        return output;
    }

    private int randInt(int min, int max) {
        int randomNum = random.nextInt((max - min) + 1) + min;
        return randomNum;
    }
}
