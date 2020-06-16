package com.terminal;

import com.terminal.emulator.mouse.MouseMode;
import com.terminal.model.JediTerminal;
import com.terminal.model.TerminalSelection;

import java.awt.Dimension;

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
