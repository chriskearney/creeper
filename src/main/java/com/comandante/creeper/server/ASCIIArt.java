package com.comandante.creeper.server;

import org.apache.commons.lang3.text.WordUtils;

public class ASCIIArt {

    public static Integer GLOBAL_TERMINAL_WIDTH = 80;

    public static String VERTICAL_SWORD_WITH_EMITTING_ORA = "        )         \n" +
            "          (            \n" +
            "        '    }      \n" +
            "      (    '      \n" +
            "     '      (   \n" +
            "      )  |    ) \n" +
            "    '   /|\\    `\n" +
            "   )   / | \\  ` )   \n" +
            "  {    | | |  {   \n" +
            " }     | | |  .\n" +
            "  '    | | |    )\n" +
            " (    /| | |\\    .\n" +
            "  .  / | | | \\  (\n" +
            "}    \\ \\ | / /  .        \n" +
            " (    \\ `-' /    }\n" +
            " '    / ,-. \\    ' \n" +
            "  }  / / | \\ \\  }\n" +
            " '   \\ | | | /   } \n" +
            "  (   \\| | |/  (\n" +
            "    )  | | |  )\n" +
            "    .  | | |  '\n" +
            "       J | L\n" +
            " /|    J_|_L    |\\\n" +
            " \\ \\___/ o \\___/ /\n" +
            "  \\_____ _ _____/\n" +
            "        |-|\n" +
            "        |-|\n" +
            "        |-|\n" +
            "       ,'-'.\n" +
            "       '---'";

    public static String wrap(String s) {
       return WordUtils.wrap(s, GLOBAL_TERMINAL_WIDTH, "\r\n", true);
    }

    static String centerOnWidth(String str, int size, String repeatedChar) {
        int left = (size - str.length()) / 2;
        int right = size - left - str.length();
        StringBuffer buff = new StringBuffer();
        for (int i = 0; i < left; i++) {
            buff.append(repeatedChar);
        }
        buff.append(str);
        for (int i = 0; i < right; i++) {
            buff.append(repeatedChar);
        }
        return buff.toString();
    }
}
