package io.github.queritylib.querity.api;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Getter
@EqualsAndHashCode
@ToString
public class SimpleCondition implements Condition {
  @NonNull
  private final String propertyName;
  @NonNull
  private Operator operator = Operator.EQUALS;
  private final Object value;

  @Builder(toBuilder = true)
  @Jacksonized
  public SimpleCondition(@NonNull String propertyName, Operator operator, Object value) {
    this.propertyName = propertyName;
    if (operator != null)
      this.operator = operator;
    this.value = value;
    validate(this.operator, this.value);
  }

  private void validate(Operator operator, Object value) {
    if (operator.getRequiredValuesCount() != getValuesCount(value))
      throw new IllegalArgumentException(
          String.format("The operator %s requires %d value(s)", operator, operator.getRequiredValuesCount()));
  }

  private int getValuesCount(Object value) {
    return value == null ? 0 : 1;
  }
}
