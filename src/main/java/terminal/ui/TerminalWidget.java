package terminal.ui;


import terminal.TerminalDisplay;
import terminal.TtyConnector;
import terminal.ui.TerminalPanelListener;
import terminal.ui.TerminalSession;

import javax.swing.*;
import java.awt.*;

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
