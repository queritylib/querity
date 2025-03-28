package io.github.queritylib.querity.test.domain;

import java.util.List;

public interface Order<I extends OrderItem> {
  Short getYear();

  void setYear(Short year);

  Integer getNumber();

  void setNumber(Integer number);

  List<I> getItems();

  void setItems(List<I> items);
}
