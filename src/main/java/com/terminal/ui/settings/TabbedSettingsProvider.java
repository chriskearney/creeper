package com.terminal.ui.settings;


import com.terminal.TtyConnector;

import javax.swing.KeyStroke;

/**
 * @author traff
 */
public interface TabbedSettingsProvider extends SettingsProvider {
  boolean shouldCloseTabOnLogout(TtyConnector ttyConnector);

  String tabName(TtyConnector ttyConnector, String sessionName);

  KeyStroke[] getNextTabKeyStrokes();

  KeyStroke[] getPreviousTabKeyStrokes();
}
