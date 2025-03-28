package io.github.queritylib.querity.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

public class OrConditionsWrapper extends LogicConditionsWrapper {
  @Builder(toBuilder = true)
  @Jacksonized
  public OrConditionsWrapper(List<Condition> conditions) {
    super(LogicOperator.OR, conditions);
  }

  @Override
  @JsonProperty("or")
  public @NonNull List<Condition> getConditions() {
    return super.getConditions();
  }
}
