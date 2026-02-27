package io.github.queritylib.querity.test.domain;

public interface IdDocument<T extends DocumentType> {
  T getType();

  void setType(T type);

  String getNumber();

  void setNumber(String number);
}
