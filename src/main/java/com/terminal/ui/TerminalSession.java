package com.terminal.ui;

import com.terminal.Terminal;
import com.terminal.TtyConnector;
import com.terminal.debug.DebugBufferType;
import com.terminal.model.TerminalTextBuffer;

/**
 * @author traff
 */
public interface TerminalSession {
  void start();

  String getBufferText(DebugBufferType type);

  TerminalTextBuffer getTerminalTextBuffer();

  Terminal getTerminal();

  TtyConnector getTtyConnector();

  String getSessionName();

  void close();
}
