package com.comandante.creeper.player;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Test;

public class QuestTest {


    @Test
    public void testSerialization() throws Exception {

        Quest.ItemsAmount aloe = new Quest.ItemsAmount(1, "aloe vera");
        Quest.Critera critera = new Quest.Critera(Lists.newArrayList(aloe));

        Quest.ItemsAmount aegirs_greataxe = new Quest.ItemsAmount(2, "Aegirs greataxe");
        Quest.Reward reward = new Quest.Reward(Lists.newArrayList(aegirs_greataxe), 1000, 6000);

        Quest aloe_quest = new Quest("aloe quest", critera, reward, "My daughter sufered a wicked burn and I'm in desperate need of aloe vera.", "My daughter's wounds can be healed! Thank you!! Here have this reward: ", 1, Sets.newHashSet(PlayerClass.WARRIOR));

        ObjectMapper objectMapper = new ObjectMapper();
        String questJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(aloe_quest);

        Quest quest = objectMapper.readValue(questJson, Quest.class);

        System.out.println(questJson);


    }



}