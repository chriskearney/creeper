package terminal.ui;

import static java.lang.Math.pow;
import static java.lang.StrictMath.sqrt;

public class Levels {

    private static double CONSTANT_MODIFIER = 0.02;

    public static long getLevel(long experience) {
        double v = CONSTANT_MODIFIER * sqrt(experience);
        return (long) Math.floor(v);
    }

    public static long getXp(long level) {
        double v = pow(level, 2) / pow(CONSTANT_MODIFIER, 2);
        return (long) Math.ceil(v);
    }

}