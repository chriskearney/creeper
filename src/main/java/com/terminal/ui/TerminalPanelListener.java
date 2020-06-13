package com.terminal.ui;

import com.terminal.RequestOrigin;

import java.awt.Dimension;


public interface TerminalPanelListener {
  void onPanelResize(Dimension pixelDimension, RequestOrigin origin);

  void onSessionChanged(TerminalSession currentSession);

  void onTitleChanged(String title);
}
