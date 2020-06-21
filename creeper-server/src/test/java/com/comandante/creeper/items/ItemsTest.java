package com.comandante.creeper.items;

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
