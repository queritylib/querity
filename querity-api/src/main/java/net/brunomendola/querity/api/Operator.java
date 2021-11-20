package net.brunomendola.querity.api;

import lombok.Getter;

public enum Operator {
  EQUALS(1),
  NOT_EQUALS(1),
  STARTS_WITH(1),
  ENDS_WITH(1),
  CONTAINS(1),
  IS_NULL(0),
  IS_NOT_NULL(0);

  @Getter
  private final int requiredValuesCount;

  Operator(int requiredValuesCount) {
    this.requiredValuesCount = requiredValuesCount;
  }
}
