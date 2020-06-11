package terminal.ui.settings;


import terminal.TtyConnector;

import javax.swing.*;

/**
 * @author traff
 */
public interface TabbedSettingsProvider extends SettingsProvider {
  boolean shouldCloseTabOnLogout(TtyConnector ttyConnector);

  String tabName(TtyConnector ttyConnector, String sessionName);

  KeyStroke[] getNextTabKeyStrokes();

  KeyStroke[] getPreviousTabKeyStrokes();
}
