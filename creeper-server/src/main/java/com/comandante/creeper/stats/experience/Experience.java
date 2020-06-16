package com.comandante.creeper.stats.experience;

import com.comandante.creeper.npc.Npc;

public class Experience {

    public static int calculateNpcXp(int playerLevel, int npcLevel) {
        float xp = 0;
        if (npcLevel == playerLevel) {
            xp = (playerLevel * 5 + 45);
        }
        if (npcLevel > playerLevel) {
            float i = playerLevel * 5 + 45;
            float diff = npcLevel - playerLevel;
            float modifier = 1 + .2f * (diff);
            xp = i * modifier;
        }
        if (npcLevel < playerLevel) {
            if (getLevelColor(playerLevel, npcLevel).equals(Npc.NpcLevelColor.WHITE)) {
                xp = 0;
            } else {
                float levelDif = playerLevel - npcLevel;
                xp = (playerLevel * 5 + 45) * (1 - (levelDif / getZD(playerLevel)));
            }
        }
        if (xp == 0) {
            return 0;
        }
        return (int) Math.floor(xp + 0.5);
    }


    public static Npc.NpcLevelColor getLevelColor(int playerLevel, int npcLevel) {
        if (playerLevel + 5 <= npcLevel) {
            return Npc.NpcLevelColor.RED;
        } else {
            switch (npcLevel - playerLevel) {
                case 4:
                case 3:
                    return Npc.NpcLevelColor.ORANGE;
                case 2:
                case 1:
                case 0:
                case -1:
                case -2:
                    return Npc.NpcLevelColor.YELLOW;
                default:
                    if (playerLevel <= 5) {
                        return Npc.NpcLevelColor.GREEN;
                    } else {
                        if (playerLevel <= 50) {
                            if (npcLevel <= (playerLevel - 5 - Math.floor(playerLevel / 10))) {
                                return Npc.NpcLevelColor.WHITE;
                            } else {
                                return Npc.NpcLevelColor.GREEN;
                            }
                        } else {
                            // Player is over level 50
                            if (npcLevel <= (playerLevel - 1 - Math.floor(playerLevel / 5))) {
                                return Npc.NpcLevelColor.WHITE;
                            } else {
                                return Npc.NpcLevelColor.GREEN;
                            }
                        }
                    }
            }
        }
    }

    private static int getZD(int lvl) {
        if (lvl <= 7) {
            return 5;
        }
        if (lvl <= 9) {
            return 6;
        }
        if (lvl <= 11) {
            return 7;
        }
        if (lvl <= 15) {
            return 8;
        }
        if (lvl <= 19) {
            return 9;
        }
        if (lvl <= 29) {
            return 11;
        }
        if (lvl <= 39) {
            return 12;
        }
        if (lvl <= 49) {
            return 13;
        }
        if (lvl <= 59) {
            return 14;
        }
        if (lvl <= 69) {
            return 15;
        }
        if (lvl <= 79) {
            return 16;
        }
        if (lvl <= 89) {
            return 17;
        }
        if (lvl <= 99) {
            return 18;
        } else {
            return 19;
        }
    }

}
