package com.terminal;

public interface Questioner {
  String questionVisible(String question, String defValue);

  String questionHidden(String string);

  void showMessage(String message);
}
