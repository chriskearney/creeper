package terminal.ui;

import terminal.Terminal;
import terminal.TtyConnector;
import terminal.debug.DebugBufferType;
import terminal.model.TerminalTextBuffer;

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
