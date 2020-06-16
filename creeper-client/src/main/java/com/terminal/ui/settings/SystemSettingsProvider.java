package com.terminal.ui.settings;

import javax.swing.KeyStroke;

public interface SystemSettingsProvider {
  KeyStroke[] getCopyKeyStrokes();

  KeyStroke[] getPasteKeyStrokes();

  KeyStroke[] getClearBufferKeyStrokes();

  KeyStroke[] getNewSessionKeyStrokes();

  KeyStroke[] getCloseSessionKeyStrokes();

  KeyStroke[] getFindKeyStrokes();

  KeyStroke[] getPageUpKeyStrokes();

  KeyStroke[] getPageDownKeyStrokes();
}
