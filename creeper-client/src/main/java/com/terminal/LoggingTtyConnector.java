package com.terminal;

import java.util.List;

/**
 * @author traff
 */
public interface LoggingTtyConnector {
  List<char[]> getChunks();
}
