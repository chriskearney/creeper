package terminal;

import terminal.emulator.mouse.MouseMode;
import terminal.model.JediTerminal;
import terminal.model.TerminalSelection;

import java.awt.*;

public interface TerminalDisplay {
  // Size information
  int getRowCount();

  int getColumnCount();

  void setCursor(int x, int y);

  void beep();

  Dimension requestResize(Dimension pendingResize, RequestOrigin origin, int cursorY, JediTerminal.ResizeHandler resizeHandler);

  void scrollArea(final int scrollRegionTop, final int scrollRegionSize, int dy);

  void setCursorVisible(boolean shouldDrawCursor);

  void setScrollingEnabled(boolean enabled);

  void setBlinkingCursor(boolean enabled);

  void setWindowTitle(String name);

  void setCurrentPath(String path);

  void terminalMouseModeSet(MouseMode mode);

  TerminalSelection getSelection();
  
  boolean ambiguousCharsAreDoubleWidth();
}
