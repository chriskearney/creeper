package com.comandante.creeper.command.commands;

import com.comandante.creeper.common.FriendlyTime;
import org.junit.Test;


public class FriendlyTimeTest {

    @Test
    public void testFriendlyParsing() throws Exception {

        FriendlyTime friendlyTime = new FriendlyTime(400);

        System.out.println("Friendly Long: " + friendlyTime.getFriendlyFormatted());

        System.out.println("Friendly Short: " + friendlyTime.getFriendlyFormattedShort());
    }

}