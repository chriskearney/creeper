package terminal.ui.settings;

import terminal.HyperlinkStyle;
import terminal.TextStyle;
import terminal.emulator.ColorPalette;

import java.awt.*;

public interface UserSettingsProvider {
  ColorPalette getTerminalColorPalette();

  Font getTerminalFont();

  float getTerminalFontSize();

  float getLineSpace();

  TextStyle getDefaultStyle();

  TextStyle getSelectionColor();

  TextStyle getFoundPatternColor();

  TextStyle getHyperlinkColor();

  HyperlinkStyle.HighlightMode getHyperlinkHighlightingMode();

  boolean useInverseSelectionColor();

  boolean copyOnSelect();

  boolean pasteOnMiddleMouseClick();

  boolean emulateX11CopyPaste();

  boolean useAntialiasing();

  int maxRefreshRate();

  boolean audibleBell();

  boolean enableMouseReporting();

  int caretBlinkingMs();

  boolean scrollToBottomOnTyping();

  boolean DECCompatibilityMode();

  boolean forceActionOnMouseReporting();

  int getBufferMaxLinesCount();
  
  boolean altSendsEscape();

  boolean ambiguousCharsAreDoubleWidth();
}
