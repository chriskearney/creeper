package com.comandante.creeper.server;

import org.apache.commons.lang3.text.WordUtils;

public class ASCIIArt {

    public static Integer GLOBAL_TERMINAL_WIDTH = 79;

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
        return wrap(s, GLOBAL_TERMINAL_WIDTH);
    }

    public static String wrap(String s, int terminalWidth) {
       return WordUtils.wrap(s, terminalWidth, "\r\n", true);
    }

    public static String asciiColorPattern = "\u001B\\[[;\\d]*m";

    public static String centerOnWidth(String s, int size, String repeatedChar) {
        String str = s.replaceAll(asciiColorPattern, "");
        int left = (size - str.length()) / 2;
        int right = size - left - str.length();
        StringBuffer buff = new StringBuffer();
        for (int i = 0; i < left; i++) {
            buff.append(repeatedChar);
        }
        buff.append(s);
        for (int i = 0; i < right; i++) {
            buff.append(repeatedChar);
        }
        return buff.toString();
    }

    public static String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }
}
