package terminal.ui;

import java.awt.Color;
import java.util.ArrayList;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

/**
 * ANSI is a helper class that will process and create a
 * SimpleAttributeSet that has the color settings from a given String and
 * return it to the calling function. This is used to allow the DragonConsole
 * compatability with ANSI.
 *
 * This method will also convert DragonConsole color codes into ANSI compatible
 * codes (although if no compatible color can be found it will use the default
 * colors.
 * @author Brandon E Buck
 */
public class ANSI {
    public static final String ESCAPE = "\033"; // ANSI Escape Character that starts commands

    public static final Color BLACK = Color.BLACK;
    public static final Color RED = Color.RED.darker();
    public static final Color GREEN = Color.GREEN.darker();
    public static final Color YELLOW = Color.YELLOW.darker();
    public static final Color BLUE = new Color(66, 66, 255).darker();
    public static final Color MAGENTA = Color.MAGENTA.darker();
    public static final Color CYAN = Color.CYAN.darker();
    public static final Color WHITE = Color.GRAY.brighter();

    public static final Color INTENSE_BLACK = Color.GRAY.darker();
    public static final Color INTENSE_RED = Color.RED;
    public static final Color INTENSE_GREEN = Color.GREEN;
    public static final Color INTENSE_YELLOW = Color.YELLOW;
    public static final Color INTENSE_BLUE = new Color(66, 66, 255);
    public static final Color INTENSE_MAGENTA = Color.MAGENTA;
    public static final Color INTENSE_CYAN = Color.CYAN;
    public static final Color INTENSE_WHITE = Color.WHITE;

    private static final Color normal[] = {BLACK, RED, GREEN, YELLOW, BLUE,
            MAGENTA, CYAN, WHITE};
    private static final Color bright[] = {INTENSE_BLACK, INTENSE_RED,
            INTENSE_GREEN, INTENSE_YELLOW, INTENSE_BLUE, INTENSE_MAGENTA,
            INTENSE_CYAN, INTENSE_WHITE};

    /**
     * Takes an ANSI Code as a String and breaks it apart and then creates a
     * SimpleAttributeSet with the proper Foreground and Background color.
     * @param old The current ANSI Style so any changes not specified are
     *  carried over.
     * @param string The String containing the ANSI code that needs to be
     *  processed.
     * @param defaultStyle The Default Style for the Console, used for ANSI
     *  codes 39 and 49.
     * @return Returns the SimpleAttributeSet with the proper colors of the
     *  ANSI code.
     */
    public static SimpleAttributeSet getANSIAttribute(SimpleAttributeSet old,
                                                      String string, Style defaultStyle) {
        SimpleAttributeSet ANSI = old;

        if (ANSI == null)
            ANSI = new SimpleAttributeSet();

        if (string.length() > 3) {
            string = string.substring(2); // Cut off the "\033[";
            string = string.substring(0, string.length() - 1); // Remove the "m" from the end

        } else
            return null;

        String codes[] = string.split(";");

        boolean brighter = false;
        for (int i = 0; i < codes.length; i++) {

            if (codes[i].matches("[\\d]*")) {
                int code = Integer.parseInt(codes[i]);

                switch (code) {
                    case 0:
                        brighter = false;
                        ANSI = new SimpleAttributeSet();
                        break;
                    case 1:
                        brighter = true;
                        break;
                    case 30:
                    case 31:
                    case 32:
                    case 33:
                    case 34:
                    case 35:
                    case 36:
                    case 37:
                        StyleConstants.setForeground(ANSI, getColorFromANSICode(code, brighter));
                        break;
                    case 39:
                        StyleConstants.setForeground(ANSI, StyleConstants.getForeground(defaultStyle));
                        break;
                    case 40:
                    case 41:
                    case 42:
                    case 43:
                    case 44:
                    case 45:
                    case 46:
                    case 47:
                        StyleConstants.setBackground(ANSI, getColorFromANSICode(code, false));
                        break;
                    case 49:
                        StyleConstants.setBackground(ANSI, StyleConstants.getBackground(defaultStyle));
                        break;
                }
            }
        }

        return ANSI;
    }


    /**
     * This method returns the Color associated with the given ANSI Code, it
     * also takes a boolean to test for color intensity. Returns null if
     * no color is found.
     * @param code The ANSI Code the color is requested for.
     * @param brighter <code>true</code> if intensity is set to bold or
     *  <code>false</code> if not.
     * @return Returns the Color associated with the ANSI code or null if none
     *  is found.
     */
    private static Color getColorFromANSICode(int code, boolean brighter) {
        switch (code) {
            case 30:
            case 40:
                if (brighter)
                    return INTENSE_BLACK;
                else
                    return BLACK;
            case 31:
            case 41:
                if (brighter)
                    return INTENSE_RED;
                else
                    return RED;
            case 32:
            case 42:
                if (brighter)
                    return INTENSE_GREEN;
                else
                    return GREEN;
            case 33:
            case 43:
                if (brighter)
                    return INTENSE_YELLOW;
                else
                    return YELLOW;
            case 34:
            case 44:
                if (brighter)
                    return INTENSE_BLUE;
                else
                    return BLUE;
            case 35:
            case 45:
                if (brighter)
                    return INTENSE_MAGENTA;
                else
                    return MAGENTA;
            case 36:
            case 46:
                if (brighter)
                    return INTENSE_CYAN;
                else
                    return CYAN;
            case 37:
            case 47:
                if (brighter)
                    return INTENSE_WHITE;
                else
                    return WHITE;
            default:
                return null;
        }
    }
}
