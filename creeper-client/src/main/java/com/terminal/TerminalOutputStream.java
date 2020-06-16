package com.terminal;

/**
 *  Sends a response from the com.terminal emulator.
 * 
 * @author traff
 */
public interface TerminalOutputStream {
  void sendBytes(byte[] response);

  void sendString(final String string);
}
