package com.comandante.creeper.player;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class QuestTest {


    @Test
    public void testSerialization() throws Exception {

        Quest.ItemsAmount aloe = new Quest.ItemsAmount(1, "aloe vera");
        Quest.Critera critera = new Quest.Critera(Lists.newArrayList(aloe));

        Quest.ItemsAmount aegirs_greataxe = new Quest.ItemsAmount(2, "Aegirs greataxe");
        Quest.Reward reward = new Quest.Reward(Lists.newArrayList(aegirs_greataxe), 1000);

        Quest aloe_quest = new Quest("aloe quest", critera, reward, "My daughter sufered a wicked burn and I'm in desperate need of aloe vera.", "My daughter's wounds can be healed! Thank you!! Here have this reward: ");

        ObjectMapper objectMapper = new ObjectMapper();
        String questJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(aloe_quest);

        System.out.println(questJson);


    }



}