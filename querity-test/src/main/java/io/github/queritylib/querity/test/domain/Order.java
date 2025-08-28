package io.github.queritylib.querity.test.domain;

import java.util.List;
import java.util.UUID;

public interface Order<I extends OrderItem> {
  Short getYear();

  void setYear(Short year);

  Integer getNumber();

  void setNumber(Integer number);

  List<I> getItems();

  void setItems(List<I> items);

  UUID getExternalId();

  void setExternalId(UUID externalId);
}
