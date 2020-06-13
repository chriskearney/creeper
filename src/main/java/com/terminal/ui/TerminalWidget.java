package com.terminal.ui;


import com.terminal.TerminalDisplay;
import com.terminal.TtyConnector;

import javax.swing.JComponent;
import java.awt.Dimension;

/**
 * @author traff
 */
public interface TerminalWidget {
  TerminalSession createTerminalSession(TtyConnector ttyConnector);

  JComponent getComponent();

  boolean canOpenSession();

  void setTerminalPanelListener(TerminalPanelListener terminalPanelListener);

  Dimension getPreferredSize();

  TerminalSession getCurrentSession();

  TerminalDisplay getTerminalDisplay();
}
