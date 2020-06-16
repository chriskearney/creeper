package com.comandante.creeper.server;

import com.comandante.creeper.Creeper;
import com.comandante.creeper.common.CreeperUtils;
import com.comandante.creeper.server.player_communication.Color;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.regex.Pattern;

import static com.comandante.creeper.server.ASCIIArt.wrap;
import static com.comandante.creeper.server.player_communication.Color.RESET;

public class ServerWelcomeScreen {


    public static String getServerWelcomeScreen() {
        String creeper = Color.BOLD_ON + Color.GREEN + "Creeper" + RESET;

        String loreumIpsom = "Qui acu munere labore, vel et nominati evertitur, sed melius tincidunt ei. Nec alia detraxit an, purto vidisse ullamcorper pri et. In nibh impedit sea, justo equidem lucilius at cum, consul deserunt inimicus usu ad. Legimus voluptatum sit ut, magna justo cu sed. Pri suscipit consulatu eu, probo molestie te eam. Nam eu equidem detracto, eos an scripta fabellas, nec te facer mundi simul. Euismod maiestatis at mea, eam tempor constituto sententiae ex.\n" +
                "\n" +
                "Mea ex oratio iisque, phaedrum reprehendunt te cum. Hinc tota quodsi est no, quas vidit scriptorem et pri. Quo no delenit eloquentiam. Has dicat tantas petentium in, virtute contentiones eu pro, ad sed vivendum democritum. Audire nusquam referrentur vim cu, vis ea eruditi postulant, mel putant equidem inimicus cu. No vis putent graecis dissentiet.\n" +
                "\n" +
                "Dolores ponderum accusamus nam cu. Duo velit debet dissentiunt no, nec an nisl tempor. Usu at virtute hendrerit aliquando, nec novum ignota eu. Ne eos delenit fierent. Cu sed integre mnesarchum, nec harum virtute tincidunt an, has at molestie perfecto.";

        StringBuilder sb = new StringBuilder();

        String version = "(version: " + Creeper.getCreeperVersion() + ")";

        int adjustment = ASCIIArt.GLOBAL_TERMINAL_WIDTH - maxLineLength(ASCIIArt.VERTICAL_SWORD_WITH_EMITTING_ORA) - 1;

        String centeredCreeper = ASCIIArt.centerOnWidth(creeper, adjustment, " ");
        String centeredVersion = ASCIIArt.centerOnWidth(version, adjustment, " ");
        sb.append(centeredCreeper).append("\r\n").append(centeredVersion).append("\r\n").append("\r\n");
        sb.append(wrap(loreumIpsom, adjustment));
        return CreeperUtils.printStringsNextToEachOther(Lists.newArrayList(ASCIIArt.VERTICAL_SWORD_WITH_EMITTING_ORA, sb.toString()), " ");

    }

    private static int maxLineLength(String s) {
        return CreeperUtils.getLongestStringLength(Lists.newArrayList(Splitter.on(Pattern.compile("\r?\n")).split(s).iterator()));
    }

    public static void main(String[] args) {

        ServerWelcomeScreen serverWelcomeScreen = new ServerWelcomeScreen();
        System.out.println("\r\n");
        System.out.println("\r\n");

        System.out.println("\r\n");

        System.out.println("\r\n");
        System.out.println("\r\n");
        System.out.println("\r\n");
        System.out.println("\r\n");
        System.out.println("\r\n");
        System.out.println("\r\n");

        System.out.println(serverWelcomeScreen.getServerWelcomeScreen());

        System.out.printf("DONE");
    }

}
