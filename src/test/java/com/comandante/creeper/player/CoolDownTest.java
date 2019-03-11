package com.comandante.creeper.player;

import com.google.gson.GsonBuilder;
import org.junit.Test;

public class CoolDownTest {


    @Test
    public void testSerialization() throws Exception {


        CoolDown coolDown = new CoolDown(CoolDownType.DETAINMENT);

        String s = new GsonBuilder().create().toJson(coolDown, CoolDown.class);

        System.out.println(s);


    }

}