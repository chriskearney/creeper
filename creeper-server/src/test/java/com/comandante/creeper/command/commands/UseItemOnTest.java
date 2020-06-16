package com.comandante.creeper.command.commands;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class UseItemOnTest {

    @Test
    public void testUseItemOnParseWithNoTargetSpecified() {
        String itemTrigger = "a spell book wow.";
        String fullCommand = "use " + itemTrigger;
        List<String> originalMessageParts = Command.getOriginalMessageParts(fullCommand);
        UseCommand.UseItemOn useItemOn = new UseCommand.UseItemOn(originalMessageParts);
        Assert.assertTrue(itemTrigger.equals(useItemOn.getItem()));
    }

    @Test
    public void testUseItemOnParseWithNoTargetSpecifiedButOnIs() {
        String itemTrigger = "a spell book wow.";
        String fullCommand = "use " + itemTrigger + " on   ";
        List<String> originalMessageParts = Command.getOriginalMessageParts(fullCommand);
        UseCommand.UseItemOn useItemOn = new UseCommand.UseItemOn(originalMessageParts);
        Assert.assertTrue(itemTrigger.equals(useItemOn.getItem()));
    }

    @Test
    public void testUseItemOnParseWithTargetSpecifiedHasOnInName() {
        String itemTrigger = "a spell book wow.";
        String itemTarget = "BONONONIGBOGCHRNGVONJAONL";
        String fullCommand = "use " + itemTrigger + " on " + itemTarget;
        List<String> originalMessageParts = Command.getOriginalMessageParts(fullCommand);
        UseCommand.UseItemOn useItemOn = new UseCommand.UseItemOn(originalMessageParts);
        Assert.assertTrue(itemTrigger.equals(useItemOn.getItem()));
        Assert.assertTrue(itemTarget.equals(useItemOn.getTarget().get()));
    }

    @Test
    public void testUseItemOnParseWithTargetSpecifiedRandomCasing() {
        String itemTrigger = "a spell book wow.";
        String itemTarget = "big bad wolf";
        String fullCommand = "use " + itemTrigger + " oN " + itemTarget;
        List<String> originalMessageParts = Command.getOriginalMessageParts(fullCommand);
        UseCommand.UseItemOn useItemOn = new UseCommand.UseItemOn(originalMessageParts);
        Assert.assertTrue(itemTrigger.equals(useItemOn.getItem()));
        Assert.assertTrue(itemTarget.equals(useItemOn.getTarget().get()));
    }

}