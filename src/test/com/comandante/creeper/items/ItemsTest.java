package com.comandante.creeper.items;

import com.comandante.creeper.server.player_communication.Color;
import com.comandante.creeper.stats.StatsBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.Optional;

public class ItemsTest {

    @Test
    public void testEffectsOnUseWeapon() throws Exception {

        Optional<ItemMetadata> flameThrowerOptional = ItemTestingUtility.getMetadataFromFile("flame thrower");
        ItemMetadata flameThrower = flameThrowerOptional.get();

        Map<Double, Effect> attackEffects = flameThrower.getAttackEffects();

        Assert.assertNotNull(attackEffects);

    }
}
